/* 
 * File:   inputbox.cpp
 * Author: polpe
 * 
 * Created on February 14, 2014, 2:03 PM
 */

#include <polcz/pczdebug.h>
#include "PInputBox.h"

PInputBox::PInputBox (QWidget * parent) : QGroupBox (parent)
{
    m_layout = new QFormLayout(this);

    setSizePolicy(QSizePolicy::Preferred, QSizePolicy::Maximum);
    PCZ_SEG(this);
}

PInputBox::~PInputBox ()
{
    PCZ_SEG(this);
}

template <typename _QWidget>
_QWidget* PInputBox::addInput (const QString& label)
{
    _QWidget * input = new _QWidget(this);
    m_inputs.insert(input, label);
    m_layout->addRow(label, input);
    return input;
}

void PInputBox::addInput (const QString& label, QWidget * input)
{
    m_inputs.insert(input, label);
    m_layout->addRow(label, input);
}

void PInputBox::addWidget (QWidget* widget)
{
    m_layout->addRow(widget);
}

QString PInputBox::getLabel (const QWidget* input)
{
    return m_inputs[input];
}

// instantiations
template QSpinBox* PInputBox::addInput<QSpinBox>(const QString& label);
template QDoubleSpinBox* PInputBox::addInput<QDoubleSpinBox>(const QString& label);
template QLineEdit* PInputBox::addInput<QLineEdit>(const QString& label);
template QComboBox* PInputBox::addInput<QComboBox>(const QString& label);
template QLabel* PInputBox::addInput<QLabel>(const QString& label);
