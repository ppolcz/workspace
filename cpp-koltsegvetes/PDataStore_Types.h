/* 
 * File:   PDataStore_Types.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 1, 2014, 12:11 PM
 */

#ifndef PDATASTORE_TYPES_H
#define	PDATASTORE_TYPES_H

#include <polcz/pczfstream>
#include <boost/format.hpp>

#include <QDate>
#include <QString>
#include <QSqlQuery>

#include "POldRecord.h"

struct PTranTipus
{
    PTranTipus(QString const& tip) : tip(tip) { }
    QString tip;
    QString toSqlInsert() const;
    QString toSqlUpdate() const;
};

struct PFolyoszamla
{
    PFolyoszamla(QString id, QString nev, int osszeg = 0, QDate date = QDate::currentDate())
    : fs_Id(id), fs_Nev(nev), fs_Datum(date), fs_Osszeg(osszeg) { }
    
    QString fs_Id;
    QString fs_Nev;
    QDate fs_Datum;
    int fs_Osszeg;
    
    QString toSqlInsert() const;
    QString toSqlUpdate() const;
    static PFolyoszamla fromSqlQuery (QSqlQuery const& query);
};

struct PHistoryTuple
{
    int trid; // --> datum
    QString fsid; // --> szamla pontos neve
    int osszeg;
    
    QString toSqlInsert() const;
    QString toQslUpdate() const;
    static PHistoryTuple fromSqlQuery (QSqlQuery const& query);
};

struct PTranzakcio
{
    enum OldTranzactionType {
        OldType_KezKoltseg, OldType_KezBevetel, OldType_OtpKoltseg, OldType_OtpKivetel, OldType_OtpBevetel,
        OldType_Alberlet, OldType_AblerletUj
    };

    PTranzakcio();
    PTranzakcio(int osszeg, QString megj, QString tipus, QString szamlaid, QDate date = QDate::currentDate(), int regi_egyenleg = 0);
    
    operator bool () { return valid; }
    
    int tr_Id;
    QDate tr_Datum = QDate::currentDate();
    int tr_Osszeg;
    QString tr_Megjegyzes;
    QString tr_Tipus;
    QString tr_SzamlaId;
    int tr_UjEgyenleg;
    
    bool valid = true;
    bool kiadas;
    bool athelyezes = false;
    QString tr_CelSzamlaId;
    int tr_CelUjEgyenleg;
    
    QString toSqlInsert() const;
    QString toSqlUpdate() const;
    
    bool isValid() const;
    bool isMoveTranzaction() const;
    void fromTo(QString innenid, QString ideid);
    void moveTo(QString celszamlaid);
    void otherSide();
    void changeDirection();

    int szorzo() const;
    
    static PTranzakcio fromOldRecord(POldRecord const& old, OldTranzactionType type);
    static PTranzakcio fromSqlQuery (QSqlQuery query);
};

#endif	/* PDATASTORE_TYPES_H */

