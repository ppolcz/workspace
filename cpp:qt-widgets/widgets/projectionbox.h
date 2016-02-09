/* 
 * File:   projectionbox.h
 * Author: polpe
 *
 * Created on February 22, 2014, 8:38 PM
 */

#ifndef PROJECTIONBOX_H
#define	PROJECTIONBOX_H

#include "util/util.h"
#include "inputbox.h"

class ProjectionBox : public InputBox {
    Q_OBJECT
public:
    explicit ProjectionBox (QWidget * parent = 0);
    virtual ~ProjectionBox ();

    QString filename ();
    QString savename ();
    int imageWidth ();

signals:
    void sig_process ();

private:
    void p_setupUi ();
    void p_setupLinks ();

    QLineEdit * le_filename;
    QLineEdit * le_savename;
    QSpinBox * sb_width;
    QPushButton * btn_project;
};

#endif	/* PROJECTIONBOX_H */

