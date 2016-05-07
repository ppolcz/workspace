/* 
 * File:   cropdem_main.h
 * Author: polpe
 *
 * Created on February 18, 2014, 3:29 PM
 */

#ifndef CROPDEM_MAIN_H
#define	CROPDEM_MAIN_H

#include "util/util.h"
#include "widgets/cropdem_box.h"
#include "layouts/inputslayout.h"

class CropDemMain : public QMainWindow {
    Q_OBJECT
public:
    CropDemMain ();
    virtual ~CropDemMain ();
private:
    void p_setupUi();

    QSplitter * m_splitter;
    
    QFileSystemModel * m_model;
    QAbstractItemView * m_listw;
    
    QWidget * m_inputsw;
    InputsLayout * m_inputsl;
    
    QWidget * m_formsw;
    QGridLayout * m_formsl;
    
    CropDemBox * m_dembox;
};

#endif	/* CROPDEM_MAIN_H */

