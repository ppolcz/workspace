/* 
 * File:   BigMainWindow.h
 * Author: polpe
 *
 * Created on January 14, 2014, 4:54 AM
 */

#ifndef _BIGMAINWINDOW_H
#define	_BIGMAINWINDOW_H

#include "ui_BigMainWindow.h"

#include <QGLWidget>

class BigMainWindow : public QMainWindow {
    Q_OBJECT
public:
    BigMainWindow();
    virtual ~BigMainWindow();

    void addGLWidget(QGLWidget * glw, QString title);
private:
    Ui::BigMainWindow widget;
};

#endif	/* _BIGMAINWINDOW_H */
