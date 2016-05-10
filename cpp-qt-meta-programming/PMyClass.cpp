/* 
 * File:   PMyClass.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on October 24, 2014, 6:26 PM
 */

#include "PMyClass.h"

PMyClass::PMyClass() : int_field(12), string_field("elment anyam vasarolni") { }

PMyClass::~PMyClass() { }

int PMyClass::g(int i, const char* a)
{
    return i;
}

void PMyClass::f() { }

int PMyClass::method(double a, int b)
{
    return b + a;
}

