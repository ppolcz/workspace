/* 
 * File:   IConnectableThread.cpp
 * Author: polpe
 * 
 * Created on February 25, 2014, 3:41 PM
 */

#include "PEmitterThread.h"

#include <polcz/pczdebug.h>

PEmitterThread::PEmitterThread ()
{
    PCZ_SEG(this);
    connect(this, SIGNAL(started()), emitter, SIGNAL(sig_started()));
    connect(this, SIGNAL(finished()), emitter, SIGNAL(sig_finished()));
}

PEmitterThread::~PEmitterThread ()
{
    PCZ_SEG(this);
}

void PEmitterThread::sl_runCurrentThread ()
{
    emit emitter->sig_started();
    run();
    emit emitter->sig_finished();
}

PGlobalEmitter* PEmitterThread::getEmitter () const
{
    return emitter;
}

void PEmitterThread::deleteAfterFinished ()
{
    connect(emitter, SIGNAL(sig_finished()), this, SLOT(deleteLater()));
}
