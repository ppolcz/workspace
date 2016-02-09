/* 
 * File:   NumericalMethod.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 2:01 PM
 */

#include "NumericalMethod.h"

#include <iostream>
#include <cmath>

NumericalMethod::NumericalMethod() { }

NumericalMethod::NumericalMethod(const NumericalMethod& orig) { }

NumericalMethod::~NumericalMethod() { }

void NumericalMethod::setFunction(const Function& function) {
    this->f = function;
}

void NumericalMethod::setInitialValue(double t, double x) {
    this->t = t;
    this->x = x;
}

void NumericalMethod::setUnitStep(double h) {
    this->h = h;
}

double NumericalMethod::getTime() {
    return t;
}

double NumericalMethod::getValue() {
    return x;
}

/**
 * Testing framework
 * @param method
 * @param f
 * @param t0
 * @param t1
 * @param x0
 * @param h
 * @param x_expected
 */
void NumericalMethod::test(NumericalMethod::Ptr method, 
        const Function & f, 
        double t0, double t1, double x0, double h, 
        const FunctionExpected & x_expected) {
    method->setFunction(f);
    method->setInitialValue(t0, x0);
    method->setUnitStep(h);

    int n = int ((t1 - t0) / h) + 1;

    // iteration
    for (unsigned int i = 0; i < n; ++i) {
        method->calculateNextValue();
        std::cout << method->getTime() << " - " << method->getValue() << " - expected: " << x_expected(method->getTime()) << std::endl;
    }
}

/**
 * equation: x' = x, x(0) = 1;
 * solution: x(t) = exp(t)
 * on the interval: 0:0.001:1
 * @param method
 */
void NumericalMethod::test1(NumericalMethod::Ptr method) {
    NumericalMethod::test(method, [](double t, double x) {
        return x;
    }, 0, 1, 1, 0.001, [](double t) {
        return std::exp(t);
    });
}

/**
 * equation: x' = x, x(0) = 1;
 * solution: x(t) = exp(t)
 * on the interval: 0:0.1:10
 * @param method
 */
void NumericalMethod::test2(NumericalMethod::Ptr method) {
    NumericalMethod::test(method, [](double t, double x) {
        return x;
    }, 0, 10, 1, 0.1, [](double t) {
        return std::exp(t);
    });
}