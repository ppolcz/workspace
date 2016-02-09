/* 
 * File:   pczexcept.cpp
 * Author: Polcz Péter
 * 
 * Created on May 16, 2014, 11:26 AM
 */

#include "pczexcept.h"

PInterruptedException::PInterruptedException (const std::string& _msg) : msg (_msg) { }

const char* PInterruptedException::what ()
{
    return (std::string("Interrupted: ") + msg).c_str();
}
