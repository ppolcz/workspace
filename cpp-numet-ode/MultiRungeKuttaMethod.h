/* 
 * File:   MultiRungeKutta.h
 * Author: polpe
 *
 * Created on February 1, 2014, 6:09 PM
 */

#ifndef MULTIRUNGEKUTTA_H
#define	MULTIRUNGEKUTTA_H

#include "MultiVariateMethod.h"

class MultiRungeKuttaMethod : public MultiVariateMethod {
public:
    MultiRungeKuttaMethod(int dimension = 3);
    MultiRungeKuttaMethod(const MultiRungeKuttaMethod& orig);
    virtual ~MultiRungeKuttaMethod();

    virtual void calculateNextValue();

private:
    double lambda = 2.0;
    double ilambda = 1.0 / lambda;
};

#endif	/* MULTIRUNGEKUTTA_H */

