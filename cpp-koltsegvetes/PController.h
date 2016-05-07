/* 
 * File:   PController.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on August 31, 2014, 11:42 PM
 */

#ifndef PCONTROLLER_H
#define	PCONTROLLER_H

#include <QModelIndex>
#include <QSqlRecord>
#include <QSqlTableModel>
#include <QSqlQueryModel>

#include "PDataStore.h"
#include "PInputTranzakcio.h"
#include "PTableView.h"

class PController : QObject
{
    Q_OBJECT
public:
    PController ();
    virtual ~PController ();

    void setupInputTranzakcio (PInputTranzakcio * i, bool updater = false);
    void setupTableView (PTableView * tw);

private:
    PDataStore * db;
    QSqlTableModel t_folyoszamlak;
    QSqlQueryModel t_tranTipusok;
    
};

#endif	/* PCONTROLLER_H */

