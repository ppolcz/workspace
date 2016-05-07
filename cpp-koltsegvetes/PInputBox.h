/* 
 * File:   inputbox.h
 * Author: polpe
 *
 * Created on February 14, 2014, 2:03 PM
 */

#ifndef INPUTBOX_H
#define	INPUTBOX_H

#include <functional>
#include "includes_qt.h"
#include "polpedelop.h"

class PInputBox : public QGroupBox
{
    Q_OBJECT
public:
    POLPE_DELETE_OP

    explicit PInputBox(QWidget * parent = 0);
    virtual ~PInputBox();

    template <typename _QWidget>
    _QWidget * addInput(const QString & label);
    void addInput(const QString & label, QWidget * input);

    void addWidget(QWidget * widget);

    QString getLabel(const QWidget * input);

private:
    QHash<const QWidget*, QString> m_inputs;
    QFormLayout * m_layout;
};

#endif	/* INPUTBOX_H */

