/* 
 * File:   GL3DWidget.cpp
 * Author: polpe
 * 
 * Created on January 30, 2014, 7:44 PM
 */

#include "GL3DWidget.h"
#include "include/proj_params.h"

#include <iostream>
#include <cmath>
#include <GL/glu.h>

#define PRINT_FUNC std::cout << __func__ << std::endl;

const float GL3DWidget::s_vertexBuffer[] = {
    -0.5f, -0.5f, 0.0f,
    -0.5f, 0.5f, 0.0f,
    0.5f, 0.5f, 0.0f,
    0.5f, -0.5f, 0.0f
};

const float GL3DWidget::s_colorBuffer[] = {
    1.0f, 0.0f, 0.0f, 0.5f,
    0.0f, 1.0f, 0.0f, 0.5f,
    0.0f, 0.0f, 1.0f, 0.5f,
    1.0f, 1.0f, 0.0f, 0.5f
};

GL3DWidget::GL3DWidget(QWidget * parent) : QGLWidget(QGLFormat(QGL::SampleBuffers | QGL::AlphaChannel), parent) {
    PRINT_FUNC
    makeCurrent();
}

GL3DWidget::~GL3DWidget() { }

void GL3DWidget::mousePressEvent(QMouseEvent*) { }

void GL3DWidget::mouseReleaseEvent(QMouseEvent*) { }

void GL3DWidget::mouseDoubleClickEvent(QMouseEvent*) { }

void GL3DWidget::mouseMoveEvent(QMouseEvent*) { }

void GL3DWidget::wheelEvent(QWheelEvent*) { }

void GL3DWidget::initializeGL() {
    PRINT_FUNC

    // clear color will be white
    glClearColor(P_CLEAR_RED, P_CLEAR_GREEN, P_CLEAR_BLUE, P_CLEAR_ALPHA);

    glEnable(GL_DEPTH_TEST);
    glClearStencil(0x0);
    glEnable(GL_STENCIL_TEST);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}

void GL3DWidget::resizeGL(int width, int height) {
    PRINT_FUNC;

    double dwidth = double(width);
    double dheight = double(height);
    double aspect = dwidth / dheight;
    double fovy = dheight / std::sqrt(dwidth * dwidth + dheight * dheight) * P_DEFAULT_FOV_DEG;

    // projection matrix no 1 
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(fovy, aspect, 0.1, 5.0);
    glTranslatef(0, 0, -3);
    // now we are looking from (0,0,-3) to -z direction from 2.8 (near) to -2 (far)

    // projection matrix no 2
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(fovy, aspect, p_zNear - p_camPos, p_zFar - p_camPos);
    glRotatef(20, 0, 0, 1);
    glRotatef(10, 0, 1, 0);
    glRotatef(10, 1, 0, 0);
    glTranslatef(0, 0, p_camPos);
    // now we are looking from (0,0,p_camPos) to -z direction from p_zNear to p_zFar with a little rotation
    printf("fovy = %f, aspect = %f\n", fovy, aspect);

    //    glMatrixMode(GL_PROJECTION);
    //    glLoadIdentity();
    //    gluLookAt(0, 0, p_camPos, 0, 0, 0, 0, 1, 0);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    float right = ((float) width) / 2000.f;
    float left = -right;
    float top = ((float) height) / 2000.f;
    float bottom = -top;
    float near = 20.f;
    float far = 400.f;
    glFrustum(left, right, bottom, top, near, far);
    glTranslated(0, 0, -100);

    printf("right = %.6g, top = %f\n", right, top);
    printf("2*near / (right - left) = %f\n2*near / (top - bottom) = %f\n2*far*near / (far - near) = %f\n",
            2 * near / (right - left),
            2 * near / (top - bottom),
            2 * far * near / (far - near));

    pm_printMatrix(GL_PROJECTION_MATRIX, "projection matrix");

    // IMPORTANT - without this the size of the GL surface will not fit the QGLWidget 
    glViewport(0, 0, width, height);
}

void GL3DWidget::paintGL() {
    PRINT_FUNC

    // draw into the GL widget
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glRotatef(45, 1, 1, 0);

    pm_drawCoordinateSystem();
    
    glEnableClientState(GL_COLOR_ARRAY);
    glEnableClientState(GL_VERTEX_ARRAY);
    glColorPointer(4, GL_FLOAT, 0, s_colorBuffer);
    glVertexPointer(3, GL_FLOAT, 0, s_vertexBuffer);
    {
        glTranslatef(0, 0, -0.5);
        glPushMatrix();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        glRotatef(90, 1, 0, 0);
        glTranslatef(0, 0.5, 0.5);
        glPushMatrix();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        glRotatef(90, 0, 1, 0);
        glTranslatef(0.5, 0, 0.5);
        glPushMatrix();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        glPopMatrix();
        glTranslatef(0, 0, -1);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        glPopMatrix();
        glTranslatef(0, 0, -1);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        glPopMatrix();
        glTranslatef(0, 0, 1);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }
    glDisableClientState(GL_COLOR_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);


    glColor3f(1, 0, 0);
    glPointSize(5);
    glLineWidth(5);
    glBegin(GL_LINE_STRIP);
    {
        glVertex3d(-1, -1, -1);
        glVertex3d(1, 1, -1);
    }
    glEnd();

    double scale = 0.04;

    glBegin(GL_LINE_STRIP);
    {
        glVertex3d(-1 * scale, -1 * scale - 0.1, 2);
        glVertex3d(1 * scale, 1 * scale - 0.1, 2);
    }
    glEnd();

    glBegin(GL_POINTS);
    {
        glVertex3d(0, 0, -1);
        glVertex3d(0.3, 0.3, -1);
        glVertex3d(-0.3, -0.3, -1);
    }
    glEnd();
}

void GL3DWidget::paintBasePlain() { }

void GL3DWidget::pm_printMatrix(GLenum matrix_name, const char * comment) {
    float * matrix = new float [16];
    glGetFloatv(matrix_name, matrix);
    printf("\n%s:\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n%12.6f, %12.6f, %12.6f, %12.6f\n\n",
            comment,
            matrix[0], matrix[4], matrix[8], matrix[12],
            matrix[1], matrix[5], matrix[9], matrix[13],
            matrix[2], matrix[6], matrix[10], matrix[14],
            matrix[3], matrix[7], matrix[11], matrix[15]);
}

void GL3DWidget::pm_drawCoordinateSystem() {
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