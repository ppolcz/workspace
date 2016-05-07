/* 
 * File:   PTableModel.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 2, 2014, 3:15 PM
 */

// MODEL-VIEW 2

#ifndef PTABLEMODEL_H
#define	PTABLEMODEL_H

#include <QSqlQueryModel>
#include <QModelIndex>

class PTableModel : public QSqlQueryModel
{
public:
    PTableModel ();
    virtual ~PTableModel ();
    virtual QVariant data (const QModelIndex& index, int role) const;

    void refresh ();
    
    int col_trId;
    int col_fsId;
    int col_Datum;
    int col_fsNev;
    int col_regiEgyenleg;
    int col_ujEgyenleg;
    int col_osszeg;
    int col_tipus;
    int col_megj;
    
private:

};

#endif	/* PTABLEMODEL_H */

