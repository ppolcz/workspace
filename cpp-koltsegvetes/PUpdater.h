/* 
 * File:   PUpdater.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 5, 2014, 8:06 AM
 */

#ifndef PUPDATER_H
#define	PUPDATER_H

#include "PEmitterThread.h"

class PUpdater : public PEmitterThread
{
public:
    enum UpdateMode { Test, Update };
    
    PUpdater (UpdateMode mode = Test);
    virtual ~PUpdater ();

protected:    
    virtual void run ();
    
private:
    UpdateMode mode;
};

#endif	/* PUPDATER_H */

