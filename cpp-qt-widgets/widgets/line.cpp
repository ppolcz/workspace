/* 
 * File:   linw.cpp
 * Author: polpe
 * 
 * Created on February 15, 2014, 11:25 PM
 */

#include "line.h"

HLine::HLine (QWidget* parent) : QFrame (parent) {
    setFrameShape(QFrame::HLine);
    setFrameShadow(QFrame::Sunken);
}

HLine::~HLine () { }

