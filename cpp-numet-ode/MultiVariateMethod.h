/* 
 * File:   MultiVariateMethod.h
 * Author: polpe
 *
 * Created on February 1, 2014, 2:59 PM
 */

#ifndef MULTIVARIATEMETHOD_H
#define	MULTIVARIATEMETHOD_H

#include <functional>
#include <memory>
#include <iostream>

#include <Eigen/Dense>

class MultiVariateMethod {
public:
    typedef Eigen::VectorXd Vector;
    typedef std::function<Vector(double, Vector) > Function;
    typedef std::function<Vector(double) > FunctionExpected;
    typedef std::shared_ptr<MultiVariateMethod> Ptr;

    MultiVariateMethod(int dimension = 3);
    MultiVariateMethod(const MultiVariateMethod& orig);
    virtual ~MultiVariateMethod();

    virtual void setDimension(int dimension);
    virtual void setFunction(const Function & function);
    virtual void setInitialValue(double t0, const Vector & x0);
    virtual void setUnitStep(double h);

    virtual double getTime();
    virtual Vector getValue();
    
    virtual void calculateNextValue() = 0;

protected:
    int dim;

    Function f;
    Vector x;
    double t, h;
    
protected:
    static void test (MultiVariateMethod::Ptr method, 
            int dim, 
            const Function & f, 
            double t0, 
            double t1, 
            const Vector & x0, 
            double h, 
            const FunctionExpected & x_expected);
    
    static std::ostream & printVector (MultiVariateMethod * ptr, const Vector & v, std::ostream & f = std::cout);
    static std::ostream & printVector (MultiVariateMethod::Ptr ptr, const Vector & v, std::ostream & f = std::cout);
    
public:
    static void test2_dim2 (MultiVariateMethod::Ptr method);
    static void test1_dim1 (MultiVariateMethod::Ptr method);
};

#endif	/* MULTIVARIATEMETHOD_H */

