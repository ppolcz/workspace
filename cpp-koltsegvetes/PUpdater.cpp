/* 
 * File:   PUpdater.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 5, 2014, 8:06 AM
 */

#include <QSqlQuery>

#include "PUpdater.h"
#include "PDataStore.h"

PUpdater::PUpdater(UpdateMode mode) : mode(mode) { }

PUpdater::~PUpdater () { }

void PUpdater::run () 
{
    static const int NO_VAL = -999999;
    
    QSqlQuery query = PDataStore::singleton()->listTableTranzakciok();
    PTranzakcio tr;
    
    int otp_egyenleg = NO_VAL;
    int kezpenz = NO_VAL;

    auto update = [&tr, this] (int& egyenleg) 
    {
        int regi_egyenleg = tr.tr_UjEgyenleg - tr.szorzo() * tr.tr_Osszeg;
        if (egyenleg != regi_egyenleg)
        {
            if (egyenleg == NO_VAL) egyenleg = tr.tr_UjEgyenleg;
            else if (mode == Test)
            {
                PCZ_DEBUG("INKONZISZTENCIA: tr[i-1].uj_egyenleg != tr[i].regi_egyenleg : %d != %d", egyenleg, regi_egyenleg);
                return false;
            }
            else if (mode == Update)
            {
                std::cout << "\nJavitok:\n";
                std::cout << tr.toSqlInsert().toStdString() << std::endl;
                tr.tr_UjEgyenleg = egyenleg + tr.szorzo() * tr.tr_Osszeg;
                PDataStore::singleton()->updateTableTranzakciok(tr, PCZ_DEBUG_INFO);
                std::cout << tr.toSqlInsert().toStdString() << std::endl;
            }
        }
        egyenleg = tr.tr_UjEgyenleg;
        return true;
    };
    
    while (query.next())
    {
        tr = PTranzakcio::fromSqlQuery(query);
        
        if (tr.tr_SzamlaId == PDataStore::SZAMLA_PETI_OTP && update(otp_egyenleg))
        {
            continue;
        }
        else if (tr.tr_SzamlaId == PDataStore::SZAMLA_PETI_KEZ && update(kezpenz)) 
        {
            continue;
        }
        
        throw PModelError("The database is inconsistent!", PCZ_DEBUG_INFO);
    }
}
