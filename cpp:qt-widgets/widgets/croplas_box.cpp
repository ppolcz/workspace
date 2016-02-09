/* 
 * File:   croplas_box.cpp
 * Author: polpe
 * 
 * Created on February 25, 2014, 12:18 PM
 */

#include "croplas_box.h"
#include "line.h"

#include <iostream>

CropLasBox::CropLasBox (QWidget * parent) : InputBox (parent) {
    p_setupUi();
    p_setupLinks();
}

CropLasBox::~CropLasBox () { }

void CropLasBox::p_setupUi () {
    setTitle("Crop and merge LAS clouds");

    addWidget(new QLabel("Input cloud (.las) files"));
    m_inputs = new AppendableLineEdit;
    addWidget(m_inputs);

    addWidget(new QLabel("Output cloud (.pcd) file"));
    m_savename = new DroppableLineEdit;
    addWidget(m_savename);

    addWidget(new QLabel("Desired bounding box (UTM)"));
    m_xmin = util::setupSpinBox(addInput<SpinBoxExpert>("x min "), 353151, 20, 354422, 353551);
    m_ymin = util::setupSpinBox(addInput<SpinBoxExpert>("y min "), 5260480, 20, 5261630, 5260860);
    m_xmax = util::setupSpinBox(addInput<SpinBoxExpert>("x max "), 353151, 20, 354422, 354022);
    m_ymax = util::setupSpinBox(addInput<SpinBoxExpert>("y max "), 5260480, 20, 5261630, 5261230);

    addWidget(new HLine);

    m_decimate = util::setupSpinBox(addInput<QSpinBox>("decimate "), 1, 1, 100, 1);
    m_kneigh = util::setupSpinBox(addInput<QSpinBox>("k neigh. "), 4, 1, 20, 10);

    btn_crop = new QPushButton(this);
    btn_crop->setText("Crop & merge");
    addWidget(btn_crop);

    addWidget(new HLine);

    addWidget(new QLabel("Remove duplicates from:"));
    m_removable = new DroppableLineEdit;
    addWidget(m_removable);

    btn_remove_duplicates = new QPushButton;
    btn_remove_duplicates->setText("Remove duplicates");
    addWidget(btn_remove_duplicates);
}

void CropLasBox::p_setupLinks () {
    connect(btn_crop, SIGNAL(pressed()), this, SIGNAL(sig_crop()));
    connect(btn_remove_duplicates, SIGNAL(pressed()), this, SIGNAL(sig_remove_duplicates()));

    connect(m_xmin, SIGNAL(valueChanged(int)), m_xmax, SLOT(setMinimum(int)));
    connect(m_xmax, SIGNAL(valueChanged(int)), m_xmin, SLOT(setMaximum(int)));
    connect(m_ymin, SIGNAL(valueChanged(int)), m_ymax, SLOT(setMinimum(int)));
    connect(m_ymax, SIGNAL(valueChanged(int)), m_ymin, SLOT(setMaximum(int)));

    connect(m_xmin, SIGNAL(valueChanged(int)), this, SLOT(psl_somethingChanged(int)));
    connect(m_xmax, SIGNAL(valueChanged(int)), this, SLOT(psl_somethingChanged(int)));
    connect(m_ymin, SIGNAL(valueChanged(int)), this, SLOT(psl_somethingChanged(int)));
    connect(m_ymax, SIGNAL(valueChanged(int)), this, SLOT(psl_somethingChanged(int)));
}

void CropLasBox::psl_somethingChanged (int) {
    emit sig_clipDimensions(getXmin(), getYmin(), getXmax(), getYmax());
}

double CropLasBox::getYmax () const {
    return (double) m_ymax->value();
}

double CropLasBox::getXmax () const {
    return (double) m_xmax->value();
}

double CropLasBox::getYmin () const {
    return (double) m_ymin->value();
}

double CropLasBox::getXmin () const {
    return (double) m_xmin->value();
}

int CropLasBox::getDecimate () const {
    return m_decimate->value();
}

int CropLasBox::getKNeigh () const {
    return m_kneigh->value();
}

QStringList CropLasBox::getInputs () const {
    return m_inputs->getPaths();
}

QString CropLasBox::getSavename () const {
    return m_savename->text();
}
