/* 
 * File:   listview.h
 * Author: polpe
 *
 * Created on February 16, 2014, 4:08 PM
 */

#ifndef LISTVIEW_H
#define	LISTVIEW_H

#include "util/util.h"
#include <stdexcept>

class NavigableListView : public QListView {
public:
    explicit NavigableListView (QWidget * parent = 0);
    virtual ~NavigableListView ();

    virtual void setModel (QAbstractItemModel* model) throw(std::invalid_argument);
    
protected:
    virtual void mouseDoubleClickEvent (QMouseEvent* event);
    virtual void keyPressEvent (QKeyEvent* event);

private:

};

#endif	/* LISTVIEW_H */

