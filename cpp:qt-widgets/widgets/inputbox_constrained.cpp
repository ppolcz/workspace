/* 
 * File:   inputboxconstrained.cpp
 * Author: polpe
 * 
 * Created on February 15, 2014, 12:56 PM
 */

#include "inputbox_constrained.h"

InputBoxConstrained::InputBoxConstrained (QWidget* parent) : InputBox (parent) { }

InputBoxConstrained::~InputBoxConstrained () { }

void InputBoxConstrained::p_makeValid (bool valid) {
    if (!valid)
        this->setStyleSheet("QComboBox { background-color: red; }");
}

void InputBoxConstrained::addConstraint (ValidationFunction constraint) {
    m_constraints.push_back(constraint);
}

void InputBoxConstrained::validate () {
    QString msg;
    for (auto & f : m_constraints) {
        if (!f(msg)) {
            m_valid = false;
            p_makeValid(false);
            emit sig_validationChanged(false);
        }
    }
    if (m_valid) return;
    m_valid = true;
    emit sig_validationChanged(true);
}


