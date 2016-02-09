/* 
 * File:   projectionbox.cpp
 * Author: polpe
 * 
 * Created on February 22, 2014, 8:38 PM
 */

#include "projectionbox.h"
#include "droppablelineedit.h"

ProjectionBox::ProjectionBox (QWidget* parent) : InputBox (parent) {
    p_setupUi();
    p_setupLinks();
}

ProjectionBox::~ProjectionBox () {
    DEBUG
}

void ProjectionBox::p_setupUi () {

    setTitle("Resized projection");

    le_filename = new DroppableLineEdit(this);
    le_savename = new DroppableLineEdit(this);

    sb_width = new QSpinBox(this);
    sb_width->setMaximum(12000);
    sb_width->setMinimum(128);
    sb_width->setValue(1000);

    btn_project = new QPushButton(this);
    btn_project->setText("Project");

    addWidget(new QLabel("Input DEM (.asc) file:", this));
    addWidget(le_filename);
    addWidget(new QLabel("Output image (.jpg) file", this));
    addWidget(le_savename);
    addInput("image width: ", sb_width);
    addWidget(btn_project);
}

void ProjectionBox::p_setupLinks () {
    connect(btn_project, SIGNAL(pressed()), this, SIGNAL(sig_process()));
}

QString ProjectionBox::filename () {
    return le_filename->text();
}

QString ProjectionBox::savename () {
    return le_savename->text();
}

int ProjectionBox::imageWidth () {
    return sb_width->value();
}

