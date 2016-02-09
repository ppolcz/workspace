/* 
 * File:   PDataStore.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on August 30, 2014, 11:56 PM
 */

#ifndef PDATASTORE_H
#define	PDATASTORE_H

#include <polcz/pczstring.h>
#include <polcz/pczfstream.h>

#include <QSqlDatabase>
#include <QStringList>
#include <QDate>

#include "PDataStore_Exception.h"
#include "PDataStore_Preprocessor.h"
#include "PDataStore_Types.h"

/**
 * select * from Tranzakciok, Folyoszamlak where fs_Id = tr_SzamlaId; 
 */

#define FS_ID fs_Id
#define FS_NEV fs_Nev
#define FS_DATUM fs_Datum
#define FS_OSSZEG fs_Osszeg

#define TR_ID tr_Id
#define TR_DATUM tr_Datum
#define TR_OSSZEG tr_Osszeg
#define TR_MEGJ tr_Megjegyzes
#define TR_TIPUS tr_Tipus
#define TR_SZAMLA_ID tr_SzamlaId
#define TR_UJ_EGYENLEG tr_UjEgyenleg    

class PDataStore : public QObject
{
    Q_OBJECT
public:
    static PDataStore* singleton();

    static const string LOG_DIR;
    
    static const QString SZAMLA_PETI_OTP;
    static const QString SZAMLA_PETI_KEZ;
    static const QString SZAMLA_DORI_OTP;
    static const QString SZAMLA_DORI_KEZ;

    static const QString DATE_FORMAT;
    static const QString DEFAULT_TRAN_TIPUS;
    static const QString DEFAULT_FOLYOSZAMLA;
    static const QString ATHELYEZES_INNEN;
    static const QString ATHELYEZES_IDE;
    static const QString ABSTRACT_BEVETEL;
    static const QString ABSTRACT_KIADAS;    
    static const QString KI_ALBERLET;
    static const QString KORRIGALAS;

    void open ();
    void close ();
    void reset ();
    void readOldDatabase (string const& fn);
    void logDatabase (string const& dirn);
    
    QSqlQuery findRecord(QString const& tablename, QString const& conditions, QString const& values = "*");
    
    PCZ_DECLARE_TABLE(Folyoszamlak, PFolyoszamla)
    PCZ_DECLARE_TABLE(Tranzakciok, PTranzakcio)
    PCZ_DECLARE_TABLE(TranTipusok, PTranTipus)
    
    QSqlDatabase get() const;

private:
    PDataStore (QString const& fn);
    virtual ~PDataStore ();
    
    QSqlDatabase db;
    std::vector<PFolyoszamla> folyoszamlak;
};

inline ostream& operator << (ostream& s, const PTranzakcio& tr)
{
    s << boost::format("%d|%s|%d|%s|%s|%s|%d|%d|%d|%d|%s|%d\n")
        % tr.tr_Id % tr.tr_Datum.toString(PDataStore::DATE_FORMAT).toStdString() % tr.tr_Osszeg 
        % tr.tr_Megjegyzes.toStdString() % tr.tr_Tipus.toStdString() % tr.tr_SzamlaId.toStdString()
        % tr.tr_UjEgyenleg % tr.valid % tr.kiadas % tr.athelyezes 
        % tr.tr_CelSzamlaId.toStdString() % tr.tr_CelUjEgyenleg;
    return s;
}

inline istream& operator >> (istream& s, const PTranzakcio& tr)
{
    
//    s << boost::format("%d|%s|%d|%s|%s|%s|%d|%d|%d|%d|%s|%d\n")
//        % tr.tr_Id % tr.tr_Datum.toString(PDataStore::DATE_FORMAT).toStdString() % tr.tr_Osszeg 
//        % tr.tr_Megjegyzes.toStdString() % tr.tr_Tipus.toStdString() % tr.tr_SzamlaId.toStdString()
//        % tr.tr_UjEgyenleg % tr.valid % tr.kiadas % tr.athelyezes 
//        % tr.tr_CelSzamlaId.toStdString() % tr.tr_CelUjEgyenleg;
    (void) tr;
    return s;
}

#endif	/* PDATASTORE_H */

