/* 
 * File:   GL2DWidget.cpp
 * Author: polpe
 * 
 * Created on February 10, 2014, 3:51 PM
 */

#include "GL2DWidget.h"
#include <iostream>

GL2DWidget::GL2DWidget(QWidget * parent) : QGLWidget(QGLFormat(QGL::SampleBuffers | QGL::AlphaChannel), parent)  { }

GL2DWidget::~GL2DWidget() { }

void GL2DWidget::initializeGL() { }

void GL2DWidget::resizeGL(int, int) { }

void GL2DWidget::paintGL() {
    glClearColor(0, 0, 0, 0);
    glClear(GL_COLOR_BUFFER_BIT);

    glColor3f(0, 1, 0);
    glBegin(GL_LINES);
    glVertex2d(0, 0);
    glVertex2d(1, 1);
    glEnd();
}

