/* 
 * File:   demviewer.cpp
 * Author: polpe
 * 
 * Created on February 22, 2014, 12:55 PM
 */

#include <GL/gl.h>

#include "demviewer.h"

DemViewer::DemViewer (QWidget * parent) : QGLViewer (parent) { }

DemViewer::~DemViewer () { }

void DemViewer::init () {
    qglviewer::Vec min(353151, 5260480, 0), max(354422, 5261630, 1000);
    setSceneBoundingBox(min, max);

     restoreStateFromFile();
     glDisable(GL_LIGHTING);
     glPointSize(3.0);
    // setGridIsDrawn();
     startAnimation();
}

void DemViewer::draw () {
    
    glMatrixMode(GL_PROJECTION);
    glOrtho(353151, 354422, 5260480, 5261630, 0, 1000);
    
    glBegin(GL_POINTS);
    for (int i = 0; i < 100; i++)
        glVertex3f(353151 + double(i), 5260480 + double(i), double(i)); 
    glEnd();
}