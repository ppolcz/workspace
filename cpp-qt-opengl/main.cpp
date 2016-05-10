/*
 * File:   main.cpp
 * Author: polpe
 *
 * Created on January 14, 2014, 3:43 AM
 */

#include <QApplication>

#include "mainwindow/MainWindow.h"

#include "glwidgets/GL2DWidget.h"
#include "glwidgets/GL3DWidget.h"
#include "glwidgets/pcd_rotation_widget.h"
#include "glwidgets/dem_viewer.h"

int main(int argc, char *argv[]) {
    // initialize resources, if needed
    // Q_INIT_RESOURCE(resfile);

    QApplication app(argc, argv);

    BigMainWindow window;
    window.show();

    window.addGLWidget(new DemViewer, "DEM Viewer");
    window.addGLWidget(new GL2DWidget, "OpenGL 2D");
    window.addGLWidget(new GL3DWidget, "OpenGL 3D");
    window.addGLWidget(new PCDRotationWidget, "pcd - Forgatas demo");
    
    return app.exec();
}
