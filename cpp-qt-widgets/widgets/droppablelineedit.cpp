/* 
 * File:   droppablelineedit.cpp
 * Author: polpe
 * 
 * Created on February 16, 2014, 5:06 PM
 */

#include "droppablelineedit.h"

DroppableLineEdit::DroppableLineEdit (QWidget* parent) : QLineEdit (parent) { }

DroppableLineEdit::~DroppableLineEdit () { }

void DroppableLineEdit::dragEnterEvent (QDragEnterEvent*event) {
    if (p_checkDragEvent(event)) event->acceptProposedAction();
}

void DroppableLineEdit::dragMoveEvent (QDragMoveEvent* event) {
    if (p_checkDragEvent(event)) event->acceptProposedAction();
}

void DroppableLineEdit::dropEvent (QDropEvent* event) {
    if (p_checkDragEvent(event)) {
        handleUrls(event->mimeData()->urls());
    }
}

bool DroppableLineEdit::p_checkDragEvent (QDropEvent* event) {
    auto urls = event->mimeData()->urls();
    return !urls.isEmpty() && urls[0].scheme() == "file";
}

void DroppableLineEdit::handleUrls (const QList<QUrl>& urls) {
    setText(urls[0].path());
}
