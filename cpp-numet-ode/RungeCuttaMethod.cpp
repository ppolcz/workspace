/* 
 * File:   RungeCuttaMethod.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 12:18 PM
 */

#include "RungeCuttaMethod.h"

RungeCuttaMethod::RungeCuttaMethod() { }

RungeCuttaMethod::RungeCuttaMethod(const RungeCuttaMethod& orig) { }

RungeCuttaMethod::~RungeCuttaMethod() { }

void RungeCuttaMethod::calculateNextValue() {
    double k1 = f(t, x);
    double k2 = f(t + 0.5 * h, x + 0.5 * k1 * h);
    double k3 = f(t + 0.5 * h, x + (0.5 - 1 * ilambda) * k1 * h + 1 * ilambda * k2 * h);
    double k4 = f(t + h, x + (1.0 - lambda * 0.5) * k2 * h + lambda * 0.5 * k3 * h);
    x = x + 1 / 6.0 * h * (k1  + (4.0 - lambda) * k2 + lambda * k3 + k4);
    t = t + h;
}
