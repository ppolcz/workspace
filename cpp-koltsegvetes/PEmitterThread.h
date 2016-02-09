/* 
 * File:   IConnectableThread.h
 * Author: polpe
 *
 * Created on February 25, 2014, 3:41 PM
 */

#ifndef PROGRESSED_THREAD_H
#define	PROGRESSED_THREAD_H

#include <QThread>

#include "PGlobalEmitter.h"
#include "polpedelop.h"

class PEmitterThread : public QThread
{
    Q_OBJECT
public:
    POLPE_DELETE_OP
    
    PEmitterThread();
    virtual ~PEmitterThread();

    void deleteAfterFinished();

    PGlobalEmitter * getEmitter() const;

protected:
    PGlobalEmitter * emitter = PGlobalEmitter::getEmitterSingleton();

public slots:
    void sl_runCurrentThread();

};

#endif	/* PROGRESSED_THREAD_H */

