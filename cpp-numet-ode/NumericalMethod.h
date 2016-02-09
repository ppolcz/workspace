/* 
 * File:   NumericalMethod.h
 * Author: polpe
 *
 * Created on February 1, 2014, 2:01 PM
 */

#ifndef NUMERICALMETHOD_H
#define	NUMERICALMETHOD_H

#include <functional>
#include <memory>

class NumericalMethod {
public:
    typedef std::function<double(double, double) > Function;
    typedef std::function<double(double) > FunctionExpected;
    typedef std::shared_ptr<NumericalMethod> Ptr;

    NumericalMethod();
    NumericalMethod(const NumericalMethod& orig);
    virtual ~NumericalMethod();

    virtual void setFunction(const Function & function);
    virtual void setInitialValue(double t, double x);
    virtual void setUnitStep(double h);

    virtual double getTime();
    virtual double getValue();

    virtual void calculateNextValue() = 0;

protected:
    Function f;
    double h;

    double t, x;

public:
    static void test(NumericalMethod::Ptr method,
            const Function & f,
            double t0,
            double t1,
            double x0,
            double h,
            const FunctionExpected & x_expected);
    
    static void test1(NumericalMethod::Ptr method);
    static void test2(NumericalMethod::Ptr method);
};

#endif	/* NUMERICALMETHOD_H */

