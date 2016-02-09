/* 
 * File:   PInputTranzakcio.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on August 31, 2014, 9:13 PM
 */

#ifndef PINPUTTRANZAKCIO_H
#define	PINPUTTRANZAKCIO_H

#include <QCalendarWidget>
#include <QDate>

#include "PInputBox.h"
#include "PDataStore_Types.h"

class PInputTranzakcio : public PInputBox
{
    Q_OBJECT
public:
    explicit PInputTranzakcio (QWidget * parent = 0);
    virtual ~PInputTranzakcio ();

    void setTipusModel (QAbstractItemModel* model, const QModelIndex& index);
    void setSzamlaModel (QAbstractItemModel* model, const QModelIndex& index);
    void setTranzakcio (PTranzakcio const& tr);
    
signals:
//    void sig_newTranzakcio(int osszeg, QString megj, QModelIndex tipus, QModelIndex szamla, QDate date);
    void sig_newTranzakcio(PTranzakcio const& tr);
    void sig_updateTranzakcio(PTranzakcio const& tr);
    
private:
    void collectValues();
    void setupUi();
//    void setupLinks();
    void setupUi_Fill();
    
    QLineEdit * osszeg;
    QCalendarWidget * calendar;
    QTextEdit * megj;
    QListView * tipus;
    QTableView * szamla;
    QPushButton * commit;

    PTranzakcio tranzakcio;
};

#endif	/* PINPUTTRANZAKCIO_H */

