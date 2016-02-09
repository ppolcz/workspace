/* 
 * File:   openwidget.h
 * Author: deva
 *
 * Created on February 12, 2014, 3:07 PM
 */

#ifndef OPENWIDGET_H
#define	OPENWIDGET_H

#include "util/util.h"

class OpenWidget : public QWidget {
    Q_OBJECT
    ADD_CHANGE_STATUS_CALLBACK
public:
    ADD_SHARED_PTR (OpenWidget)

    OpenWidget (QWidget * parent = 0);
    virtual ~OpenWidget ();

    void shareStatusBar (QStatusBar * sb);
    void setDefaultPath (const QString & dpath);
    void setTitle (const QString & title);
    void setFilter (const QString & filter);
    void setDir (const QString & dir);

private:
    void p_makeConnects ();

signals:
    void sig_filenameChanged (const QString &);

private slots:
    void sl_open ();

private:
    QString m_dir;
    QString m_title = "Open a LIDAR file, to proceed our algorithms";
    QString m_filter = "LIDAR files (*.pcd *.las)";
    // --
    QHBoxLayout * m_layout;
    QLineEdit * m_lineEdit;
    QPushButton * m_button;
};

#endif	/* OPENWIDGET_H */

