/* 
 * File:   appendablelineedit.cpp
 * Author: polpe
 * 
 * Created on February 25, 2014, 1:27 PM
 */

#include "appendablelineedit.h"

#include <iostream>

AppendableLineEdit::AppendableLineEdit (QWidget * parent) : DroppableLineEdit (parent) { }

AppendableLineEdit::~AppendableLineEdit () { }

void AppendableLineEdit::handleUrls (const QList<QUrl>& urls) {
    QString text = "";
    m_paths.clear();
    for (int i = 0; i < urls.size() - 1; ++i) {
        m_paths.append(urls[i].path());
        text.append(urls[i].path()).append(";");
    }
    m_paths.append(urls[urls.size() - 1].path());
    text.append(urls[urls.size() - 1].path());
    setText(text);
}

QStringList AppendableLineEdit::getPaths () const {
    return m_paths;
}
