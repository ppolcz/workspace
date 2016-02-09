/* 
 * File:   PDataStore.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on August 30, 2014, 11:56 PM
 */

#include <polcz/pczdebug.h>
#include <polcz/pczfstream>

#include <QSqlQuery>
#include <QVariant>
#include <QDir>

#include "strtk.hpp"
#include "PDataStore.h"
#include "POldRecord.h"

PDataStore* PDataStore::singleton()
{
    static QString dbname = "fileName.db3";
    static PDataStore DATA_STORE(dbname);
    return &DATA_STORE;
}

const string PDataStore::LOG_DIR = "/home/polpe/Dropbox/__mentesek/koltsegvetes";

const QString PDataStore::SZAMLA_PETI_OTP = "potp";
const QString PDataStore::SZAMLA_PETI_KEZ = "pkez";
const QString PDataStore::SZAMLA_DORI_OTP = "dotp";
const QString PDataStore::SZAMLA_DORI_KEZ = "dkez";

const QString PDataStore::DATE_FORMAT = "yyyy-MM-dd";
const QString PDataStore::DEFAULT_FOLYOSZAMLA = SZAMLA_PETI_KEZ;
const QString PDataStore::DEFAULT_TRAN_TIPUS = "KI_Etel";
const QString PDataStore::ATHELYEZES_INNEN = "KI_Athelyezes_Innen";
const QString PDataStore::ATHELYEZES_IDE = "BE_Athelyezes_Ide";
const QString PDataStore::ABSTRACT_BEVETEL = "BE_Akarmi";
const QString PDataStore::ABSTRACT_KIADAS = "KI_Akarmi";
const QString PDataStore::KI_ALBERLET = "KI_Alberlet";
const QString PDataStore::KORRIGALAS = "BE_Korrigalas";

PDataStore::PDataStore (const QString& fn) : db(QSqlDatabase::addDatabase("QSQLITE"))
{
    db.setDatabaseName(fn);
}

PDataStore::~PDataStore () 
{ try { close(); } catch (PDBIsNotOpenException& e) { } }

void PDataStore::open () 
{
    if (!db.isOpen()) /* throw PDBIsOpenException("", PCZ_DEBUG_INFO); */ db.open();
}

void PDataStore::close () 
{
    if (db.isOpen()) /* throw PDBIsNotOpenException("", PCZ_DEBUG_INFO); */ db.close();
}

QSqlDatabase PDataStore::get () const 
{
    return db;
}

void PDataStore::reset () 
{
    open();
    try { dropTableFolyoszamlak(); } catch (...) { }
    try { dropTableTranzakciok(); } catch (...) { }
    try { dropTableTranTipusok(); } catch (...) { }
    createTableFolyoszamlak();
    createTableTranzakciok();
    createTableTranTipusok();
    
    PCZ_DEBUG_W(insertTableFolyoszamlak(PFolyoszamla(SZAMLA_PETI_KEZ, "Peti keszpenz", 15000)));
    PCZ_DEBUG_W(insertTableFolyoszamlak(PFolyoszamla(SZAMLA_PETI_OTP, "Peti Otp Bank", 300000)));
    PCZ_DEBUG_W(insertTableFolyoszamlak(PFolyoszamla(SZAMLA_DORI_KEZ, "Dori keszpenz", 0)));
    PCZ_DEBUG_W(insertTableFolyoszamlak(PFolyoszamla(SZAMLA_DORI_OTP, "Dori Otp Bank", 0)));

    insertTableTranTipusok(PTranTipus(DEFAULT_TRAN_TIPUS));
    insertTableTranTipusok(PTranTipus(ABSTRACT_BEVETEL));
    insertTableTranTipusok(PTranTipus(ABSTRACT_KIADAS));
    insertTableTranTipusok(PTranTipus("BE_Osztondij"));
    insertTableTranTipusok(PTranTipus("BE_Szulok"));
    insertTableTranTipusok(PTranTipus("KI_Butor"));
    insertTableTranTipusok(PTranTipus("KI_Haztartasi"));
    insertTableTranTipusok(PTranTipus("KI_Oltozkodes"));
    insertTableTranTipusok(PTranTipus("KI_Mobil"));
    insertTableTranTipusok(PTranTipus("KI_Luxus"));
    insertTableTranTipusok(PTranTipus("KI_Rezsi"));
    insertTableTranTipusok(PTranTipus(KI_ALBERLET));
    insertTableTranTipusok(PTranTipus("Z_ELBIRALAS_ALATT"));
    insertTableTranTipusok(PTranTipus(ATHELYEZES_INNEN));
    insertTableTranTipusok(PTranTipus(ATHELYEZES_IDE));
    insertTableTranTipusok(PTranTipus(KORRIGALAS));
}

void PDataStore::readOldDatabase (string const& fn) 
{
    ifstream fs(fn);
    string line;
    POldRecord old;
    PTranzakcio tr;
    
    getline(fs, line);
    getline(fs, line);
    getline(fs, line);
    getline(fs, line);
    
    if (!old.loadFromString(line)) return;
    
    int otp = old.m_potpEgyenleg;
    int otp_d = old.m_potpEgyenleg;
    int kez = old.m_pkezEgyenleg;
    int kez_d = old.m_pkezEgyenleg;
    
    while (true) 
    {
        #define __UPDATE_AND_INSERT(szamlaid, egyenleg) \
        if (tr.valid && tr.tr_SzamlaId == szamlaid) \
        { \
            egyenleg += tr.tr_Osszeg * tr.szorzo(); \
            tr.tr_UjEgyenleg = egyenleg; \
            insertTableTranzakciok(tr, PCZ_DEBUG_INFO); \
        } 
        
        getline(fs, line);
        if (!old.loadFromString(line)) break;

        otp_d = old.m_potpEgyenleg;
        kez_d = old.m_pkezEgyenleg;

        
        // ----------------------------------------
        // KESZPENZ
        
        /* Keszpenzt kaptam */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_KezBevetel);
        __UPDATE_AND_INSERT(SZAMLA_PETI_KEZ, kez)

        /* Keszpenzben fizettem */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_KezKoltseg);
        __UPDATE_AND_INSERT(SZAMLA_PETI_KEZ, kez)

        // ----------------------------------------
        // OTP SZAMLA
        
        /* OTP-re kaptam penzt */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_OtpBevetel);
//        if (tr.valid) std::cout << tr.toSqlInsert().toStdString() << std::endl;
//        PCZ_DEBUG("ods szerint = %d, szamolas szerint = %d", otp_d, otp);
        __UPDATE_AND_INSERT(SZAMLA_PETI_OTP, otp)

        /* OTP-rol fizettem */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_OtpKoltseg);
//        if (tr.valid) std::cout << tr.toSqlInsert().toStdString() << std::endl;
//        PCZ_DEBUG("ods szerint = %d, szamolas szerint = %d", otp_d, otp);
        __UPDATE_AND_INSERT(SZAMLA_PETI_OTP, otp)

        // ----------------------------------------
        // OTP SZAMLA - KESZPENZ ATHELYEZES
        
        /* Kivetel egyik helyrol */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_OtpKivetel);
//        if (tr.valid) std::cout << tr.toSqlInsert().toStdString() << std::endl;
//        DEBUG("ods szerint = %d, szamolas szerint = %d", otp_d, otp);
        __UPDATE_AND_INSERT(SZAMLA_PETI_OTP, otp) /* otp-bol KI */
        __UPDATE_AND_INSERT(SZAMLA_PETI_KEZ, kez) /* otp-be be - azaz a kezpenzbol KIveszek */

        /* Berakas masik helyre */
        tr.otherSide();
//        if (tr.valid) std::cout << tr.toSqlInsert().toStdString() << std::endl;
//        PCZ_DEBUG("ods szerint = %d, szamolas szerint = %d", otp_d, otp);
        __UPDATE_AND_INSERT(SZAMLA_PETI_OTP, otp) /* az otp-be BErakok penzt */
        __UPDATE_AND_INSERT(SZAMLA_PETI_KEZ, kez) /* az otp-bol vettem ki, azaz a keszpenzbe BEraktam */
            
        // ----------------------------------------
        // ALBERLET
        
        /* Alberletet fizettem */
        tr = PTranzakcio::fromOldRecord(old, PTranzakcio::OldType_Alberlet);
//        if (tr.valid) std::cout << tr.toSqlInsert().toStdString() << std::endl;
//        PCZ_DEBUG("ods szerint = %d, szamolas szerint = %d", otp_d, otp);
        __UPDATE_AND_INSERT(SZAMLA_PETI_OTP, otp) /* OTP-rol */
        __UPDATE_AND_INSERT(SZAMLA_PETI_KEZ, kez) /* keszpenzben */
        
        // ----------------------------------------
        // KORRIGALAS
            
        if (otp != otp_d)
        {
            PCZ_DEBUG("KORRIGALAS - OTP: %d expected: %d", otp, otp_d);
            tr = PTranzakcio(otp_d - otp, "Korrigalas [ennyi van pluszba]", PDataStore::KORRIGALAS, PDataStore::SZAMLA_PETI_OTP, old.m_date, otp);
            insertTableTranzakciok(tr, PCZ_DEBUG_INFO);
//            PCZ_DEBUG_W(otp_d);
//            PCZ_DEBUG_W(otp);
//            PCZ_DEBUG_W(tr.tr_UjEgyenleg);
            otp = otp_d;
            assert(tr.tr_UjEgyenleg == otp_d);
        }

        if (kez != kez_d)
        {
            PCZ_DEBUG("KORRIGALAS - KESZPENZ: %d expected: %d", kez, kez_d);
            tr = PTranzakcio(kez_d - kez, "Korrigalas [ennyi van pluszba]", PDataStore::KORRIGALAS, PDataStore::SZAMLA_PETI_KEZ, old.m_date, kez);
            insertTableTranzakciok(tr, PCZ_DEBUG_INFO);
//            PCZ_DEBUG_W(kez_d - kez);
//            PCZ_DEBUG_W(kez_d);
//            PCZ_DEBUG_W(kez);
//            PCZ_DEBUG_W(tr.tr_UjEgyenleg);
//            PCZ_DEBUG_W(tr.kiadas);
//            PCZ_DEBUG_W(tr.szorzo());
            kez = kez_d;
            assert(tr.tr_UjEgyenleg == kez_d);
        }
        
        #undef __UPDATE_AND_INSERT   
    }
    PCZ_DEBUG("Otp szamlan utolso osszeg: %d", otp);
    PCZ_DEBUG("Keszpenzben utolso osszeg: %d", kez);
}

void PDataStore::logDatabase (const string& dirn) 
{
    QDir dir(dirn.c_str());
    if (!dir.exists()) dir.mkpath(".");
    
    ofstream fs(dirn + "/koltsegvetes_" + QDateTime::currentDateTime().toString("yyyy-MM-dd_HH:mm:ss_zzz").toStdString() + ".log");
    
    auto q = listTableTranzakciok(PCZ_DEBUG_INFO);
    PTranzakcio tr;
    while (q.next())
    {
        tr = PTranzakcio::fromSqlQuery(q);
        fs << tr;
    }
}


QSqlQuery PDataStore::findRecord (const QString& tablename, const QString& conditions, const QString& values) 
{
    return db.exec(QString("select %3 from %1 where %2;").arg(tablename).arg(conditions).arg(values));
}

PCZ_DEFINE_TABLE( TranTipusok, PTranTipus, 
    tt varchar(20) primary key not null unique )

PCZ_DEFINE_TABLE( Folyoszamlak, PFolyoszamla,
    fs_Id varchar(8) primary key not null unique,
    fs_Nev varchar(20), 
    fs_Datum varchar(50), 
    fs_Osszeg integer )

PCZ_DEFINE_TABLE( Tranzakciok, PTranzakcio,
    TR_ID integer primary key autoincrement not null unique,
    TR_DATUM varchar(50),
    TR_OSSZEG integer,
    TR_MEGJ varchar(200),
    TR_TIPUS varchar(30),
    TR_SZAMLA_ID varchar(8),
    TR_UJ_EGYENLEG integer,
    foreign key (TR_SZAMLA_ID) references Folyoszamlak(FS_ID))
