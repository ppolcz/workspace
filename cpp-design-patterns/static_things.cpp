/* 
 * File:   static_things.cpp
 * Author: polpe
 * 
 * Created on March 31, 2014, 10:29 PM
 */

#include "static_things.h"

StaticThings::StaticThings () { }

StaticThings::StaticThings (const StaticThings& orig) { }

StaticThings::~StaticThings () { }

//constexpr int StaticThings::SP_StaticPrivate[];

const std::string StaticThings::SP_String = "adasd";