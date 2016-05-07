/* 
 * File:   formtoolbox.h
 * Author: polpe
 *
 * Created on February 25, 2014, 12:48 PM
 */

#ifndef FORMTOOLBOX_H
#define	FORMTOOLBOX_H

#include "util/util.h"

class FormToolBox : public QToolBox {
public:
    explicit FormToolBox (QWidget * parent = 0);
    virtual ~FormToolBox ();
private:

};

#endif	/* FORMTOOLBOX_H */

