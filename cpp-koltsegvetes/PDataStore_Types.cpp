/* 
 * File:   PDataStore_Types.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 1, 2014, 12:11 PM
 */

#include <QVariant>

#include "PDataStore.h"
#include "PDataStore_Types.h"
#include "PDataStore_Exception.h"

QString PTranTipus::toSqlInsert () const 
{
    return QString("values('%1')").arg(tip);
}

QString PTranTipus::toSqlUpdate () const 
{
    return "";
}

// ============================================================================================== //

QString PHistoryTuple::toSqlInsert () const 
{
    return QString("values(%1,'%2',%3)")
        .arg(trid)
        .arg(fsid)
        .arg(osszeg);
}

QString PHistoryTuple::toQslUpdate () const 
{
//    return QString("")
//        .arg(trid)
//        .arg(fsid)
//        .arg(osszeg);
    return "";
}

// ============================================================================================== //

QString PFolyoszamla::toSqlInsert () const 
{
    return QString("values('%1','%2','%3', %4)")
        .arg(fs_Id)
        .arg(fs_Nev)
        .arg(fs_Datum.toString(PDataStore::DATE_FORMAT))
        .arg(fs_Osszeg);
}

QString PFolyoszamla::toSqlUpdate () const 
{
    return QString("fs_Nev = '%2' , fs_Datum = '%3' , fs_Osszeg = %4 where fs_Id == '%1'")
        .arg(fs_Id)
        .arg(fs_Nev)
        .arg(fs_Datum.toString(PDataStore::DATE_FORMAT))
        .arg(fs_Osszeg);
}

PFolyoszamla PFolyoszamla::fromSqlQuery(const QSqlQuery& query)
{
    return PFolyoszamla(query.value(0).toString(), query.value(1).toString(),
        query.value(2).toInt(), QDate::fromString(query.value(3).toString(), PDataStore::DATE_FORMAT));
}
// ============================================================================================== //

PTranzakcio::PTranzakcio () : valid(false) { }

PTranzakcio::PTranzakcio (int osszeg, QString megj, QString tipus, QString szamlaid, QDate date, int regi_egyenleg)
: tr_Datum (date), tr_Osszeg (osszeg), tr_Megjegyzes (megj), tr_Tipus (tipus), tr_SzamlaId (szamlaid)
{
    kiadas = tipus.startsWith("KI_");
    tr_UjEgyenleg = regi_egyenleg + szorzo() * osszeg;
}

QString PTranzakcio::toSqlInsert () const
{
    return QString("values(NULL,'%1',%2,'%3','%4','%5',%6)")
        .arg(tr_Datum.toString(PDataStore::DATE_FORMAT))
        .arg(tr_Osszeg)
        .arg(tr_Megjegyzes)
        .arg(tr_Tipus)
        .arg(tr_SzamlaId)
        .arg(tr_UjEgyenleg);
}

QString PTranzakcio::toSqlUpdate () const
{
    return QString("tr_Datum = '%1', tr_Osszeg = %2, tr_Megjegyzes = '%3', tr_Tipus = '%4', tr_SzamlaId = '%5', tr_UjEgyenleg = %6 "
        "where tr_Id == %7")
        .arg(tr_Datum.toString(PDataStore::DATE_FORMAT))
        .arg(tr_Osszeg)
        .arg(tr_Megjegyzes)
        .arg(tr_Tipus)
        .arg(tr_SzamlaId)
        .arg(tr_UjEgyenleg)
        .arg(tr_Id);
    return "";
}

void PTranzakcio::fromTo (QString innenid, QString ideid) 
{
    tr_SzamlaId = innenid;
    moveTo(ideid);
}

void PTranzakcio::moveTo (QString celszamlaid) 
{
    tr_Tipus = PDataStore::ATHELYEZES_INNEN;
    tr_CelSzamlaId = celszamlaid;
    athelyezes = true;
    kiadas = true;
}

void PTranzakcio::otherSide () 
{
    tr_Tipus = PDataStore::ATHELYEZES_IDE;
    tr_SzamlaId = tr_CelSzamlaId;
    tr_UjEgyenleg = tr_CelUjEgyenleg;
    kiadas = false;
    athelyezes = false;
}

void PTranzakcio::changeDirection () 
{
    tr_Osszeg *= -1;
    std::swap(tr_SzamlaId, tr_CelSzamlaId);
    std::swap(tr_UjEgyenleg, tr_CelUjEgyenleg);
}

PTranzakcio PTranzakcio::fromOldRecord (const POldRecord& old, OldTranzactionType type) 
{
    PTranzakcio ret;
    ret.valid = true;
    ret.tr_Datum = old.m_date;
    ret.tr_Megjegyzes = old.m_comment;
    
    if (type == OldType_OtpBevetel)
    {
        ret.tr_UjEgyenleg = old.m_potpEgyenleg;
        ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_OTP;
        ret.tr_Tipus = PDataStore::ABSTRACT_BEVETEL;
        ret.tr_Osszeg = old.m_potpBevetel;
        ret.kiadas = false;
    }
    else if (type == OldType_OtpKivetel)
    {
        ret.fromTo(PDataStore::SZAMLA_PETI_OTP, PDataStore::SZAMLA_PETI_KEZ);
        ret.tr_UjEgyenleg = old.m_potpEgyenleg;
        ret.tr_CelUjEgyenleg = old.m_pkezEgyenleg;
        ret.tr_Osszeg = old.m_potpKivetel;
        if (ret.tr_Osszeg < 0) ret.changeDirection();
        ret.kiadas = true;
    }
    else if (type == OldType_OtpKoltseg)
    {
        ret.tr_UjEgyenleg = old.m_potpEgyenleg;
        ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_OTP;
        ret.tr_Tipus = PDataStore::ABSTRACT_KIADAS;
        ret.tr_Osszeg = old.m_potpKiadas;
        ret.kiadas = true;
    }
    else if (type == OldType_KezBevetel)
    {
        ret.tr_UjEgyenleg = old.m_pkezEgyenleg;
        ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_KEZ;
        ret.tr_Tipus = PDataStore::ABSTRACT_BEVETEL;
        ret.tr_Osszeg = old.m_pkezBevetel;
        ret.kiadas = false;
    }
    else if (type == OldType_KezKoltseg)
    {
        ret.tr_UjEgyenleg = old.m_pkezEgyenleg;
        ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_KEZ;
        ret.tr_Tipus = PDataStore::ABSTRACT_KIADAS;
        ret.tr_Osszeg = old.m_pkezKiadas;
        ret.kiadas = true;
    }
    else if (type == OldType_Alberlet)
    {
        ret.tr_Osszeg = old.m_alberletKoltseg;
        ret.tr_Tipus = PDataStore::KI_ALBERLET;
        
        if (old.m_alberletHonnan == POldRecord::SZAMLA_KEZ) 
        {
            ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_KEZ;
            ret.tr_UjEgyenleg = old.m_pkezEgyenleg;
        }
        else if (old.m_alberletHonnan == POldRecord::SZAMLA_OTP) 
        {
            ret.tr_SzamlaId = PDataStore::SZAMLA_PETI_OTP;
            ret.tr_UjEgyenleg = old.m_potpEgyenleg;
        }
        else ret.valid = false;
        ret.kiadas = true;
    }
            
    if (ret.tr_Osszeg == 0) ret.valid = false;
    return ret;
}

PTranzakcio PTranzakcio::fromSqlQuery (QSqlQuery query) 
{
//    426|14/09/02|65500|SZTAKI fizetes|BE_Akarmi|potp|442263
//    426|14/09/02|65500|SZTAKI fizetes|BE_Akarmi|potp|442263
//    0   1        2     3              4         5    6     
    if (!query.isValid()) throw PNotImplementedException("Not Implemented yet - ezt atirtam, ez allt itt: if (!query.next()) return ..", PCZ_DEBUG_INFO);
    auto ret = PTranzakcio(query.value(2).toInt(), query.value(3).toString(), query.value(4).toString(),
        query.value(5).toString(), QDate::fromString(query.value(1).toString(), PDataStore::DATE_FORMAT));
    ret.tr_UjEgyenleg = query.value(6).toInt();
    ret.tr_Id = query.value(0).toInt();
    return ret;
}

bool PTranzakcio::isMoveTranzaction () const 
{
    return athelyezes;
}

bool PTranzakcio::isValid () const 
{
    return valid;
}

int PTranzakcio::szorzo () const 
{
//    return kiadas;
    return !kiadas * 2 - 1;
}
