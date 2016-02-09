/* 
 * File:   EulerMethod.cpp
 * Author: polpe
 * 
 * Created on January 31, 2014, 10:29 PM
 */

#include "EulerMethod.h"

#include <iostream>
#include <cmath>

EulerMethod::EulerMethod() { }

EulerMethod::EulerMethod(const EulerMethod& orig) { }

EulerMethod::~EulerMethod() { }

void EulerMethod::calculateNextValue() {
    x = x + h * f(t, x);
    t = t + h;
}
