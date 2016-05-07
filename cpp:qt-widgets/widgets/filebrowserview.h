/* 
 * File:   filebrowserview.h
 * Author: polpe
 *
 * Created on February 16, 2014, 2:48 PM
 */

#ifndef FILE_BROWSER_VIEW_H
#define	FILE_BROWSER_VIEW_H

#include "util/util.h"

//template <typename _ViewType = QTreeView>

class FileBrowserView : public QWidget {
public:
    explicit FileBrowserView (QAbstractItemView * view, QWidget * parent = 0);
    virtual ~FileBrowserView ();

    QAbstractItemView * getView ();

protected:

private:
    void p_setupUi ();
    void p_setupTree ();
    void p_setupLineEdit ();

    QGridLayout * m_layout;

    QFileSystemModel * m_model;

    QAbstractItemView * m_view;
    QLineEdit * m_path;
};
#endif	/* FILE_BROWSER_VIEW_H */

