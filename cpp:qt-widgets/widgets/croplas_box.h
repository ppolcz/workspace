/* 
 * File:   croplas_box.h
 * Author: polpe
 *
 * Created on February 25, 2014, 12:18 PM
 */

#ifndef CROPLAS_BOX_H
#define	CROPLAS_BOX_H

#include "inputbox.h"
#include "util/util.h"
#include "appendablelineedit.h"
#include "spinbox_expert.h"

class CropLasBox : public InputBox {
    Q_OBJECT
public:
    explicit CropLasBox (QWidget * parent = 0);
    virtual ~CropLasBox ();

    double getYmax () const;
    double getXmax () const;
    double getYmin () const;
    double getXmin () const;
    int getDecimate () const;
    int getKNeigh () const;

    QStringList getInputs () const;
    QString getSavename () const;

signals:
    void sig_crop ();
    void sig_remove_duplicates ();
    void sig_clipDimensions (double, double, double, double);

private slots:
    void psl_somethingChanged(int);

private:
    void p_setupUi ();
    void p_setupLinks ();

    AppendableLineEdit * m_inputs;

    SpinBoxExpert * m_xmin;
    SpinBoxExpert * m_ymin;
    SpinBoxExpert * m_xmax;
    SpinBoxExpert * m_ymax;
    QSpinBox * m_decimate;
    QLineEdit * m_savename;
    QPushButton * btn_crop;

    QLineEdit * m_removable;
    QSpinBox * m_kneigh;
    QPushButton * btn_remove_duplicates;
};

#endif	/* CROPLAS_BOX_H */

