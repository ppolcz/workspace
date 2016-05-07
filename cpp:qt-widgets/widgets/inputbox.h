/* 
 * File:   inputbox.h
 * Author: polpe
 *
 * Created on February 14, 2014, 2:03 PM
 */

#ifndef INPUTBOX_H
#define	INPUTBOX_H

#include <functional>
#include "util/util.h"

class InputBox : public QGroupBox {
    Q_OBJECT
    ADD_CHANGE_STATUS_CALLBACK
public:
    ADD_SHARED_PTR (InputBox)

    explicit InputBox (QWidget * parent = 0);
    virtual ~InputBox ();

    template <typename _QWidget>
    _QWidget * addInput (const QString & label);
    void addInput (const QString & label, QWidget * input);
    
    void addWidget(QWidget * widget);
    
    QString getLabel (const QWidget * input);

private:
    QHash<const QWidget*, QString> m_inputs;
    QFormLayout * m_layout;
};

#endif	/* INPUTBOX_H */

