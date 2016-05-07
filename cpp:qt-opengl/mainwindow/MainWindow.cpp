/*
 * File:   BigMainWindow.cpp
 * Author: polpe
 *
 * Created on January 14, 2014, 4:54 AM
 */

#include "MainWindow.h"

#include <QtWidgets/QLabel>
#include <QtWidgets/QToolBar>
#include <QtWidgets/QAction>
#include <qt4/QtGui/qlistwidget.h>

BigMainWindow::BigMainWindow() {
    widget.setupUi(this);

    // QToolBar * fileToolBar = addToolBar(tr("File"));
    // fileToolBar->addAction(new QAction(this));
}

BigMainWindow::~BigMainWindow() { }

void BigMainWindow::addGLWidget(QGLWidget* glw, QString title) {
    widget.listWidget->addItem(title);
    widget.stackedWidget->addWidget(glw);
}
