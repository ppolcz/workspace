/* 
 * File:   POldRecord.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 1, 2014, 11:21 PM
 */

#ifndef POLDRECORD_H
#define	POLDRECORD_H

#include <polcz/pczstring.h>

#include <QDate>
#include <QString>

struct POldRecord
{
    static const string NO_VAL;
    static const string DELIMITER;
    static const QString DATE_FORMAT;
    
    static const QString SZAMLA_OTP;
    static const QString SZAMLA_KEZ;
    
    QDate m_date;
    int m_potpEgyenleg;
    int m_potpBevetel;
    int m_potpKivetel;
    int m_potpKiadas;
    int m_pkezEgyenleg;
    int m_pkezBevetel;
    int m_pkezKiadas;
    int m_alberletKoltseg;
    QString m_alberletHonnan;
    int m_mastolOsszeg;
    QString m_mastolKicsoda;
    QString m_comment;
    int m_htOtp;
    int m_htKez;
    
    bool loadFromString (const string& line);
};

#endif	/* POLDRECORD_H */

