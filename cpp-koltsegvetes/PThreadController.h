/* 
 * File:   ThreadController.h
 * Author: polpe
 *
 * Created on February 28, 2014, 11:40 PM
 */

#ifndef THREADCONTROLLER_H
#define	THREADCONTROLLER_H

#include <QObject>
#include "polpedelop.h"
#include "PEmitterThread.h"

class PThreadController : QObject
{
    Q_OBJECT
    PThreadController();
public:
    POLPE_DELETE_OP
    
    virtual ~PThreadController();

    void processAlgorithm(PEmitterThread * algorithm);

    static PThreadController & getSingleton();

signals:
    void sig_startAlgorithm();

private:
    QThread wrk;
    QThread bgr;
};

#endif	/* THREADCONTROLLER_H */

