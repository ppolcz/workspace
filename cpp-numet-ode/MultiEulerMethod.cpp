/* 
 * File:   MultiEulerMethod.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 3:17 PM
 */

#include "MultiEulerMethod.h"

MultiEulerMethod::MultiEulerMethod(int dimension) : MultiVariateMethod(dimension) { }

MultiEulerMethod::MultiEulerMethod(const MultiEulerMethod& orig) { }

MultiEulerMethod::~MultiEulerMethod() { }

void MultiEulerMethod::calculateNextValue() {
    x = x + h * f(t, x);
    t = t + h;
}
