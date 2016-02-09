/* 
 * File:   ThreadController.cpp
 * Author: polpe
 * 
 * Created on February 28, 2014, 11:40 PM
 */

#include "PThreadController.h"

#include <iostream>
#include <polcz/pczdebug.h>

PThreadController::PThreadController ()
{
    PCZ_SEG(this);
    wrk.start();
}

PThreadController::~PThreadController ()
{
    wrk.quit();
    wrk.wait();
    PCZ_SEG(this);
}

void PThreadController::processAlgorithm (PEmitterThread* algorithm)
{
    algorithm->moveToThread(&wrk);
    QMetaObject::invokeMethod(algorithm, "run", Qt::QueuedConnection);
//    #warning ezt mar ird at QMetaObject-re
//    connect(&wrk, SIGNAL(finished()), algorithm, SLOT(deleteLater()));
//
//    connect(this, SIGNAL(sig_startAlgorithm()), algorithm, SLOT(sl_runCurrentThread()));
//    emit sig_startAlgorithm();
//    disconnect(this, SIGNAL(sig_startAlgorithm()), algorithm, SLOT(sl_runCurrentThread()));
}

PThreadController& PThreadController::getSingleton ()
{
    static PThreadController THREAD_CONTROLLER;
    return THREAD_CONTROLLER;
}
