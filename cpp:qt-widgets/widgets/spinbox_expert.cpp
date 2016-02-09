/* 
 * File:   spinbox_expert.cpp
 * Author: polpe
 * 
 * Created on February 15, 2014, 3:06 PM
 */

#include "spinbox_expert.h"

SpinBoxExpert::SpinBoxExpert (QWidget* parent) : QSpinBox (parent) {
    m_oldvalue = value();
    enableConstraints();
}

SpinBoxExpert::~SpinBoxExpert () { }

// a Qt4.8 moc generatora nem tud macrokat kifejteni, sajnos :(
// SETTER_TO_SLOT_IMP (SpinBoxExpert, QSpinBox, Maximum, 1, int)
// SETTER_TO_SLOT_IMP (SpinBoxExpert, QSpinBox, Minimum, 1, int)
// SETTER_TO_SLOT_IMP (SpinBoxExpert, QSpinBox, Range, 2, int, int)

void SpinBoxExpert::setMaximum (int max) { 
    QSpinBox::setMaximum(max);
}

void SpinBoxExpert::setMinimum (int min) { 
    QSpinBox::setMinimum(min);
}

void SpinBoxExpert::setRange (int min, int max) { 
    QSpinBox::setRange(min, max);
}

void SpinBoxExpert::enableConstraints () {
    connect(this, SIGNAL(valueChanged(int)), this, SLOT(psl_calcDiff(int)));
}

void SpinBoxExpert::disableConstraints () {
    disconnect(this, SIGNAL(valueChanged(int)), this, SLOT(psl_calcDiff(int)));
}

void SpinBoxExpert::setRange (int min, int step, int max) {
    setRange(min, max);
    setSingleStep(step);
}

void SpinBoxExpert::sl_incMaximum (int diff) {
    setMaximum(maximum() + diff);
}

void SpinBoxExpert::sl_decMaximum (int diff) {
    setMaximum(maximum() - diff);
}

void SpinBoxExpert::psl_calcDiff (int value) {
    m_diff = value - m_oldvalue;
    m_oldvalue = value;
    emit sig_diffChanged(m_diff);
}
