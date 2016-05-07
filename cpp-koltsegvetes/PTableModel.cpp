/* 
 * File:   PTableModel.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 2, 2014, 3:15 PM
 */

// MODEL-VIEW 2

#include <QColor>

#include "PTableModel.h"
#include "PDataStore.h"

PTableModel::PTableModel () { refresh(); }

PTableModel::~PTableModel () { }

void PTableModel::refresh () 
{
    setQuery(
        "select tr_Id, fs_Id, strftime('%Y.%m.%d. ', tr_Datum) || "
        "  case cast (strftime('%w', tr_Datum) as integer) "
        "    when 0 then 'Hetfo'"
        "    when 1 then 'Kedd'"
        "    when 2 then 'Szerda'"
        "    when 3 then 'Csutortok'"
        "    when 4 then 'Pentek'"
        "    when 5 then 'Szombat'"
        "    else 'Vasarnap' end as Datum, "
        "  fs_Nev as Folyoszamla, "
        "  CASE WHEN tr_Tipus LIKE 'KI_%' THEN tr_UjEgyenleg + tr_Osszeg ELSE tr_UjEgyenleg - tr_Osszeg END as 'Regi Egyenleg',"
        "  tr_Osszeg as Osszeg, "
        "  tr_UjEgyenleg as 'Uj Egyenleg', "
        "  tr_Tipus as Tipus, "
        "  tr_Megjegyzes as Megjegyzesek "
        "from Tranzakciok, Folyoszamlak "
        "where fs_Id == tr_SzamlaId "
//        "      AND " __STR(TR_SZAMLA_ID) " == 'potp' "
//        "group by " __STR(TR_ID) " " 
//        "sort by " __STR(TR_DATUM) " ASC "
        ";");

    col_trId = 0;
    col_fsId = 1;
    col_Datum = 2;
    col_fsNev = 3;
    col_regiEgyenleg = 4;
    col_ujEgyenleg = 5;
    col_osszeg = 6;
    col_tipus = 7;
    col_megj = 8;
}

QVariant PTableModel::data (const QModelIndex& i, int role) const 
{
    QVariant v = QSqlQueryModel::data(i, role);

    if (role == Qt::TextAlignmentRole)
        if (i.column() == col_Datum) return QVariant(Qt::AlignLeft | Qt::AlignVCenter);
        else if (i.column() == col_megj) return QVariant(Qt::AlignLeft | Qt::AlignVCenter);
        else if (i.column() == col_fsNev) return Qt::AlignCenter;
        else if (i.column() == col_regiEgyenleg) return Qt::AlignCenter;
        else if (i.column() == col_ujEgyenleg) return Qt::AlignCenter;
        else if (i.column() == col_osszeg) return Qt::AlignCenter;
        else if (i.column() == col_tipus) return Qt::AlignCenter;
        else return QVariant(Qt::AlignLeft | Qt::AlignVCenter);
    
    else if (role == Qt::BackgroundRole)
        /* Peti Otp sorok*/
        if (index(i.row(), col_fsId).data().toString() == PDataStore::SZAMLA_PETI_OTP) return QVariant(QColor(0x9F, 0xF7, 0x81));

    return v;
}
