/* 
 * File:   MultiEulerMethod.h
 * Author: polpe
 *
 * Created on February 1, 2014, 3:17 PM
 */

#ifndef MULTIEULERMETHOD_H
#define	MULTIEULERMETHOD_H

#include "MultiVariateMethod.h"

class MultiEulerMethod : public MultiVariateMethod {
public:
    MultiEulerMethod(int dimension = 3);
    MultiEulerMethod(const MultiEulerMethod& orig);
    virtual ~MultiEulerMethod();
    
    virtual void calculateNextValue();
    
private:

};

#endif	/* MULTIEULERMETHOD_H */

