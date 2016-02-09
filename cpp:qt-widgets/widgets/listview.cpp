/* 
 * File:   listview.cpp
 * Author: polpe
 * 
 * Created on February 16, 2014, 4:08 PM
 */

#include "listview.h"

NavigableListView::NavigableListView (QWidget* parent) : QListView (parent) {
    setUniformItemSizes(true);
    setDragEnabled(true);
    setMinimumWidth(200);
    setMaximumWidth(400);
}

NavigableListView::~NavigableListView () { }

void NavigableListView::setModel (QAbstractItemModel* model) throw (std::invalid_argument) {
    try {
        QListView::setModel(dynamic_cast<QFileSystemModel*>(model));
    } catch (const std::bad_cast& e) {
        throw std::invalid_argument("Only QFileSystemModel accepted" + DEBUG_INFO);;
    }
}

void NavigableListView::mouseDoubleClickEvent (QMouseEvent* event) {
    setRootIndex(indexAt(event->pos()));
    QListView::mouseDoubleClickEvent(event);
}

void NavigableListView::keyPressEvent (QKeyEvent* event) {
    if (event->key() == Qt::Key_Left) {
        setRootIndex(rootIndex().parent());
    } else if (event->key() == Qt::Key_Right && ((QFileSystemModel*) model())->isDir(currentIndex())) {
        setRootIndex(currentIndex());
    }
    QListView::keyPressEvent(event);
}
