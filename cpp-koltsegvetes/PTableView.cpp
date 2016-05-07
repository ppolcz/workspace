/* 
 * File:   PTableView.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 2, 2014, 2:49 PM
 */

// MODEL-VIEW 2

#include <polcz/pczdebug.h>

#include <QSqlQueryModel>
#include <QHeaderView>

#include "PTableView.h"

PTableView::PTableView (QWidget * parent) : QTableView (parent) { setupUi(); }

PTableView::~PTableView () { }

void PTableView::setModel (QAbstractItemModel* model) 
{
    QTableView::setModel(model);
    customize((PTableModel*)model);
}


void PTableView::setupUi () 
{
    connect(this, &QTableView::doubleClicked, [this] (const QModelIndex& ) {
        for (int i = 0; i < currentIndex().model()->columnCount(); ++i)
        {
            PCZ_DEBUG("tview.col[%d] = %d", i, columnWidth(i));
        }
        PCZ_DEBUG();
    });
}

void PTableView::customize(PTableModel* model) 
{
    hideColumn(model->col_fsId);
    hideColumn(model->col_trId);
    
    int COL_WIDTH_DATE = 148;
    int COL_WIDTH_SZAMLA = 100;
    int COL_WIDTH_REGI_EGYENLEG = 93;
    int COL_WIDTH_EGYENLEG = 86;
    int COL_WIDTH_OSSZEG = 86;
    int COL_WIDTH_TIPUS = 135;
    int COL_WIDTH_MEGJ = 745;

    setColumnWidth(model->col_Datum, COL_WIDTH_DATE);
    setColumnWidth(model->col_fsNev, COL_WIDTH_SZAMLA);
    setColumnWidth(model->col_regiEgyenleg, COL_WIDTH_REGI_EGYENLEG);
    setColumnWidth(model->col_ujEgyenleg, COL_WIDTH_EGYENLEG);
    setColumnWidth(model->col_osszeg, COL_WIDTH_OSSZEG);
    setColumnWidth(model->col_tipus, COL_WIDTH_TIPUS);
    setColumnWidth(model->col_megj, COL_WIDTH_MEGJ);

//    horizontalHeader()->setDefaultAlignment(Qt::AlignCenter);
//    horizontalHeader()->setResizeMode(QHeaderView::ResizeToContents)
//    horizontalHeader()->setResizeMode(QHeaderView::ResizeToContents)
}
