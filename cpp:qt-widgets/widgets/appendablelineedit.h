/* 
 * File:   appendablelineedit.h
 * Author: polpe
 *
 * Created on February 25, 2014, 1:27 PM
 */

#ifndef APPENDABLELINEEDIT_H
#define	APPENDABLELINEEDIT_H

#include "droppablelineedit.h"

class AppendableLineEdit : public DroppableLineEdit {
public:
    explicit AppendableLineEdit (QWidget * parent = 0);
    virtual ~AppendableLineEdit ();

    QStringList getPaths () const;
    
protected:
    virtual void handleUrls (const QList<QUrl> & urls);

private:
    QStringList m_paths;
};

#endif	/* APPENDABLELINEEDIT_H */

