/* 
 * File:   filebrowserview.cpp
 * Author: polpe
 * 
 * Created on February 16, 2014, 2:48 PM
 */

#include "filebrowserview.h"
#include "widgets/treeview.h"
#include "widgets/droppablelineedit.h"

FileBrowserView::FileBrowserView (QAbstractItemView * view, QWidget* parent) : QWidget (parent) {
    m_view = view;
    m_model = new QFileSystemModel();
    m_model->setRootPath(QDir::rootPath());
    p_setupUi();
}

FileBrowserView::~FileBrowserView () { }

void FileBrowserView::p_setupUi () {
    m_layout = new QGridLayout(this);

    m_path = new DroppableLineEdit(parentWidget());
    p_setupLineEdit();
    m_layout->addWidget(m_path, 0, 0);

    m_view = new NavigableTreeView(parentWidget());
    p_setupTree();
    m_layout->addWidget(m_view, 1, 0);
}

void FileBrowserView::p_setupLineEdit () {
    m_path->setAcceptDrops(true);
}

void FileBrowserView::p_setupTree () {
    m_view->setModel(m_model);
    m_view->setRootIndex(m_model->index(QDir::homePath()));
}

QAbstractItemView* FileBrowserView::getView () {
    return m_view;
}
