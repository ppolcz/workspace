/* 
 * File:   droppablelineedit.h
 * Author: polpe
 *
 * Created on February 16, 2014, 5:06 PM
 */

#ifndef DROPPABLE_LINEEDIT_H
#define	DROPPABLE_LINEEDIT_H

#include "util/util.h"

class DroppableLineEdit : public QLineEdit {
public:
    explicit DroppableLineEdit (QWidget * parent = 0);
    virtual ~DroppableLineEdit ();

protected:
    virtual void dragEnterEvent (QDragEnterEvent*);
    virtual void dragMoveEvent (QDragMoveEvent*);
    virtual void dropEvent (QDropEvent*);
    virtual void handleUrls (const QList<QUrl> & urls);

private:
    bool p_checkDragEvent (QDropEvent*);

};

#endif	/* DROPPABLE_LINEEDIT_H */

