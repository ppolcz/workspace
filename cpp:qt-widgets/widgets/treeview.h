/* 
 * File:   treeview.h
 * Author: polpe
 *
 * Created on February 16, 2014, 3:42 PM
 */

#ifndef TREEVIEW_H
#define	TREEVIEW_H

#include "util/util.h"
#include <stdexcept>

class NavigableTreeView : public QTreeView {
public:
    explicit NavigableTreeView (QWidget * parent = 0);
    virtual ~NavigableTreeView ();

    virtual void setModel (QAbstractItemModel* model) throw(std::invalid_argument);
    
protected:
    // virtual void mousePressEvent (QMouseEvent*);
    virtual void mouseDoubleClickEvent (QMouseEvent* event);
    virtual void keyPressEvent (QKeyEvent* event);

private:
    // QString p_absolutePath (const QModelIndex & index);

};

#endif	/* TREEVIEW_H */

