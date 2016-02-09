/* 
 * File:   demviewer.h
 * Author: polpe
 *
 * Created on February 22, 2014, 12:55 PM
 */

#ifndef DEMVIEWER_H
#define	DEMVIEWER_H

#include <QGLViewer/qglviewer.h>

class DemViewer : public QGLViewer {
public:
    explicit DemViewer (QWidget * parent = 0);
    virtual ~DemViewer ();

protected:
    virtual void draw ();
    virtual void init ();

private:

};

#endif	/* DEMVIEWER_H */

