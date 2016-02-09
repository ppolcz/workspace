/* 
 * File:   cropdem_box.cpp
 * Author: polpe
 * 
 * Created on February 15, 2014, 9:26 PM
 */

#include "cropdem_box.h"
#include "widgets/line.h"

CropDemBox::CropDemBox (QWidget* parent) : InputBox (parent) {
    p_setupUi();
    p_setupLinks();
}

CropDemBox::~CropDemBox () { }

void CropDemBox::p_setupUi () {
    setTitle("Crop DEM - dimensions");

    m_demSizeX = addInput<QLabel>("DEM Size X ");
    m_demSizeY = addInput<QLabel>("DEM Size Y ");

    addWidget(new HLine(this));

    m_offsetX = util::setupSpinBox(addInput<SpinBoxExpert>("Offset X "), 0, 1, 2000);
    m_offsetY = util::setupSpinBox(addInput<SpinBoxExpert>("Offset Y "), 0, 1, 2000);
    m_sizeX = util::setupSpinBox(addInput<SpinBoxExpert>("Size X "), 100, 1, 1000, 1000);
    m_sizeY = util::setupSpinBox(addInput<SpinBoxExpert>("Size Y "), 100, 1, 1000, 1000);

    btn_update = new QPushButton(this);
    btn_update->setText("Update");
    addWidget(btn_update);

    btn_crop = new QPushButton(this);
    btn_crop->setText("Crop DEM");
    addWidget(btn_crop);
    
    btn_resize = new QPushButton(this);
    btn_resize->setText("Resize DEM");
    addWidget(btn_resize);
}

void CropDemBox::p_setupLinks () {
    connect(btn_crop, SIGNAL(pressed()), this, SIGNAL(sig_crop()));
    connect(btn_update, SIGNAL(pressed()), this, SIGNAL(sig_update()));
    connect(btn_resize, SIGNAL(pressed()), this, SIGNAL(sig_resize()));
}

/**
 * m_sizeX + m_offsetX < size_x
 * m_sizeX < size_x - m_offsetX
 */

void CropDemBox::p_createConstraints () {
    connect(m_offsetX, SIGNAL(sig_diffChanged(int)), m_sizeX, SLOT(sl_decMaximum(int)));
    connect(m_sizeX, SIGNAL(sig_diffChanged(int)), m_offsetX, SLOT(sl_decMaximum(int)));
    connect(m_offsetY, SIGNAL(sig_diffChanged(int)), m_sizeY, SLOT(sl_decMaximum(int)));
    connect(m_sizeY, SIGNAL(sig_diffChanged(int)), m_offsetY, SLOT(sl_decMaximum(int)));
}

void CropDemBox::p_destroyConstraints () {
    disconnect(m_offsetX, SIGNAL(sig_diffChanged(int)), m_sizeX, SLOT(sl_decMaximum(int)));
    disconnect(m_sizeX, SIGNAL(sig_diffChanged(int)), m_offsetX, SLOT(sl_decMaximum(int)));
    disconnect(m_offsetY, SIGNAL(sig_diffChanged(int)), m_sizeY, SLOT(sl_decMaximum(int)));
    disconnect(m_sizeY, SIGNAL(sig_diffChanged(int)), m_offsetY, SLOT(sl_decMaximum(int)));
}

void CropDemBox::setDemDimensions (int size_x, int size_y) {
    m_demSizeX->setText(QString::number(size_x));
    m_demSizeY->setText(QString::number(size_y));

    p_destroyConstraints();
    m_sizeX->setMaximum(size_x - m_offsetX->value());
    m_sizeY->setMaximum(size_y - m_offsetY->value());
    m_offsetX->setMaximum(size_x - m_sizeX->value());
    m_offsetY->setMaximum(size_x - m_sizeY->value());
    p_createConstraints();
}

int CropDemBox::getOffsetX () {
    return m_offsetX->value();
}

int CropDemBox::getOffsetY () {
    return m_offsetY->value();
}

int CropDemBox::getSizeX () {
    return m_sizeX->value();
}

int CropDemBox::getSizeY () {
    return m_sizeY->value();
}
