/* 
 * File:   MultiVariateMethod.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 2:59 PM
 */

#include "MultiVariateMethod.h"

MultiVariateMethod::MultiVariateMethod(int dimension) {
    dim = dimension;
}

MultiVariateMethod::MultiVariateMethod(const MultiVariateMethod& orig) {
    std::cerr << "Not implemented yet!";
}

MultiVariateMethod::~MultiVariateMethod() { }

void MultiVariateMethod::setFunction(const Function & function) {
    f = function;
}

void MultiVariateMethod::setDimension(int dimension) {
    dim = dimension;
}

void MultiVariateMethod::setInitialValue(double t0, const Vector & x0) {
    t = t0;
    x = x0;
}

void MultiVariateMethod::setUnitStep(double unitstep) {
    h = unitstep;
}

double MultiVariateMethod::getTime() {
    return t;
}

MultiVariateMethod::Vector MultiVariateMethod::getValue() {
    return x;
}

std::ostream & MultiVariateMethod::printVector (MultiVariateMethod::Ptr ptr, const Vector & v, std::ostream & f) {
    printVector(ptr.get(), v, f);
}
std::ostream & MultiVariateMethod::printVector (MultiVariateMethod * ptr, const Vector & v, std::ostream & f) {
    f << "[ ";
    for (int i = 0; i < ptr->dim; ++i) {
        f << v[i] << " ";
    }
    f << "]";
    return f;
}

void MultiVariateMethod::test(Ptr method, int dim, const Function & f, double t0, double t1, const Vector & x0, double h, const FunctionExpected & x_expected) {
    method->setDimension(dim);
    method->setFunction(f);
    method->setInitialValue(t0, x0);
    method->setUnitStep(h);

    int n = int ((t1 - t0) / h) + 1;

    // iteration
    for (unsigned int i = 0; i < n; ++i) {
        method->calculateNextValue();
        std::cout << method->getTime() << " - ";
        printVector(method, method->getValue());
        std::cout << " - expected: ";
        printVector(method, x_expected(method->getTime()));
        std::cout << std::endl;
    }
}

void MultiVariateMethod::test1_dim1(MultiVariateMethod::Ptr method) {
    int dim = 1;
    
    auto f = [](double t, Vector x) {
        return x;
    };
    auto x_expected = [dim](double t) {
        Vector x(dim);
        x << std::exp(t);
        return x;
    };
    double t0 = 0;
    double t1 = 1;
    Vector x0(dim);
    x0 << 1;
    double h = 0.01;
    
    test(method, dim, f, t0, t1, x0, h, x_expected);
}

void MultiVariateMethod::test2_dim2(MultiVariateMethod::Ptr method) {
    auto f = [](double t, Vector x) {
        return x;
    };
    auto x_expected = [](double t) {
        Vector x(2);
        x << std::exp(t), std::exp(t);
        return x;
    };
    double t0 = 0;
    double t1 = 1;
    Vector x0(2);
    x0 << 1, 1;
    double h = 0.01;
    
    test(method, 2, f, t0, t1, x0, h, x_expected);
}
