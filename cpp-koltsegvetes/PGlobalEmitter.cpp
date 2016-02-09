/* 
 * File:   progessed_object.cpp
 * Author: polpe
 * 
 * Created on March 5, 2014, 1:38 PM
 */

#include "PGlobalEmitter.h"
#include <polcz/pczdebug.h>

PGlobalEmitter::PGlobalEmitter ()
{
    PCZ_SEG(this);
}

PGlobalEmitter::~PGlobalEmitter ()
{
    PCZ_SEG(this);
}

PGlobalEmitter* PGlobalEmitter::getEmitterSingleton ()
{
    static PGlobalEmitter EMITTER;
    return &EMITTER;
}
