/* 
 * File:   MultiRungeKutta.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 6:09 PM
 */

#include "MultiRungeKuttaMethod.h"

MultiRungeKuttaMethod::MultiRungeKuttaMethod(int dimension) : MultiVariateMethod(dimension) { }

MultiRungeKuttaMethod::MultiRungeKuttaMethod(const MultiRungeKuttaMethod& orig) { }

MultiRungeKuttaMethod::~MultiRungeKuttaMethod() { }

void MultiRungeKuttaMethod::calculateNextValue() {
    Vector k1 = f(t, x);
    Vector k2 = f(t + 0.5 * h, x + 0.5 * k1 * h);
    Vector k3 = f(t + 0.5 * h, x + (0.5 - 1 * ilambda) * k1 * h + 1 * ilambda * k2 * h);
    Vector k4 = f(t + h, x + (1.0 - lambda * 0.5) * k2 * h + lambda * 0.5 * k3 * h);
    x = x + 1 / 6.0 * h * (k1  + (4.0 - lambda) * k2 + lambda * k3 + k4);
    t = t + h;
}
