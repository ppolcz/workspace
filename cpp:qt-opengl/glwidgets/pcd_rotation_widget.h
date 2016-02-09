/* 
 * File:   FrontPanel.h
 * Author: polpe
 *
 * Created on September 21, 2012, 11:38 PM
 */

#ifndef PCD_WIDGET_1_H
#define	PCD_WIDGET_1_H

#include <math.h>
#include <iostream>
#include <cstdlib>
#include <ctime>

#include <QtOpenGL>
#include <QImage>
#include <QListWidget>

#include <pcl/point_cloud.h>
#include <pcl/point_types.h>
#include <pcl/kdtree/kdtree_flann.h>
#include <pcl/io/pcd_io.h>
#include <pcl/common/centroid.h>

#include <Eigen/Core>
#include <Eigen/Eigenvalues>
#include <Eigen/Dense>
#include <eigen3/Eigen/src/Eigenvalues/EigenSolver.h>
#include <eigen3/Eigen/src/Core/Matrix.h>

#include <CGAL/intersections.h>
#include <CGAL/Cartesian.h>


#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/matrix_access.hpp>

#include "include/cgal_kiegeszito.h"
#include "include/proj_params.h"
#include "include/print.h"
#include "include/imageBMP.h"
#include "include/debug.h"

#include "pcd_widget.h"

typedef CGAL::Cartesian<double> Kernel;

class PCDRotationWidget : public QGLWidget {
    Q_OBJECT

public:
    explicit PCDRotationWidget(QWidget *parent = 0);
    explicit PCDRotationWidget(pcl::PointCloud<pcl::PointXYZRGBA>::Ptr cloud_ptr, GLuint color = 0xFFFFFFFF);
    virtual ~PCDRotationWidget();

    void addPointCloud(pcl::PointCloud<pcl::PointXYZRGBA>::Ptr cloud_ptr, GLuint color = 0xFFFFFFFF, int type = 0);
    void addRoofs(QVector<pcl::PointCloud<pcl::PointXYZRGBA> > & roofs_vector_nonptr, QVector<GLuint> color);
    void addRoofsPlane(QVector<pcl::PointCloud<pcl::PointXYZRGBA> > & roofs_vector_nonptr, QVector<GLuint> color);
    void planeFitting(int intex = 0);
    void planeIntersection();

    void paintEvent(QPaintEvent *);
    void mousePressEvent(QMouseEvent *);
    void mouseReleaseEvent(QMouseEvent *);
    void mouseDoubleClickEvent(QMouseEvent *);
    void mouseMoveEvent(QMouseEvent *);
    void wheelEvent(QWheelEvent *);
    void initializeGL();
    void paintGL();
    void paintBasePlain();

    float getXRotation() {
        return rot_x;
    }

    float getYRotation() {
        return rot_y;
    }

    float getZRotation() {
        return rot_z;
    }

    static void setActionMode(char c);

public slots:
    void setXRotation(int angle);
    void setYRotation(int angle);
    void setZRotation(int angle);
    void setGridEnabled(int enabled);
    void repaint(int index, QColor color);
    void stabiliseRotationMatrix();

signals:
    void xRotationChanged(int angle);
    void yRotationChanged(int angle);
    void zRotationChanged(int angle);

private: // method
    void create_list();
    void normalize_pcd(int index);
    void normalize_pcd_fast_inaccurate(int index);

private: // tags

    TransformParams transform_params;
    QPoint anchor, movement;
    float scale;
    float rot_x, rot_y, rot_z;
    float pos_x, pos_y;
    float grid_enabled;
    glm::mat4 projectionMatrix; // Store the projection matrix  
    glm::mat4 viewMatrix; // Store the view matrix  
    glm::mat4 modelMatrix; // Store the model matrix  
    pcl::PCDWriter writer;
    pcl::PCDReader reader;
    bool view_changed;
    bool cloud_changed;


public:
    GLuint tile_list;
    GLuint tile_list_plane;
    GLuint tile_list_intersection;
    GLuint tile_list_roofs;
    QVector<pcl::PointCloud<pcl::PointXYZRGBA>::Ptr> cloud_vector;
    QVector<pcl::PointCloud<pcl::PointXYZRGBA> > roofs_vector_nonptr;
    QVector<GLuint> color_vector;
    QVector<bool> enabled_vector;
    QVector<uchar> object_type;
    QVector<QVector<bool> > selected_vector;
    QVector<QVector<QVector<PositionPair> > > screenmap;
    static char actionMode; //e - edit mode, v - view mode


private: // PROJECTION
    pcl::PointXYZRGBA _min, _max;
    int _cloud_size;
public: // PROJECTION

    pcl::PointXYZRGBA get_min() {
        return _min;
    }

    pcl::PointXYZRGBA get_max() {
        return _max;
    }
    void get_proj_params(pcl::PointXYZRGBA & min, pcl::PointXYZRGBA & max, int & cloud_size);
    void set_trasform_parameters(const pcl::PointXYZRGBA & min, const pcl::PointXYZRGBA & max, float divider);
    void set_trasform_parameters(const TransformParams & transform_params);

    TransformParams get_trasform_parameters() {
        return transform_params;
    }
} ;
#endif	/* PCD_WIDGET_1_H */

