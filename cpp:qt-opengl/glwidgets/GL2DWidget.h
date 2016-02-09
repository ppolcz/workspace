/* 
 * File:   GL2DWidget.h
 * Author: polpe
 *
 * Created on February 10, 2014, 3:51 PM
 */

#ifndef GL2DWIDGET_H
#define	GL2DWIDGET_H

#include <QGLWidget>

class GL2DWidget : public QGLWidget {
public:
    explicit GL2DWidget(QWidget * parent = 0);
    virtual ~GL2DWidget();

protected:

    virtual void initializeGL();
    virtual void resizeGL(int w, int h);
    virtual void paintGL();

private:

};

#endif	/* GL2DWIDGET_H */

