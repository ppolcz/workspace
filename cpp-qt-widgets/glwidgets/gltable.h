/* 
 * File:   gltable.h
 * Author: polpe
 *
 * Created on February 15, 2014, 11:48 PM
 */

#ifndef GLTABLE_H
#define	GLTABLE_H

#include "util/util.h"
#include <QGLWidget>

class GLTable : public QGLWidget {
public:
    explicit GLTable(QWidget * parent = 0);
    virtual ~GLTable();

protected:

    virtual void initializeGL();
    virtual void resizeGL(int w, int h);
    virtual void paintGL();

private:

};

#endif	/* GLTABLE_H */

