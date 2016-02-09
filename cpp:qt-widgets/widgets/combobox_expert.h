/* 
 * File:   combobox_expert.h
 * Author: polpe
 *
 * Created on February 17, 2014, 9:38 AM
 */

#ifndef COMBOBOX_EXPERT_H
#define	COMBOBOX_EXPERT_H

#include "util/util.h"

class ComboBoxExpert : public QComboBox {
    Q_OBJECT
public:
    explicit ComboBoxExpert (QWidget * parent = 0);
    virtual ~ComboBoxExpert ();

private slots:
    void psl_indexChanged (int index);

signals:
    void sig_indexChanged (QComboBox*, int);

private:
    void p_createConnections ();

};

#endif	/* COMBOBOX_EXPERT_H */

