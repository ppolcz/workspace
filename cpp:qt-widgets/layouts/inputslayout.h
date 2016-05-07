/* 
 * File:   inputslayout.h
 * Author: polpe
 *
 * Created on February 17, 2014, 8:55 AM
 */

#ifndef INPUTSLAYOUT_H
#define	INPUTSLAYOUT_H

#include "util/util.h"
#include <stdexcept>

class InputsLayout : public QGridLayout {
    Q_OBJECT
public:
    explicit InputsLayout (QWidget * parent = 0);
    virtual ~InputsLayout ();

    QComboBox * getCombo (unsigned index);
    QString getString (unsigned index);
    QLineEdit * getInput (unsigned index);

    void setComboItems (const QStringList & slist);
    
signals:
    void sig_newInputCreated (int, int);
    void sig_inputChanged (int, int);

private slots:
    void psl_createNewInput ();
    void psl_comboChanged (QComboBox*, int);
    
private:
    void p_setupUi ();
    void p_createConnections ();
    void p_checkIndex (unsigned index) throw (std::invalid_argument);
    void p_setupCombo (QComboBox * combo);

    QStringList m_comboItems;
    QVector<QComboBox*> m_combos;
    QVector<QLineEdit*> m_inputs;
    QPushButton * m_more;
    
    QSpacerItem * m_spacer = nullptr;
};

#endif	/* INPUTSLAYOUT_H */

