/* 
 * File:   progessed_object.h
 * Author: polpe
 *
 * Created on March 5, 2014, 1:38 PM
 */

#ifndef PROGESSED_OBJECT_H
#define	PROGESSED_OBJECT_H

#include <QObject>

#include "polpedelop.h"

class PGlobalEmitter : public QObject
{
    Q_OBJECT

    explicit PGlobalEmitter();

public:
    POLPE_DELETE_OP
    
    virtual ~PGlobalEmitter();
    static PGlobalEmitter * getEmitterSingleton();

signals:
    void sig_started();
    void sig_finished();
    void sig_pbsetMaximum(int);
    void sig_pbsetProgress(int);
    void sig_sbshowMessage(const QString &, int);
    void sig_showProgressBar();
    void sig_hideProgressBar();
    void sig_messageBoxCritical(const QString &, const QString &);
    void sig_emitNext(int);
};

#endif	/* PROGESSED_OBJECT_H */

