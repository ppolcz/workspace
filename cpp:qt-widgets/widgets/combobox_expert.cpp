/* 
 * File:   combobox_expert.cpp
 * Author: polpe
 * 
 * Created on February 17, 2014, 9:38 AM
 */

#include "combobox_expert.h"

ComboBoxExpert::ComboBoxExpert (QWidget* parent) : QComboBox (parent) {
    p_createConnections();
}

ComboBoxExpert::~ComboBoxExpert () { }

void ComboBoxExpert::p_createConnections () {
    connect(this, SIGNAL(currentIndexChanged(int)), this, SLOT(psl_indexChanged(int)));
}

void ComboBoxExpert::psl_indexChanged (int index) {
    emit sig_indexChanged(this, index);
}
