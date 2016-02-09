#include "pcd_widget.h"

char PCDWidget::actionMode = 'v';

static void qNormalizeAngle (int &angle) {
    while (angle < 0)
        angle += 360;
    while (angle > 360)
        angle -= 360;
}

static void qNormalizeAngle (float &angle) {
    while (angle < 0)
        angle += 360;
    while (angle > 360)
        angle -= 360;
}

/**
 * 
 * CONSTRUCTOR: initiates with a PCD and this will represent the reference to
 * the next PCD-s added to the widget.
 * 
 * Az elso ponthalmaz eseten, amit elojegyez kirajzolasra,
 * transzformalja a teret ugy, hogy kirajzolaskor kb. (50,50,50) pont
 * korul legyen, ez azert szukseges, hogy a GL kirajzolaskor ne a kamarat
 * kelljen a ponthoz illeszteni, hanem a ponthalmazt a kamerahoz, es azert
 * csak az elso eseten, mert akkor a tobbi ponthalmazt relative ehhez tudja
 * majd igazitani
 * @param cloud_ptr
 * @param color
 */
PCDWidget::PCDWidget (pcl::PointCloud<pcl::PointXYZRGBA>::Ptr cloud_ptr, GLuint color) {
    srand(time(0));
    setWindowTitle("OpenGL framebuffer objects");
    makeCurrent();

    // --------------------------- NORMALIZING ------------------------------ //

    pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_ptr.get()[0];

    pcl::PointXYZRGBA max;
    pcl::PointXYZRGBA min;

    min.x = min.y = min.z = (float)RAND_MAX;
    max.x = max.y = max.z = -(float)RAND_MAX;
    for (unsigned int i = 0; i < cloud.size(); ++i) {
        if (cloud[i].x > max.x) {
            max.x = cloud[i].x;
        }
        if (cloud[i].y > max.y) {
            max.y = cloud[i].y;
        }
        if (cloud[i].z > max.z) {
            max.z = cloud[i].z;
        }
        if (cloud[i].x < min.x) {
            min.x = cloud[i].x;
        }
        if (cloud[i].y < min.y) {
            min.y = cloud[i].y;
        }
        if (cloud[i].z < min.z) {
            min.z = cloud[i].z;
        }
    }
    _cloud_size = cloud.size();
    float divider = std::max(max.x - min.x, max.y - min.y);
    divider = std::max(divider, max.z - min.z);

    transform_params.min = min;
    transform_params.max = max;
    transform_params.div = divider;

    rot_x = rot_y = rot_z = 0.0f;
    pos_x = pos_y = 0.0f;
    scale = 0.1f;

    cloud_vector.push_back(cloud_ptr);
    normalize_pcd_fast_inaccurate(0);
    color_vector.push_back(color);
    enabled_vector.push_back(true);
    object_type.push_back(0);
    QVector<bool> tmp(cloud.size(), false);
    selected_vector.push_back(tmp);
    tile_list = glGenLists(1);
    view_changed = true;
    create_list();
    setGridEnabled(true);
}

void PCDWidget::addPointCloud (pcl::PointCloud<pcl::PointXYZRGBA>::Ptr cloud_ptr, GLuint color, int type) {

    int index = cloud_vector.size();

    cloud_vector.push_back(cloud_ptr); // cloud_vector
    normalize_pcd_fast_inaccurate(index);
    color_vector.push_back(color); // color_vector
    enabled_vector.push_back(true); // enabled_vector
    object_type.push_back(type); // object_type
    QVector<bool> tmp(cloud_ptr.get()[0].size(), false);
    selected_vector.push_back(tmp); // selected_vector
    glDeleteLists(tile_list, 1); // delete glLists
    view_changed = true;
    create_list(); // create list
}

void PCDWidget::addRoofs (QVector<pcl::PointCloud<pcl::PointXYZRGBA> >& roofs_vector_NONPTR, QVector<GLuint> color) {
    tile_list_roofs = glGenLists(1);
    glNewList(tile_list_roofs, GL_COMPILE);

    for (int i = 0; i < roofs_vector_NONPTR.size(); i++) {
        // transformation 1 = 10 m
        for (unsigned int j = 0; j < roofs_vector_NONPTR[i].size(); ++j) {
            roofs_vector_NONPTR[i][j].x = (roofs_vector_NONPTR[i][j].x - transform_params.max.x / 2 - transform_params.min.x / 2) / transform_params.div * 50;
            roofs_vector_NONPTR[i][j].y = (roofs_vector_NONPTR[i][j].y - transform_params.max.y / 2 - transform_params.min.y / 2) / transform_params.div * 50;
            roofs_vector_NONPTR[i][j].z = (roofs_vector_NONPTR[i][j].z - transform_params.max.z / 2 - transform_params.min.z / 2) / transform_params.div * 50;
        }
    }

    for (int i = 0; i < roofs_vector_NONPTR.size(); i++) {
        GLuint temp = color[i];
        GLubyte b = temp % 0x100;
        temp /= 0x100;
        GLubyte g = temp % 0x100;
        temp /= 0x100;
        GLubyte r = temp % 0x100;
        glBegin(GL_POLYGON);
        glColor3b(r, g, b);
        for (unsigned int j = 0; j < roofs_vector_NONPTR[i].size(); j++) {
            glVertex3f((GLfloat)roofs_vector_NONPTR[i][j].x, (GLfloat)roofs_vector_NONPTR[i][j].y, (GLfloat)roofs_vector_NONPTR[i][j].z);
        }
        glEnd();
    }
    glEndList();
    updateGL();
}

void PCDWidget::addRoofsPlane (QVector<pcl::PointCloud<pcl::PointXYZRGBA> >& roofs_vector_nonptr, QVector<GLuint> color) {

    // Placeholder for the 3x3 covariance matrix at each surface patch
    Eigen::Matrix3f covariance_matrix;
    // 16-bytes aligned placeholder for the XYZ centroid of a surface patch
    Eigen::Vector4f xyzt_centroid;

    tile_list_plane = glGenLists(1);
    glNewList(tile_list_plane, GL_COMPILE);
    {
        for (int i = 0; i < roofs_vector_nonptr.size(); i++) {
            // Estimate the XYZ centroid
            compute3DCentroid(roofs_vector_nonptr[i], xyzt_centroid);
            // Compute the 3x3 covariance matrix
            computeCovarianceMatrixNormalized(roofs_vector_nonptr[i], xyzt_centroid, covariance_matrix);

            Eigen::Vector3f centroid;
            centroid << xyzt_centroid.coeff(0), xyzt_centroid.coeff(1), xyzt_centroid.coeff(2);

            Eigen::SelfAdjointEigenSolver<Eigen::Matrix3f> eigensolver(covariance_matrix);
            if (eigensolver.info() != Eigen::Success) abort();

            Eigen::Vector3f eigen_values = eigensolver.eigenvalues();
            Eigen::Matrix3f eigen_vectors = eigensolver.eigenvectors();

            Eigen::Vector3f ox, oy, oz;
            oz << eigen_vectors.coeffRef(0, 0), eigen_vectors.coeffRef(1, 0), eigen_vectors.coeffRef(2, 0);
            oz = oz * pow(eigen_values.coeff(0), 0.5);

            oy << eigen_vectors.coeffRef(0, 1), eigen_vectors.coeffRef(1, 1), eigen_vectors.coeffRef(2, 1);
            oy = oy * pow(eigen_values.coeff(1), 0.5);

            ox << eigen_vectors.coeffRef(0, 2), eigen_vectors.coeffRef(1, 2), eigen_vectors.coeffRef(2, 2);
            ox = ox * pow(eigen_values.coeff(2), 0.5);

            // draw plane
            glBegin(GL_POLYGON);
            {
                GLuint temp = color[i];
                GLubyte b = temp % 0x100;
                temp /= 0x100;
                GLubyte g = temp % 0x100;
                temp /= 0x100;
                GLubyte r = temp % 0x100;
                glBegin(GL_POLYGON);
                glColor3b(r, g, b);
                glVertex3f(centroid.coeff(0) - ox.coeff(0) - oy.coeff(0), centroid.coeff(1) - ox.coeff(1) - oy.coeff(1), centroid.coeff(2) - ox.coeff(2) - oy.coeff(2));
                glVertex3f(centroid.coeff(0) - ox.coeff(0) + oy.coeff(0), centroid.coeff(1) - ox.coeff(1) + oy.coeff(1), centroid.coeff(2) - ox.coeff(2) + oy.coeff(2));
                glVertex3f(centroid.coeff(0) + ox.coeff(0) + oy.coeff(0), centroid.coeff(1) + ox.coeff(1) + oy.coeff(1), centroid.coeff(2) + ox.coeff(2) + oy.coeff(2));
                glVertex3f(centroid.coeff(0) + ox.coeff(0) - oy.coeff(0), centroid.coeff(1) + ox.coeff(1) - oy.coeff(1), centroid.coeff(2) + ox.coeff(2) - oy.coeff(2));

            }
            glEnd();

            // draw centroid
            glBegin(GL_POLYGON);
            {
                glColor3f(1.0, 0, 0);
                glVertex3f(centroid.coeff(0), centroid.coeff(1), centroid.coeff(2));
                glVertex3f(centroid.coeff(0) + 0.1, centroid.coeff(1) + 0.1, centroid.coeff(2) + 0.1);
                glVertex3f(centroid.coeff(0) - 0.1, centroid.coeff(1) + 0.1, centroid.coeff(2) - 0.1);
                glVertex3f(centroid.coeff(0) + 0.1, centroid.coeff(1) - 0.1, centroid.coeff(2) - 0.1);
                glVertex3f(centroid.coeff(0) - 0.1, centroid.coeff(1) - 0.1, centroid.coeff(2) + 0.1);
                glVertex3f(centroid.coeff(0) - 0.1, centroid.coeff(1) + 0.1, centroid.coeff(2) + 0.1);

            }
            glEnd();
        }
    }
    glEndList();
}

void PCDWidget::planeFitting (int index) {

    // Placeholder for the 3x3 covariance matrix at each surface patch
    Eigen::Matrix3f covariance_matrix;
    // 16-bytes aligned placeholder for the XYZ centroid of a surface patch
    Eigen::Vector4f xyzt_centroid;
    // Estimate the XYZ centroid
    compute3DCentroid(cloud_vector[0].get()[0], xyzt_centroid);
    // Compute the 3x3 covariance matrix
    computeCovarianceMatrix(cloud_vector[index].get()[0], xyzt_centroid, covariance_matrix);

    Eigen::Vector3f centroid;
    centroid << xyzt_centroid.coeff(0), xyzt_centroid.coeff(1), xyzt_centroid.coeff(2);

    Eigen::SelfAdjointEigenSolver<Eigen::Matrix3f> eigensolver(covariance_matrix);
    if (eigensolver.info() != Eigen::Success) abort();

    Eigen::Vector3f eigen_values = eigensolver.eigenvalues();
    Eigen::Matrix3f eigen_vectors = eigensolver.eigenvectors();

    Eigen::Vector3f ox, oy, oz;
    oz << eigen_vectors.coeffRef(0, 0), eigen_vectors.coeffRef(1, 0), eigen_vectors.coeffRef(2, 0);
    oz = oz * pow(eigen_values.coeff(0), 0.5) / 10;

    oy << eigen_vectors.coeffRef(0, 1), eigen_vectors.coeffRef(1, 1), eigen_vectors.coeffRef(2, 1);
    oy = oy * pow(eigen_values.coeff(1), 0.5) / 10;

    ox << eigen_vectors.coeffRef(0, 2), eigen_vectors.coeffRef(1, 2), eigen_vectors.coeffRef(2, 2);
    ox = ox * pow(eigen_values.coeff(2), 0.5) / 10;

    tile_list_plane = glGenLists(1);
    glNewList(tile_list_plane, GL_COMPILE);
    glBegin(GL_POLYGON);
    {
        glColor3b(255, 26, 255);
        glVertex3f(centroid.coeff(0) - ox.coeff(0) - oy.coeff(0), centroid.coeff(1) - ox.coeff(1) - oy.coeff(1), centroid.coeff(2) - ox.coeff(2) - oy.coeff(2));
        glVertex3f(centroid.coeff(0) - ox.coeff(0) + oy.coeff(0), centroid.coeff(1) - ox.coeff(1) + oy.coeff(1), centroid.coeff(2) - ox.coeff(2) + oy.coeff(2));
        glVertex3f(centroid.coeff(0) + ox.coeff(0) + oy.coeff(0), centroid.coeff(1) + ox.coeff(1) + oy.coeff(1), centroid.coeff(2) + ox.coeff(2) + oy.coeff(2));
        glVertex3f(centroid.coeff(0) + ox.coeff(0) - oy.coeff(0), centroid.coeff(1) + ox.coeff(1) - oy.coeff(1), centroid.coeff(2) + ox.coeff(2) - oy.coeff(2));
    }
    glEnd();
    glEndList();
}

void PCDWidget::planeIntersection () {
    using namespace CGAL;

    // ne jelenitse meg azt amit nem kell
    glDeleteLists(tile_list, 1);

    // first plane
    Kernel::RT a1(1);
    Kernel::RT b1(1);
    Kernel::RT c1(1);
    Kernel::RT d1(-1);
    Plane_3<Kernel> h1(a1, b1, c1, d1);

    // second plane
    Kernel::RT a2(-1);
    Kernel::RT b2(1);
    Kernel::RT c2(1);
    Kernel::RT d2(-1);
    Plane_3<Kernel> h2(a2, b2, c2, d2);

    // centroid of the two planes
    Point_3<Kernel> center1 = to_3d(h1, Point_2<Kernel > (0, 0));
    Point_3<Kernel> center2 = to_3d(h2, Point_2<Kernel > (0, 0));

    // calculation of intersection
    Object line_wrapper = intersection<Kernel > (h1, h2);
    //    std::cout << line_wrapper.type().name() << std::endl;
    Line_3<Kernel> line = object_cast<Line_3<Kernel> >(line_wrapper);

    Point_3<Kernel> p1 = line.point(10);
    Point_3<Kernel> p2 = line.point(-10);

    Point_2<Kernel> point_2(0, 0);
    Point_3<Kernel> point_3 = to_3d(h1, point_2, PLANE_XY);
    std::cout << "sajat to_3d: " << point_3 << std::endl;


    // Line_3 intersection Line_3
    typedef Vector_3<Kernel> VEC;

    VEC P(0, 0, 0), Q(3, 0, 0), m(1, 1, 1), n(0, 0, 1);
    m = m / sqrt(m * m);
    std::cout << m << " " << m*m << std::endl;
    double t1 = -10, t2 = 10;

    double A = -2 * m * n;
    double B = 2 * (P - Q) * m;
    double C = -2 * (P - Q) * n;
    double D = (P - Q) * (P - Q);

    std::cout << A << " " << B << " " << C << " " << D << " " << std::endl;

    double u, v;
    if (abs(A) < 0.001) {
        //        u = 
    } else {
        v = (2 * C - A * B) / (A * A - 4);
        u = (2 * B - A * C) / (A * A - 4);
    }

    VEC tmp1 = P + m*u;
    VEC tmp2 = Q + n*v;

    std::cout << "tavolsag: = " << pow((tmp1-tmp2)*(tmp1-tmp2),0.5) << std::endl;
    
    VEC I = (tmp1 + tmp2) / 2;

    grid_enabled = 0;

    // create the list
    tile_list_intersection = glGenLists(1);
    glNewList(tile_list_intersection, GL_COMPILE);
    glBegin(GL_QUADS);
    {

    }
    glEnd();
    glBegin(GL_LINES);
    {
        glLineWidth(4.0);
        glColor3f(0.0,0.5,0.9);
        glVertex3f((Q + n * t1)[0], (Q + n * t1)[1], (Q + n * t1)[2]);
        glVertex3f((Q + n * t2)[0], (Q + n * t2)[1], (Q + n * t2)[2]);
        glVertex3f((P + m * t1)[0], (P + m * t1)[1], (P + m * t1)[2]);
        glVertex3f((P + m * t2)[0], (P + m * t2)[1], (P + m * t2)[2]);

        glColor3f(1.0, 0.0, 0.0);
        glVertex3f(tmp1[0], tmp1[1], tmp1[2]);
        glVertex3f(tmp2[0], tmp2[1], tmp2[2]);

        //        glVertex3f(CGAL::to_double(p1.x()), CGAL::to_double(p1.y()), CGAL::to_double(p1.z()));
        //        glVertex3f(CGAL::to_double(p2.x()), CGAL::to_double(p2.y()), CGAL::to_double(p2.z()));

        //        std::cout << "\n\n\n" << CGAL::to_double(p1.x()) << " " << CGAL::to_double(p1.y()) << " " << CGAL::to_double(p1.z()) << "\n\n\n\n";
        //        std::cout << "\n\n\n" << CGAL::to_double(p2.x()) << " " << CGAL::to_double(p2.y()) << " " << CGAL::to_double(p2.z()) << "\n\n\n\n";
    }
    glEnd();
    glBegin(GL_POINTS);
    glColor3f(1,1,1);
    glVertex3f(I[0], I[1], I[2]);
    //    glVertex3f(CGAL::to_double(P.x()), CGAL::to_double(P.y()), CGAL::to_double(P.z()));
    glEnd();
    glEndList();
}

/**
 * DEMO CONSTRUCTOR: constructs a DEMO 3D image, and visualizes it.
 * @param parent
 */
PCDWidget::PCDWidget (QWidget *parent) : QGLWidget (QGLFormat (QGL::SampleBuffers | QGL::AlphaChannel), parent) {
    srand(time(0));
    makeCurrent();

    rot_x = rot_y = rot_z = 0.0f;
    pos_x = pos_y = 0.0f;
    scale = 0.1f;

    tile_list = glGenLists(1);
    glNewList(tile_list, GL_COMPILE);
    glBegin(GL_TRIANGLES);
    {
        for (int i = 0; i < 10; ++i) {
            glColor3b((rand() * i) % 256, (rand() - i) % 256, (rand() + i) % 256);
            glVertex3f((GLfloat)rand() / (GLfloat)RAND_MAX * 100 - 50, (GLfloat)rand() / (GLfloat)RAND_MAX * 100 - 50, (GLfloat)rand() / (GLfloat)RAND_MAX * 100 - 50);
        }
    }
    glEnd();
    //    glBegin(GL_POINTS);
    //    {
    //        for (int i = 0; i < 100000; ++i) {
    //            glColor3b((rand() * i) % 256, (rand() - i) % 256, (rand() + i) % 256);
    //            glVertex3f((GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50, (GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50, (GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50);
    //        }
    //    }
    //    glEnd();
    //    glBegin(GL_LINES);
    //    {
    //        for (int i = 0; i < 1000; ++i) {
    //            glColor3b(rand() * i % 256, rand() - i % 256, rand() + i % 256);
    //            glVertex3f((GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50, (GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50, (GLfloat) rand() / (GLfloat) RAND_MAX * 100 - 50);
    //        }
    //    }
    //    glEnd();
    glEndList();
}

PCDWidget::~PCDWidget () {
    glDeleteLists(tile_list, 1);
}

void PCDWidget::paintEvent (QPaintEvent *) {
    updateGL();
}

void PCDWidget::initializeGL () {
    glClearColor(0, 0, 0, 0);

    // GYAKORLATILAG EZ A NEHANY KOMMAND TESZI LEHETOVE A 3D-S VIZIOT
    glEnable(GL_DEPTH_TEST);
    glClearStencil(0x0);
    glEnable(GL_STENCIL_TEST);

}

void PCDWidget::paintGL () {
    // draw into the GL widget
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // ez arra szolgal, hogy ami a szemmel lathato a lyukon keresztul, az legyen is ott, ne vagja le, ami nem eleme (left,right)
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    projectionMatrix = glm::frustum(-((float)width()) / 2000.f, ((float)width()) / 2000.f, -((float)height()) / 2000.f, ((float)height()) / 2000.f, 20.f, 400.f);
    // itt lehet intezni a kamera allasat, FIGYELEM a ketto erosen osszefugg!!!!!!!
    glFrustum(-((float)width()) / 2000.f, ((float)width()) / 2000.f, -((float)height()) / 2000.f, ((float)height()) / 2000.f, 20, 400);
    glTranslatef(pos_x, pos_y, -180.0f);


    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glViewport(0, 0, width(), height());
    glEnable(GL_BLEND);

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    viewMatrix = glm::translate(glm::mat4(1.0f), glm::vec3((float)pos_x, (float)pos_y, -180.f)); // Create our view matrix which will translate us back 5 units

    glScalef(scale, scale, scale);
    glRotatef(rot_x, 1.0f, 0.0f, 0.0f);
    glRotatef(rot_y, 0.0f, 1.0f, 0.0f);
    glRotatef(rot_z, 0.0f, 0.0f, 1.0f);

    modelMatrix = glm::scale(glm::mat4(1.0f), glm::vec3((float)scale));
    modelMatrix = glm::rotate(modelMatrix, (float)rot_x, glm::vec3(1.f, 0.f, 0.f));
    modelMatrix = glm::rotate(modelMatrix, (float)rot_y, glm::vec3(0.f, 1.f, 0.f));
    modelMatrix = glm::rotate(modelMatrix, (float)rot_z, glm::vec3(0.f, 0.f, 1.f));

    // EZEK RONTOTTAK EL A 3D-S VIZIOT
    //    glDepthFunc(GL_ALWAYS);
    //    glEnable(GL_LESS);

    glPointSize(1);

    if (actionMode == 'e' && cloud_changed) create_list();

    if (grid_enabled) {
        paintBasePlain();
    }

    glCallList(tile_list_intersection);
    glCallList(tile_list_plane);
    glCallList(tile_list);
    glCallList(tile_list_roofs);

    if (actionMode == 'e') {
        /* Képernyőtérkép a kiválasztáshoz*/
        screenmap.clear();
        const QVector<QVector<PositionPair> > tmp2(height());
        for (int i = 0; i < width(); i++) screenmap.push_back(tmp2);

        int tmp_x, tmp_y;
        int tmp_halfwidth = width() / 2;
        int tmp_halfheight = height() / 2;
        //    std::cout<<"Screenmap konstruktor lefut"<<std::endl;
        //    std::cout<<"cím1:"<<&screenmap<<' '<<screenmap.size()<<std::endl;
        //    std::cout<<"cím2:"<<&screenmap[0]<<' '<<screenmap[0].size()<<std::endl;
        //    std::cout<<"cím3:"<<&screenmap[0][0]<<' '<<screenmap[0][0].size()<<std::endl;
        for (int index = 0; index < color_vector.size(); index++) {
            if (object_type[index] == 0) {
                pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_vector[index].get()[0];
                for (unsigned int i = 0; i < cloud.size(); ++i) {
                    glm::vec4 act_pos = glm::vec4((float)cloud[i].x, (float)cloud[i].y, (float)cloud[i].z, 1.f);
                    act_pos = projectionMatrix * viewMatrix * modelMatrix * act_pos;
                    if (1.f + act_pos[0] / act_pos[3] >= 0.f && 1.f - act_pos[1] / act_pos[3] >= 0.f && (1.f + act_pos[0] / act_pos[3]) < 2.f && (1.f - act_pos[1] / act_pos[3]) < 2.f) {
                        tmp_x = (1.f + act_pos[0] / act_pos[3]) * tmp_halfwidth;
                        tmp_y = (1.f - act_pos[1] / act_pos[3]) * tmp_halfheight;
                        const PositionPair tmp_pp(index, i);
                        //                    std::cout<<tmp_x<<' '<<tmp_y<<std::endl;
                        screenmap[tmp_x][tmp_y].push_back(tmp_pp);
                        //                    std::cout<<"elem"<<std::endl;
                    }
                }
            }
        }

        /* Képernyőtérkép vége*/

        /*Transzformáció helyesség tesztelése*/
        //    glm::vec4 probar = glm::vec4(-10.f, 10.f, 1.f, 1.f);
        //    probar = projectionMatrix * viewMatrix * modelMatrix * probar;
        //    std::cout <<"red: "<<((1.f+probar[0]/probar[3])*width()/2)<<'/'<<width()<<" "<<((1.f-probar[1]/probar[3])*height()/2)<<'/'<<height()<<std::endl;
        //        
        ////    glm::vec4 probag = glm::vec4(-20.f, 10.f, 1.f, 1.f);
        ////    probag = projectionMatrix * viewMatrix * modelMatrix * probag;
        ////    std::cout <<" green: ("<<(probag[0]/probag[3])<<','<<(probag[1]/probag[3])<<','<<(probag[2]/probag[3])<<')';
        ////    glm::vec4 probab = glm::vec4(-30.f, 10.f, 1.f, 1.f);
        ////    probab = projectionMatrix * viewMatrix * modelMatrix * probab;
        ////    std::cout <<" blue: ("<<(probab[0]/probab[3])<<','<<(probab[1]/probab[3])<<','<<(probab[2]/probab[3])<<')'<<std::endl;
        //    
        //    
        //    glBegin(GL_POINTS);
        //    {
        //        glColor4f(1.f, 0.f, 0.f, 1.f);
        //        glVertex3f(-10.f, 10.f, 1.f);
        ////        glColor4f(0.f, 1.f, 0.f, 1.f);
        ////        glVertex3f(-20.f, 10.f, 1.f);
        ////        glColor4f(0.f, 0.f, 1.f, 1.f);
        ////        glVertex3f(-30.f, 10.f, 1.f);
        //    }
        //    glEnd();
        /*Helyesség tesztelésének vége*/
    }
    update();
}

void PCDWidget::paintBasePlain () {
    glBegin(GL_LINES);
    {
        glLineWidth(1.0);
        glColor4f(0.7f, 0.7f, 0.7f, 0.2f);
        for (int i = -100; i <= 100; i += 10) {
            glVertex3i(i, -100, 0);
            glVertex3i(i, 100, 0);
            glVertex3i(-100, i, 0);
            glVertex3i(100, i, 0);
        }
    }
    glEnd();
}

void PCDWidget::setActionMode (char c) {
    if (c == 'e' || c == 'v') actionMode = c;
}

void PCDWidget::mousePressEvent (QMouseEvent *e) {
    anchor = e->pos();
    view_changed = true;
    if (actionMode == 'e' && e->buttons() & Qt::LeftButton) {
        for (int i = 0; i < selected_vector.size(); i++)
            for (int j = 0; j < selected_vector[i].size(); j++)
                selected_vector[i][j] = false;

        if (anchor.x() > 1 && anchor.x() < width() - 1 && anchor.y() > 1 && anchor.y() < height() - 1)
            for (int i = anchor.x() - 1; i <= anchor.x() + 1; i++)
                for (int j = anchor.y() - 1; j <= anchor.y() + 1; j++)
                    for (int k = 0; k < screenmap[i][j].size(); k++)
                        selected_vector[screenmap[i][j][k].pointcloud][screenmap[i][j][k].point] = true;
        cloud_changed = true;
    }
}

void PCDWidget::mouseMoveEvent (QMouseEvent *e) {
    if (actionMode == 'v') {
        QPoint diff = e->pos() - anchor;
        if (e->buttons() & Qt::LeftButton) {
            rot_x += diff.y() / 5.0f;
            rot_y += diff.x() / 5.0f;
            qNormalizeAngle(rot_x);
            qNormalizeAngle(rot_y);
            emit xRotationChanged((int)rot_x);
            emit yRotationChanged((int)rot_y);
        } else if (e->buttons() & Qt::RightButton) {
            rot_z += diff.x() / 5.0f;
            qNormalizeAngle(rot_z);
            emit zRotationChanged((int)rot_z);
        } else if (e->buttons() & Qt::MiddleButton) {
            //scale -= diff.y() / 300.0f;
            //if(scale<0) scale = 0;
            pos_x += diff.x() * 0.01f / sqrt(scale);
            pos_y -= diff.y() * 0.01f / sqrt(scale);
        }
        anchor = e->pos();
        updateGL();
    } else if (actionMode == 'e') {
        QPoint newanchor = e->pos();
        for (int i = 0; i < selected_vector.size(); i++)
            for (int j = 0; j < selected_vector[i].size(); j++)
                selected_vector[i][j] = false;

        if (anchor.x() >= 0 && anchor.x() < width() && anchor.y() >= 0 && anchor.y() < height() && newanchor.x() >= 0 && newanchor.x() < width() && newanchor.y() >= 0 && newanchor.y() < height()) {
            int start_x, start_y, end_x, end_y;
            (anchor.x() < newanchor.x()) ? start_x = anchor.x() : start_x = newanchor.x();
            (anchor.y() < newanchor.y()) ? start_y = anchor.y() : start_y = newanchor.y();
            (anchor.x() < newanchor.x()) ? end_x = newanchor.x() : end_x = anchor.x();
            (anchor.y() < newanchor.y()) ? end_y = newanchor.y() : end_y = anchor.y();
            for (int i = start_x; i <= end_x; i++)
                for (int j = start_y; j <= end_y; j++)
                    for (int k = 0; k < screenmap[i][j].size(); k++)
                        selected_vector[screenmap[i][j][k].pointcloud][screenmap[i][j][k].point] = true;
        }
        cloud_changed = true;
    }
}

void PCDWidget::mouseReleaseEvent (QMouseEvent *) { }

void PCDWidget::wheelEvent (QWheelEvent *e) {
    if (actionMode == 'v') {
        e->delta() > 0 ? scale += scale * 0.1f : scale -= scale * 0.1f;
        if (scale < 0) scale = 0;
        view_changed = true;
        updateGL();
    }
}

void PCDWidget::mouseDoubleClickEvent (QMouseEvent *) {
    if (actionMode == 'v') {
        rot_x = rot_y = rot_z = 0.0f;
        pos_x = pos_y = 0.0f;
        scale = 0.1f;
        view_changed = true;
        updateGL();
    }
}

void PCDWidget::setXRotation (int angle) {
    qNormalizeAngle(angle);
    if (angle != rot_x) {
        rot_x = angle;
        // emit = [Java] notifyAll();
        emit xRotationChanged(angle);
        view_changed = true;
        updateGL();
    }
}

void PCDWidget::setYRotation (int angle) {
    qNormalizeAngle(angle);
    if (angle != rot_y) {
        rot_y = angle;
        emit yRotationChanged(angle);
        view_changed = true;
        updateGL();
    }
}

void PCDWidget::setZRotation (int angle) {
    qNormalizeAngle(angle);
    if (angle != rot_z) {
        rot_z = angle;
        emit zRotationChanged(angle);
        view_changed = true;
        updateGL();
    }
}

void PCDWidget::setGridEnabled (int enabled) {
    grid_enabled = enabled;
    view_changed = true;
    updateGL();
}

void PCDWidget::repaint (int index, QColor color) {
    if (view_changed) {
        color_vector[index] = color.rgb();
        glDeleteLists(tile_list, 1);
        view_changed = false;
        create_list();
        view_changed = false;
    }
}

void PCDWidget::create_list () {
    cloud_changed = false;
    glNewList(tile_list, GL_COMPILE);
    glBegin(GL_POINTS);
    for (int index = 0; index < color_vector.size(); index++) {
        if (object_type[index] == 0) {
            pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_vector[index].get()[0];

            GLuint temp = color_vector[index];
            GLubyte b = temp % 0x100;
            temp /= 0x100;
            GLubyte g = temp % 0x100;
            temp /= 0x100;
            GLubyte r = temp % 0x100;

            GLubyte sb = b;
            GLubyte sg = g;
            GLubyte sr = r;

            (sb < 128) ? sb += 128 : sb /= 2;
            (sg < 128) ? sg += 128 : sg /= 2;
            (sr < 128) ? sr += 128 : sr /= 2;

            for (unsigned int i = 0; i < cloud.size(); ++i) {
                if (selected_vector[index][i]) glColor3ub(sr, sg, sb);
                else glColor3ub(r, g, b);
                glVertex3f((GLfloat)cloud[i].x, (GLfloat)cloud[i].y, (GLfloat)cloud[i].z);
            }
        }
    }
    glEnd();
    glBegin(GL_LINES);
    for (int index = 0; index < color_vector.size(); index++) {
        if (object_type[index] == 1) {
            pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_vector[index].get()[0];

            GLuint temp = color_vector[index];
            GLubyte b = temp % 0x100;
            temp /= 0x100;
            GLubyte g = temp % 0x100;
            temp /= 0x100;
            GLubyte r = temp % 0x100;

            for (unsigned int i = 0; i < cloud.size(); ++i) {
                if (selected_vector[index][i]) glColor3f(1.0f, 0.0f, 0.0f);
                else glColor3ub(r, g, b);
                glVertex3f((GLfloat)cloud[i].x, (GLfloat)cloud[i].y, (GLfloat)cloud[i].z);
            }
        }
    }
    glEnd();
    glEndList();
}

void PCDWidget::normalize_pcd_fast_inaccurate (int index) {

    pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_vector[index].get()[0];

    // transformation 1 = 10 m
    for (unsigned int i = 0; i < cloud.size(); ++i) {
        cloud[i].x = (cloud[i].x - transform_params.max.x / 2 - transform_params.min.x / 2) / transform_params.div * 50;
        cloud[i].y = (cloud[i].y - transform_params.max.y / 2 - transform_params.min.y / 2) / transform_params.div * 50;
        cloud[i].z = (cloud[i].z - transform_params.max.z / 2 - transform_params.min.z / 2) / transform_params.div * 50;
    }
    cloud_vector[index] = cloud.makeShared();
}

void PCDWidget::normalize_pcd (int index) {
    pcl::PointCloud<pcl::PointXYZRGBA> cloud = cloud_vector[index].get()[0];

    _min.x = _min.y = _min.z = (float)RAND_MAX;
    _max.x = _max.y = _max.z = -(float)RAND_MAX;
    for (unsigned int i = 0; i < cloud.size(); ++i) {
        if (cloud[i].x > _max.x) {
            _max.x = cloud[i].x;
        }
        if (cloud[i].y > _max.y) {
            _max.y = cloud[i].y;
        }
        if (cloud[i].z > _max.z) {
            _max.z = cloud[i].z;
        }
        if (cloud[i].x < _min.x) {
            _min.x = cloud[i].x;
        }
        if (cloud[i].y < _min.y) {
            _min.y = cloud[i].y;
        }
        if (cloud[i].z < _min.z) {
            _min.z = cloud[i].z;
        }
    }
    _cloud_size = cloud.size();
}

void PCDWidget::get_proj_params (pcl::PointXYZRGBA& min, pcl::PointXYZRGBA& max, int& cloud_size) {
    //    normalize_pcd(list_of_pcds->currentRow());
    normalize_pcd(0);
    min = _min;
    max = _max;
    cloud_size = _cloud_size;
}

void PCDWidget::set_trasform_parameters (const pcl::PointXYZRGBA& min, const pcl::PointXYZRGBA& max, float divider) {
    transform_params.min = min;
    transform_params.max = max;
    transform_params.div = divider;
}

void PCDWidget::set_trasform_parameters (const TransformParams & tparams) {
    transform_params = tparams;
}

#define PI 3.14159
