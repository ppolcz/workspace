/* 
 * File:   openwidget.cpp
 * Author: deva
 * 
 * Created on February 12, 2014, 3:07 PM
 */

#include <stdexcept>

#include "openwidget.h"

OpenWidget::OpenWidget (QWidget * parent) : QWidget (parent) {
    m_lineEdit = new QLineEdit(this);

    m_button = new QPushButton(this);
    m_button->setText("Open");

    m_layout = new QHBoxLayout();
    m_layout->addWidget(m_lineEdit, 1);
    m_layout->addWidget(m_button);
    setLayout(m_layout);

    p_makeConnects();
}

OpenWidget::~OpenWidget () { }

void OpenWidget::p_makeConnects () {
    connect(this, SIGNAL(sig_filenameChanged(QString)), m_lineEdit, SLOT(setText(const QString &)));
    connect(m_button, SIGNAL(pressed()), this, SLOT(sl_open()));
}

void OpenWidget::sl_open () {
    QString filename = QFileDialog::getOpenFileName(this, m_title, m_dir, m_filter);
    if (!filename.isEmpty()) {

        QFile file(filename);
        if (!file.open(QFile::ReadOnly | QFile::Text)) {
            QMessageBox::warning(this, tr("Application"), tr("Cannot read file %1:\n%2.").arg(filename).arg(file.errorString()));
            return;
        }

        emit sig_filenameChanged(filename);
        cb_changeStatus("File loaded", 200);
        return;
    }
    
    cb_changeStatus("No file to open", 200);
}

void OpenWidget::setDefaultPath (const QString& dpath) {
    m_lineEdit->setText(dpath);
}

void OpenWidget::setFilter (const QString& filter) {
    m_filter = filter;
}

void OpenWidget::setTitle (const QString& title) {
    m_title = title;
}

void OpenWidget::setDir (const QString& dir) {
    QDir qdir(dir);
    if (!qdir.exists()) throw std::invalid_argument("Directory " + dir.toStdString() + " not exists" + DEBUG_INFO);
    m_dir = dir;
}
