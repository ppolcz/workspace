/* 
 * File:   cropdem_main.cpp
 * Author: polpe
 * 
 * Created on February 18, 2014, 3:29 PM
 */

#include "cropdem_main.h"
#include "widgets/listview.h"

CropDemMain::CropDemMain () {
    p_setupUi();
}

CropDemMain::~CropDemMain () { }

void CropDemMain::p_setupUi () {

    m_splitter = new QSplitter(this);

    m_model = new QFileSystemModel;
    m_model->setRootPath(QDir::rootPath());
    m_listw = new NavigableListView();
    m_listw->setModel(m_model);
    m_listw->setRootIndex(m_model->index(QDir::homePath()));
    m_listw->setMinimumWidth(VAL_LISTVIEW_MIN_WIDTH);

    m_inputsw = new QWidget;
    m_inputsl = new InputsLayout(m_inputsw);

    m_formsw = new QWidget;
    m_formsl = new QGridLayout(m_formsw);
    m_formsl->setVerticalSpacing(VAL_SPACING_SMALL);
    m_formsl->setHorizontalSpacing(VAL_SPACING_SMALL);

    m_dembox = new CropDemBox;
    m_dembox->setDemDimensions(1000, 1000);

    this->setCentralWidget(m_splitter);
    {
        m_splitter->addWidget(m_listw);
        m_splitter->addWidget(m_inputsw);
        m_splitter->addWidget(m_formsw);
        {
            m_formsl->addWidget(m_dembox);
        }
    }
}
