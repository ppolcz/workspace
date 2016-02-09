/* 
 * File:   gltable.cpp
 * Author: polpe
 * 
 * Created on February 15, 2014, 11:48 PM
 */

#include "gltable.h"

GLTable::GLTable(QWidget * parent) : QGLWidget(QGLFormat(QGL::SampleBuffers | QGL::AlphaChannel), parent)  { }

GLTable::~GLTable() { }

void GLTable::initializeGL() { }

void GLTable::resizeGL(int, int) { }

void GLTable::paintGL() {
    glClearColor(0, 0, 0, 0);
    glClear(GL_COLOR_BUFFER_BIT);

    glColor3f(0, 1, 0);
    glBegin(GL_LINES);
    glVertex2d(0, 0);
    glVertex2d(1, 1);
    glEnd();
}


