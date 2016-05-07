/* 
 * File:   PController.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on August 31, 2014, 11:42 PM
 */

#include "PController.h"
#include "PUpdater.h"

PController::PController () : db(PDataStore::singleton()) 
{ 
//    db->reset();
//    db->readOldDatabase("/home/polpe/Dropbox/koltsegvetes.ascii");
    db->open();
    
    t_folyoszamlak.setTable(PDataStore::Table_Folyoszamlak());
    t_folyoszamlak.removeColumn(2);
    t_folyoszamlak.select();
    
    t_tranTipusok.setQuery("select * from TranTipusok");
//    t_tranTipusok.setQuery("select * from TranTipusok where tt like 'KI_%' OR tt like 'BE_%'");
}

PController::~PController () 
{ 
    db->close();
}

void PController::setupInputTranzakcio(PInputTranzakcio* i, bool updater) 
{
    QModelIndex index_szamlak;
    for (int i = 0; i < t_folyoszamlak.rowCount(); ++i)
    {
        if (t_folyoszamlak.record(i).value("fs_Id").toString().indexOf(PDataStore::DEFAULT_FOLYOSZAMLA, 0, Qt::CaseSensitive) != -1) 
        {
            index_szamlak = t_folyoszamlak.index(i, 0);
            break;
        }
    }
    
    QModelIndex index_trantipusok;
    for (int i = 0; i < t_tranTipusok.rowCount(); ++i)
    {
        if (t_tranTipusok.record(i).value("tt").toString().indexOf(PDataStore::DEFAULT_TRAN_TIPUS, 0, Qt::CaseSensitive) != -1) 
        {
            index_trantipusok = t_tranTipusok.index(i, 0);
            break;
        }
    }
    
    i->setSzamlaModel(&t_folyoszamlak, index_szamlak);
    i->setTipusModel(&t_tranTipusok, index_trantipusok);
    
    if (!updater) connect(i, &PInputTranzakcio::sig_newTranzakcio, [this] (PTranzakcio const& tr) {
        db->insertTableTranzakciok(tr, PCZ_DEBUG_INFO);
        db->logDatabase(PDataStore::LOG_DIR);
    });
}

void PController::setupTableView (PTableView* tw) 
{
    connect(tw, &PTableView::doubleClicked, [tw, this] (const QModelIndex& i)
    {
        PTableModel* model = (PTableModel*) (tw->model());
        int id = model->index(i.row(), model->col_trId).data().toInt();
        QSqlQuery query = db->findRecord(PDataStore::Table_Tranzakciok(), QString("tr_Id == %1").arg(id));
        query.next();
        PTranzakcio tranzakcio = PTranzakcio::fromSqlQuery(query);
        
        PInputTranzakcio* itran = new PInputTranzakcio(tw);
        
        connect(itran, &PInputTranzakcio::sig_updateTranzakcio, [tw, model, itran](PTranzakcio const& tr)
        {
            PDataStore::singleton()->updateTableTranzakciok(tr, PCZ_DEBUG_INFO);
            PDataStore::singleton()->logDatabase(PDataStore::LOG_DIR);
            PUpdater(PUpdater::Update).sl_runCurrentThread();
            model->refresh();
            itran->hide();
            itran->deleteLater();
        });
        
        setupInputTranzakcio(itran, true);
        itran->setTranzakcio(tranzakcio);
        itran->setWindowFlags(Qt::Window);
        itran->show();
    });
}
