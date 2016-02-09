/* 
 * File:   inputslayout.cpp
 * Author: polpe
 * 
 * Created on February 17, 2014, 8:55 AM
 */

#include "inputslayout.h"
#include "widgets/droppablelineedit.h"
#include "widgets/combobox_expert.h"

InputsLayout::InputsLayout (QWidget* parent) : QGridLayout (parent) {
    p_setupUi();
}

InputsLayout::~InputsLayout () { }

void InputsLayout::p_setupUi () {
    m_more = new QPushButton(parentWidget());
    m_more->setText("more");
    SET_OBJ_NAME(m_more);
    addWidget(m_more, 0, 2);

    psl_createNewInput();
    p_createConnections();
}

void InputsLayout::p_createConnections () {
    connect(m_more, SIGNAL(pressed()), this, SLOT(psl_createNewInput()));
}

void InputsLayout::p_checkIndex (unsigned index) throw (std::invalid_argument) {
    if ((int) index >= m_combos.size()) throw std::invalid_argument("Index out of bounds" + DEBUG_INFO);
}

QComboBox* InputsLayout::getCombo (unsigned index) {
    p_checkIndex(index);
    return m_combos[index];
}

QLineEdit* InputsLayout::getInput (unsigned index) {
    p_checkIndex(index);
    return m_inputs[index];
}

QString InputsLayout::getString (unsigned index) {
    p_checkIndex(index);
    return m_inputs[index]->text();
}

void InputsLayout::setComboItems (const QStringList& slist) {
    m_comboItems = slist;
    for (QComboBox * combo : m_combos) {
        combo->addItems(m_comboItems);
    }
}

void InputsLayout::psl_createNewInput () {
    if (m_spacer != nullptr) {
        removeItem(m_spacer);
        delete m_spacer;
    }
    m_spacer = new QSpacerItem(0, 0, QSizePolicy::Minimum, QSizePolicy::Expanding);

    auto combo = new ComboBoxExpert(parentWidget());
    combo->addItems(m_comboItems);
    auto input = new DroppableLineEdit(parentWidget());

    m_combos.push_back(combo);
    m_inputs.push_back(input);

    addWidget(combo, m_combos.size() - 1, 0);
    addWidget(input, m_inputs.size() - 1, 1);
    addItem(m_spacer, m_inputs.size(), 0);

    connect(m_combos.back(), SIGNAL(sig_indexChanged(QComboBox*, int)), this, SLOT(psl_comboChanged(QComboBox*, int)));
    emit sig_newInputCreated(m_combos.size() - 1, m_combos.back()->currentIndex());

    update();
}

void InputsLayout::psl_comboChanged (QComboBox* combo, int combo_index) {
    emit sig_inputChanged(m_combos.indexOf(combo), combo_index);
}
