/* 
 * File:   RungeCuttaMethod.h
 * Author: polpe
 *
 * Created on February 1, 2014, 12:18 PM
 */

#ifndef RUNGECUTTAMETHOD_H
#define	RUNGECUTTAMETHOD_H

#include "NumericalMethod.h"

class RungeCuttaMethod : public NumericalMethod {
public:
    typedef std::function<double(double, double) > Function;

    RungeCuttaMethod();
    RungeCuttaMethod(const RungeCuttaMethod& orig);
    virtual ~RungeCuttaMethod();

    void calculateNextValue();

private:
    double lambda = 2.0;
    double ilambda = 1.0 / lambda;
};

#endif	/* RUNGECUTTAMETHOD_H */

