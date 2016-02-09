/* 
 * File:   PDataStore_Preprocessor.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 1, 2014, 12:09 PM
 */

#ifndef PDATASTORE_PREPROCESSOR_H
#define	PDATASTORE_PREPROCESSOR_H

//#include <boost/preprocessor/tuple/rem.hpp>
#include <QDebug>

#define PABSTRACT_TABLE_CONSTRUCTOR(name) name (const QSqlDatabase& db) : PAbstractTable(db) { } 
#define PABSTRACT_TABLE_INIT(create, drop) void init () { cmd_createTable = create; cmd_dropTable = drop; }

#define __STR_WEEK(args...) #args
#define __STR(args...) __STR_WEEK(args)

#define PCZ_DECLARE_TABLE(name, struct_name) \
    static QString Table_##name(); \
    void createTable##name (PCZ_DINFO_ARG_); \
    void dropTable##name (PCZ_DINFO_ARG_); \
    int insertTable##name (struct_name const&, PCZ_DINFO_ARG_); \
    int updateTable##name (struct_name const&, PCZ_DINFO_ARG_); \
    QSqlQuery listTable##name (PCZ_DINFO_ARG_) const;

#define PCZ_DEFINE_TABLE(name, struct_name, cmd...) \
QString PDataStore::Table_##name() { \
    static QString NAME = #name; \
    return NAME; } \
void PDataStore::createTable##name (PCZ_DINFO_ARG) \
{ \
    if (db.isOpen()) \
        if (!db.tables().contains(#name)) \
        { \
            /* PCZ_DEBUG("create table " #name "(" __STR(cmd) ");"); */ \
            if (!QSqlQuery().exec("create table " #name "(" __STR(cmd) ");")) \
                throw PSqlCommandError("table " #name " cannot be created due to an error!", PCZ_ADD_DINFO); \
        } \
        else throw PTableExists("table " #name " already exists!", PCZ_ADD_DINFO); \
    else throw PDBIsNotOpenException("database is not open!", PCZ_ADD_DINFO); \
} \
void PDataStore::dropTable##name (PCZ_DINFO_ARG) { \
    if (db.isOpen()) if (db.tables().contains(#name)) { if (!QSqlQuery().exec("drop table if exists " #name ";")) \
    throw PSqlCommandError("table " #name " cannot be dropped due to an error!", PCZ_ADD_DINFO); } \
    else throw PTableDoesntExists("table " #name " does not exists!", PCZ_ADD_DINFO); \
    else throw PDBIsNotOpenException("database is not open!", PCZ_ADD_DINFO); } \
int PDataStore::insertTable##name (struct_name const& obj, PCZ_DINFO_ARG) { \
    if (db.isOpen()) \
        if (db.tables().contains(#name))  \
            if (QSqlQuery().exec(QString("insert into " #name " %1;").arg(obj.toSqlInsert()))) { \
                QSqlQuery query("select last_insert_rowid()"); \
                if (query.next()) return query.value(0).toInt(); \
            } else throw PSqlCommandError("data cannot be inserted into table " #name " due to an error!\n" + \
                                          QString("insert into " #name " %1;").arg(obj.toSqlInsert()).toStdString(), \
                                          PCZ_ADD_DINFO); \
        else throw PTableDoesntExists("table " #name " does not exists!", PCZ_ADD_DINFO); \
    else throw PDBIsNotOpenException("database is not open!", PCZ_ADD_DINFO); return -1; } \
int PDataStore::updateTable##name (struct_name const& obj, PCZ_DINFO_ARG) { \
    if (db.isOpen() && db.isValid()) \
        if (db.tables().contains(#name)) { \
            if (QSqlQuery().exec(QString("update " #name " set %1;").arg(obj.toSqlUpdate()))) { \
                QSqlQuery query("select last_insert_rowid()"); \
                if (query.next()) return query.value(0).toInt(); \
            } else { qDebug() << QString("update " #name " set %1;").arg(obj.toSqlUpdate()); \
                   throw PSqlCommandError("data cannot be updated in the table " #name " due to an error! " \
                                          "Is it locked?\n" + \
                                          QString("update " #name " set %1;").arg(obj.toSqlUpdate()).toStdString(), \
                                          PCZ_ADD_DINFO); } \
        } else throw PTableDoesntExists("table " #name " does not exists!", PCZ_ADD_DINFO); \
    else throw PDBIsNotOpenException("database is not open or not valid!", PCZ_ADD_DINFO); return -1; } \
QSqlQuery PDataStore::listTable##name (PCZ_DINFO_ARG) const { \
    if (db.isOpen()) \
        if (db.tables().contains(#name)) { \
            return QSqlQuery("SELECT * FROM " #name ";"); \
        } else throw PTableDoesntExists("table " #name " does not exists!", PCZ_ADD_DINFO); \
    else throw PDBIsNotOpenException("database is not open!", PCZ_ADD_DINFO); return QSqlQuery(); } \

#endif	/* PDATASTORE_PREPROCESSOR_H */

