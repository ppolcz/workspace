/* 
 * File:   spinbox_expert.h
 * Author: polpe
 *
 * Created on February 15, 2014, 3:06 PM
 */

#ifndef SPINBOX_EXPERT_H
#define	SPINBOX_EXPERT_H

#include "util/util.h"
#include "util/preprocessor.h"

#define SETTER_TO_SLOT_DEF(setter, args...) void set##setter (args);
#define SETTER_TO_SLOT_IMP(child, parent, setter, argc, args...) \
void child::set##setter ( ARGS_TO_ARGVAL(argc,args) ) { \
    parent::set##setter( ARGS_TO_VALUES(argc,args) ); \
} 

// TODO - in Qt 5 every member function can be used as a slot (or signal)

class SpinBoxExpert : public QSpinBox {
    Q_OBJECT
public:
    explicit SpinBoxExpert (QWidget * parent = 0);
    virtual ~SpinBoxExpert ();

    void enableConstraints ();
    void disableConstraints ();

public slots:
    
    // a Qt4.8 moc generatora nem tud macrokat kifejteni, sajnos :(
    // SETTER_TO_SLOT_DEF (Maximum, int)
    // SETTER_TO_SLOT_DEF (Minimum, int)
    // SETTER_TO_SLOT_DEF (Range, int, int)

    void setMaximum(int);
    void setMinimum(int);
    void setRange(int, int);
    
    void setRange (int min, int step, int max);
    void sl_incMaximum (int);
    void sl_decMaximum (int);

private slots:
    void psl_calcDiff (int);

signals:
    void sig_diffChanged (int);

private:
    int m_diff;
    int m_oldvalue;
};

//class DoubleSpinBoxExpert : public QDoubleSpinBox {
//    Q_OBJECT
//public:
//    explicit DoubleSpinBoxExpert (QWidget * parent = 0);
//    virtual ~DoubleSpinBoxExpert ();
//
//public slots:
//
//private:
//
//};

#endif	/* SPINBOX_EXPERT_H */

