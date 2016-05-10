/* 
 * File:   cropdem_box.h
 * Author: polpe
 *
 * Created on February 15, 2014, 9:26 PM
 */

#ifndef CROPDEM_BOX_H
#define	CROPDEM_BOX_H

#include "util/util.h"
#include "widgets/inputbox.h"
#include "widgets/spinbox_expert.h"

class CropDemBox : public InputBox {
    Q_OBJECT
public:
    explicit CropDemBox (QWidget * parent = 0);
    virtual ~CropDemBox ();

    void setDemDimensions (int size_x, int size_y);
    int getOffsetX ();
    int getOffsetY ();
    int getSizeX ();
    int getSizeY ();

signals:
    void sig_update ();
    void sig_crop ();
    void sig_resize ();

private:
    void p_setupUi ();
    void p_setupLinks ();
    void p_destroyConstraints ();
    void p_createConstraints ();

    SpinBoxExpert * m_offsetX;
    SpinBoxExpert * m_offsetY;
    SpinBoxExpert * m_sizeX;
    SpinBoxExpert * m_sizeY;
    QLabel * m_demSizeX;
    QLabel * m_demSizeY;

    QPushButton * btn_update;
    QPushButton * btn_crop;
    QPushButton * btn_resize;

public:
    static const int WIDTH_TINY = 70;
    static const int WIDTH_SMALL = 100;
    static const int WIDTH_MEDIUM = 150;
    static const int WIDTH_BIG = 200;
    static const int WIDTH_LARGE = 250;
    static const int WIDTH_HUGE = 300;
};


#endif	/* CROPDEM_BOX_H */

