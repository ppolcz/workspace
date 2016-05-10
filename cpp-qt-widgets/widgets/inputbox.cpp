/* 
 * File:   inputbox.cpp
 * Author: polpe
 * 
 * Created on February 14, 2014, 2:03 PM
 */

#include "inputbox.h"
#include "spinbox_expert.h"
#include "droppablelineedit.h"
#include "appendablelineedit.h"

InputBox::InputBox (QWidget * parent) : QGroupBox (parent) {
    m_layout = new QFormLayout(this);

    setSizePolicy(QSizePolicy::Preferred, QSizePolicy::Maximum);
}

InputBox::~InputBox () { }

template <typename _QWidget>
_QWidget* InputBox::addInput (const QString& label) {
    _QWidget * input = new _QWidget(this);
    m_inputs.insert(input, label);
    m_layout->addRow(label, input);
    return input;
}

void InputBox::addInput (const QString& label, QWidget * input) {
    m_inputs.insert(input, label);
    m_layout->addRow(label, input);
}


void InputBox::addWidget (QWidget* widget) {
    m_layout->addRow(widget);
}

QString InputBox::getLabel (const QWidget* input) {
    return m_inputs[input];
}

// instantiations
template QSpinBox* InputBox::addInput<QSpinBox>(const QString& label);
template QDoubleSpinBox* InputBox::addInput<QDoubleSpinBox>(const QString& label);
template QLineEdit* InputBox::addInput<QLineEdit>(const QString& label);
template DroppableLineEdit* InputBox::addInput<DroppableLineEdit>(const QString& label);
template AppendableLineEdit* InputBox::addInput<AppendableLineEdit>(const QString& label);
template QComboBox* InputBox::addInput<QComboBox>(const QString& label);
template QLabel* InputBox::addInput<QLabel>(const QString& label);

template SpinBoxExpert* InputBox::addInput<SpinBoxExpert>(const QString& label);
