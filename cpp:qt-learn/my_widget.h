/* 
 * File:   MyWidget.h
 * Author: deva
 *
 * Created on February 11, 2014, 5:42 PM
 */

#ifndef MYWIDGET_H
#define	MYWIDGET_H

#include <QWidget>

class MyWidget : public QWidget {
    Q_OBJECT
public:
    explicit MyWidget (QWidget * orig = 0);
    virtual ~MyWidget ();

    void newInstance ();
private:

signals:
    void triggerOK ();

public slots:
    static void staticslot ();
};

#endif	/* MYWIDGET_H */

