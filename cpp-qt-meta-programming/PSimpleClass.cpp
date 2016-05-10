/* 
 * File:   PSimpleClass.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on October 22, 2014, 10:27 AM
 */

#include <polcz/pczdebug.h>

#include "PSimpleClass.h"

PSimpleClass::PSimpleClass () { }

PSimpleClass::~PSimpleClass () { }

void PSimpleClass::private_slot (double) { }

void PSimpleClass::public_slot (double)
{
    PCZ_REGISTER_METHOD
}

float PSimpleClass::from_preprocessor (int arg0, float arg1, bool arg2)
{
    PCZ_REGISTER_METHOD
    PCZ_UNUSED((arg1)(arg1)(arg2));
    return 0;
}

int PSimpleClass::registered_method (float)
{
    PCZ_REGISTER_METHOD

    return 0;
}

void PSimpleClass::private_slot (double, float, int, QString name) 
{ 
    PCZ_UNUSED((name));
}

PMetaClass PSimpleClass::meta_object = PMetaClass ();

const string& PSimpleClass::__THIS_IS_SURELY_NEVER_USED (int, double name) 
{
    PCZ_REGISTER_METHOD
}
