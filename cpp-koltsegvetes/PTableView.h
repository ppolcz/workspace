/* 
 * File:   PTableView.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 2, 2014, 2:49 PM
 */

// MODEL-VIEW 2

#ifndef PTABLEVIEW_H
#define	PTABLEVIEW_H

#include <QTableView>

#include "PTableModel.h"

class PTableView : public QTableView
{
public:
    explicit PTableView (QWidget * parent = 0);
    virtual ~PTableView ();

    virtual void setModel (QAbstractItemModel* model);
    
private:
    void setupUi ();
    void customize (PTableModel * model);
};

#endif	/* PTABLEVIEW_H */

