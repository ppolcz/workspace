/* 
 * File:   EulerMethod.h
 * Author: polpe
 *
 * Created on January 31, 2014, 10:29 PM
 */

#ifndef EULERMETHOD_H
#define	EULERMETHOD_H

#include <functional>

#include "NumericalMethod.h"

class EulerMethod : public NumericalMethod {
public:
    typedef std::function<double(double, double)> Function;

    EulerMethod();
    EulerMethod(const EulerMethod& orig);
    virtual ~EulerMethod();
    
    void calculateNextValue();
};

#endif	/* EULERMETHOD_H */

