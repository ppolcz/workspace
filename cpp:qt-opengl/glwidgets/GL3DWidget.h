/* 
 * File:   GL3DWidget.h
 * Author: polpe
 *
 * Created on January 30, 2014, 7:44 PM
 */

#ifndef GL3DWIDGET_H
#define	GL3DWIDGET_H
#define private_methods private
#define private_fields private
#define constants private

#include <QtOpenGL>
#include <string>

class GL3DWidget : public QGLWidget {
    Q_OBJECT

public:
    explicit GL3DWidget(QWidget * parent = 0);
    virtual ~GL3DWidget();

protected:
    // overridden from QGLWidget
    // virtual void paintEvent(QPaintEvent*);
    // virtual void resizeEvent(QResizeEvent*);
    virtual void mousePressEvent(QMouseEvent *);
    virtual void mouseReleaseEvent(QMouseEvent *);
    virtual void mouseDoubleClickEvent(QMouseEvent *);
    virtual void mouseMoveEvent(QMouseEvent *);
    virtual void wheelEvent(QWheelEvent *);
    virtual void initializeGL();
    virtual void resizeGL(int w, int h);
    virtual void paintGL();

    virtual void paintBasePlain();

private_fields:
    double p_camPos = -10.0f;
    double p_zNear = -5.0f;
    double p_zFar = 5.0f;

constants:
    static const float s_vertexBuffer[];
    static const float s_colorBuffer[];

private_methods:
    void pm_printMatrix(GLenum matrix_name, const char * comment = "the matrix");
    void pm_drawCoordinateSystem();
};

#define P_CLEAR_RED 1
#define P_CLEAR_GREEN 1 
#define P_CLEAR_BLUE 1 
#define P_CLEAR_ALPHA 0

#define P_DEFAULT_FOV_DEG 60.0f

#endif	/* GL3DWIDGET_H */

