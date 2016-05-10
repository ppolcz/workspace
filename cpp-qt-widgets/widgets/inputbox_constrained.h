/* 
 * File:   inputboxconstrained.h
 * Author: polpe
 *
 * Created on February 15, 2014, 12:56 PM
 */

#ifndef INPUTBOX_CONSTRAINED_H
#define	INPUTBOX_CONSTRAINED_H

#include <functional>
#include "inputbox.h"

class InputBoxConstrained : public InputBox {
    Q_OBJECT
public:
    typedef std::function<bool(QString &)> ValidationFunction;
    
    explicit InputBoxConstrained (QWidget * parent = 0);
    virtual ~InputBoxConstrained ();

    void addConstraint (ValidationFunction constraint);

signals:
    void sig_validationChanged (bool);

public slots:
    void validate ();

    void p_makeValid (bool);
protected:

private:
    QVector<ValidationFunction> m_constraints;
    bool m_valid;
};

#endif	/* INPUTBOX_CONSTRAINED_H */

