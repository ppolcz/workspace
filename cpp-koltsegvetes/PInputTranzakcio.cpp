/* 
 * File:   PInputTranzakcio.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on August 31, 2014, 9:13 PM
 */

#include <qt5/QtSql/qsqlquerymodel.h>
#include <qt5/QtSql/qsqltablemodel.h>
#include <qt5/QtWidgets/qpushbutton.h>

#include "PInputTranzakcio.h"
#include "PDataStore.h"

PInputTranzakcio::PInputTranzakcio (QWidget * parent) : PInputBox (parent) { setupUi(); }

PInputTranzakcio::~PInputTranzakcio () { }

void PInputTranzakcio::setupUi () 
{
    setTitle("Új tranzakció létrehozása");

    osszeg = new QLineEdit(this);
    addInput("Pénz összeg", osszeg);
    
    megj = new QTextEdit(this);
    addWidget(new QLabel("Megjegyzés"));
    addWidget(megj);
    
    calendar = new QCalendarWidget(this);
    addWidget(calendar);

    tipus = new QListView(this);
    tipus->setEditTriggers(QAbstractItemView::NoEditTriggers);
    addWidget(new QLabel("Költségvetés típus"));
    addWidget(tipus);
    
    szamla = new QTableView(this);
    szamla->setEditTriggers(QAbstractItemView::NoEditTriggers);
    addWidget(new QLabel("Számla"));
    addWidget(szamla);
    
    commit = new QPushButton("Jóváhagy", this);
    addWidget(commit);
    
    connect(commit, &QPushButton::pressed, [this]
    {
        collectValues();
        emit sig_newTranzakcio(tranzakcio);
    });
}

//void PInputTranzakcio::setupLinks () 
//{
//    connect(commit, &QPushButton::pressed, [this]
//    {
////        emit sig_newTranzakcio(osszeg->text().toInt(), megj->document()->toPlainText(),
////            tipus->currentIndex(), szamla->currentIndex(), calendar->selectedDate());
//        DEBUG_W(megj->document()->toPlainText().toStdString());
//        DEBUG_W(calendar->selectedDate().toString(PDataStore::DATE_FORMAT).toStdString());
////        DEBUG_W(tipus->currentIndex().column());
////        DEBUG_W(tipus->currentIndex().row());
//        DEBUG_W(tipus->currentIndex().data(Qt::DisplayRole ).toString().toStdString());
//        DEBUG_W(szamla->currentIndex().data(Qt::DisplayRole).toString().toStdString());
//        DEBUG_W(szamla->model()->index(szamla->currentIndex().row(), 0).data(Qt::DisplayRole).toString().toStdString());
//        DEBUG_W(szamla->model()->index(szamla->currentIndex().row(), 2).data(Qt::DisplayRole).toInt());
//    });
//}

void PInputTranzakcio::setupUi_Fill () 
{
    if (!tranzakcio) return;
    setTitle(QString("%1. tranzakció módosítása").arg(tranzakcio.tr_Id));
    
    osszeg->setText(QString::number(tranzakcio.tr_Osszeg));
    megj->setText(tranzakcio.tr_Megjegyzes);
    calendar->setSelectedDate(tranzakcio.tr_Datum);
    
    auto t_model = tipus->model();
    for (int i = 0; i < t_model->rowCount(); ++i)
    {
        if (t_model->index(i,0).data() == tranzakcio.tr_Tipus) tipus->setCurrentIndex(t_model->index(i,0));
    }
    
    auto sz_model = szamla->model();
    for (int i = 0; i < sz_model->rowCount(); ++i)
    {
        if (sz_model->index(i,0).data() == tranzakcio.tr_SzamlaId) szamla->setCurrentIndex(sz_model->index(i,0));
    }

    connect(commit, &QPushButton::pressed, [this]
    {
        collectValues();
        emit sig_updateTranzakcio(tranzakcio);
    });
}

void PInputTranzakcio::collectValues () 
{
    tranzakcio.tr_Datum = calendar->selectedDate();
    tranzakcio.tr_Megjegyzes = megj->document()->toPlainText();
    tranzakcio.tr_Osszeg = osszeg->text().toInt();
    tranzakcio.tr_SzamlaId = szamla->currentIndex().data().toString();
    tranzakcio.tr_Tipus = tipus->currentIndex().data().toString();
}

void PInputTranzakcio::setTipusModel (QAbstractItemModel* model, const QModelIndex& index) 
{
    if (tipus) 
    {
        tipus->setModel(model);
        tipus->setCurrentIndex(index);
        tipus->setFixedHeight(model->rowCount() * 16);
    }
}

void PInputTranzakcio::setSzamlaModel (QAbstractItemModel* model, const QModelIndex& index) 
{
    if (szamla) 
    {
        szamla->setModel(model);
        szamla->setCurrentIndex(index);
        szamla->setMinimumWidth(model->columnCount() * 110);
        szamla->setFixedHeight(model->rowCount() * 32 + 16);
    }
}

void PInputTranzakcio::setTranzakcio (const PTranzakcio& tr) 
{
    tranzakcio = tr;
    setupUi_Fill();
}
