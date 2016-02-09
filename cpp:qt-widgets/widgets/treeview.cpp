/* 
 * File:   treeview.cpp
 * Author: polpe
 * 
 * Created on February 16, 2014, 3:42 PM
 */

#include "treeview.h"
#include <typeinfo>

NavigableTreeView::NavigableTreeView (QWidget* parent) : QTreeView (parent) {
    setDragEnabled(true);
    setUniformRowHeights(true);
}

NavigableTreeView::~NavigableTreeView () { }

void NavigableTreeView::keyPressEvent (QKeyEvent* event) { 
    if (event->key() == Qt::Key_Left) {
        setRootIndex(rootIndex().parent());
    } else if (event->key() == Qt::Key_Right && ((QFileSystemModel*) model())->isDir(currentIndex()) ) {
        setRootIndex(currentIndex());
    }
    QTreeView::keyPressEvent(event);
}

void NavigableTreeView::mouseDoubleClickEvent (QMouseEvent* event) {
    setRootIndex(indexAt(event->pos()));
    QTreeView::mouseDoubleClickEvent(event);
}

void NavigableTreeView::setModel (QAbstractItemModel* model) throw (std::invalid_argument) {
    try {
        QTreeView::setModel(dynamic_cast<QFileSystemModel*>(model));
    } catch (const std::bad_cast& e) {
        throw std::invalid_argument("Only QFileSystemModel accepted" + DEBUG_INFO);
    }
}

//void TreeView::mousePressEvent (QMouseEvent * event) {
//    if (event->button() == Qt::LeftButton) {
//
//        QDrag *drag = new QDrag(this);
//        QMimeData *mimeData = new QMimeData;
//
//        // mimeData->setText(p_absolutePath(indexAt(event->pos())));
//        mimeData->setText(((QFileSystemModel*) model())->filePath((indexAt(event->pos()))));
//        drag->setMimeData(mimeData);
//        drag->exec();
//    }
//    QTreeView::mousePressEvent(event);
//}

/**
 * Here I present a way, which should NOT be your first approach.
 */
//QString TreeView::p_absolutePath (const QModelIndex& index) {
//    if (model()->itemData(index)[0].toString() == QDir::rootPath()) return "";
//    return p_absolutePath(index.parent()) + "/" + model()->itemData(index)[0].toString();
//}
