/* 
 * File:   dem_viewer.cpp
 * Author: polpe
 * 
 * Created on February 26, 2014, 12:17 PM
 */

#include "dem_viewer.h"

#include <iostream>
#include <GL/gl.h>
#include <GL/glu.h>
#include <qt5/QtGui/qimage.h>

#define DEBUG_INFO std::string(" - in file: ") + std::string(__FILE__) + std::string(":") + std::to_string(__LINE__) + std::string(" - ") + std::string(__FUNCTION__) + std::string("()")
#define DEBUG std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "()\n";
#define DEBUG_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "  --  "
#define DEBUG_ARRAY(array, from, to) DEBUG_ << #array << " = "; for (int i = from; i < to; ++i) { std::cout << array[i] << " "; } std::cout << std::endl;

const float DemViewer::s_vertexBuffer[] = {
    -100.0f, -100.0f, 0.0f,
    -100.0f, 100.0f, 0.0f,
    100.0f, 100.0f, 0.0f,
    100.0f, -100.0f, 0.0f
};

const float DemViewer::s_colorBuffer[] = {
    1.0f, 0.0f, 0.0f, 0.5f,
    0.0f, 1.0f, 0.0f, 0.5f,
    0.0f, 0.0f, 1.0f, 0.5f,
    1.0f, 1.0f, 0.0f, 0.5f
};

const float DemViewer::s_textureBuffer[] = {
    0.0f, 1.0f,
    0.0f, 0.0f,
    1.0f, 0.0f,
    1.0f, 1.0f
};

DemViewer::DemViewer (QWidget * parent) : QGLWidget (parent) {
    m_textureImage.load("/home/polpe/Resources/Muhold_javitott/Dsm-Budapest-Pleiades-1m-1000-proba.jpg");


    m_vertexBuffer[0] = m_vertexBuffer[3] = p_minDemX;
    m_vertexBuffer[1] = m_vertexBuffer[10] = p_minDemY;
    m_vertexBuffer[6] = m_vertexBuffer[9] = p_maxDemX;
    m_vertexBuffer[4] = m_vertexBuffer[7] = p_maxDemY;
    m_vertexBuffer[2] = m_vertexBuffer[5] = m_vertexBuffer[8] = m_vertexBuffer[11] = 0.0;

    DEBUG_ARRAY(m_vertexBuffer, 0, 12);
}

DemViewer::~DemViewer () { }

void DemViewer::initializeGL () {
    glClearColor(0, 0, 0, 0);
    glShadeModel(GL_SMOOTH);
    glEnable(GL_DEPTH_TEST);

    glClearStencil(0x0);
    glEnable(GL_STENCIL_TEST);

    glEnable(GL_TEXTURE_2D);
    p_loadTexture();
}

void DemViewer::paintGL () {

    // draw into the GL widget
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    // UTM coordinates:
    glLoadIdentity();
    glScalef(p_scaleDemX, p_scaleDemY, 1.0);
    glTranslated(-p_centerDemX, -p_centerDemY, 0);

    glLineWidth(1);

    // full DEM coordinate
    glColor4f(0.4, 0.4, 0.4, 0.4);
    glBegin(GL_LINES);
    {
        glVertex2d(p_minDemX, p_centerDemY);
        glVertex2d(p_maxDemX, p_centerDemY);
        glVertex2d(p_centerDemX, p_minDemY);
        glVertex2d(p_centerDemX, p_maxDemY);
    }
    glEnd();
    glColor4f(1,1,1,1);

    // selection
    glBegin(GL_LINE_STRIP);
    {
        glVertex2d(p_minClipX, p_minClipY);
        glVertex2d(p_minClipX, p_maxClipY);
        glVertex2d(p_maxClipX, p_maxClipY);
        glVertex2d(p_maxClipX, p_minClipY);
        glVertex2d(p_minClipX, p_minClipY);
    }
    glEnd();

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    // glEnableClientState(GL_COLOR_ARRAY);
    {
        glBindTexture(GL_TEXTURE_2D, m_textureID);

        glTexCoordPointer(2, GL_FLOAT, 0, s_textureBuffer);
        glVertexPointer(3, GL_DOUBLE, 0, m_vertexBuffer);
        // glColorPointer(4, GL_FLOAT, 0, s_colorBuffer);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }
    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    // glDisableClientState(GL_COLOR_ARRAY);
}

void DemViewer::resizeGL (int width, int height) {
    double dwidth = double(width);
    double dheight = double(height);
    double aspect = dwidth / dheight;
    double fovy = dheight / std::sqrt(dwidth * dwidth + dheight * dheight) * P_DEFAULT_FOV_DEG;

    // projection matrix no 2
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(fovy, aspect, p_zNear - p_camPos, p_zFar - p_camPos);
    glTranslatef(0, 0, p_camPos);

    // IMPORTANT - without this the size of the GL surface will not fit the QGLWidget 
    glViewport(0, 0, width, height);
}

void DemViewer::p_printMatrix (GLenum matrix_name, const char * comment) {
    float * matrix = new float [16];
    glGetFloatv(matrix_name, matrix);
    printf("\n%s:\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n\n",
            comment,
            matrix[0], matrix[4], matrix[8], matrix[12],
            matrix[1], matrix[5], matrix[9], matrix[13],
            matrix[2], matrix[6], matrix[10], matrix[14],
            matrix[3], matrix[7], matrix[11], matrix[15]);
}

void DemViewer::p_drawCoordinateSystem () {
    glPointSize(19);
    glBegin(GL_POINTS);
    {

        glColor3f(0, 1, 0);
        glVertex3d(0, 0, 0);
    }
    glEnd();

    glLineWidth(5);
    glBegin(GL_LINE_STRIP);
    {
        glColor3f(1, 0, 0);
        glVertex3d(0, 0, 0);
        glVertex3d(1, 0, 0);
    }
    glEnd();

    glLineWidth(5);
    glBegin(GL_LINE_STRIP);
    {
        glColor3f(0, 1, 0);
        glVertex3d(0, 0, 0);
        glVertex3d(0, 1, 0);
    }
    glEnd();

    glLineWidth(5);
    glBegin(GL_LINE_STRIP);
    {
        glColor3f(0, 0, 1);
        glVertex3d(0, 0, 0);
        glVertex3d(0, 0, 1);
    }
    glEnd();
}

void DemViewer::p_loadTexture () {
    glGenTextures(1, &m_textureID);
    glBindTexture(GL_TEXTURE_2D, m_textureID);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
            m_textureImage.width(),
            m_textureImage.height(),
            0, GL_RGBA, GL_UNSIGNED_BYTE, m_textureImage.bits());

    //    for (int i = 0 ; i < m_textureImage.width() * m_textureImage.height() * 4 ; ++i) {
    //        std::cout << (int) m_textureImage.bits()[i] << std::endl;
    //    }

//    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
//    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
}
