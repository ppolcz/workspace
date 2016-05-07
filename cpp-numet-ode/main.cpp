/* 
 * File:   main.cpp
 * Author: polpe
 *
 * Created on January 31, 2014, 10:28 PM
 */

#include <cstdlib>
#include <iostream>

#include "EulerMethod.h"
#include "RungeCuttaMethod.h"
#include "MultiVariateMethod.h"
#include "MultiEulerMethod.h"
#include "MultiRungeKuttaMethod.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {

    NumericalMethod::Ptr euler(new EulerMethod());
    NumericalMethod::Ptr runge(new RungeCuttaMethod());
    MultiVariateMethod::Ptr multiEuler(new MultiEulerMethod(2));
    MultiVariateMethod::Ptr multiRunge(new MultiRungeKuttaMethod(2));

    //    NumericalMethod::test1(euler);
    //    NumericalMethod::test1(runge);
    //    NumericalMethod::test2(euler);
    //    NumericalMethod::test2(runge);
    //    MultiVariateMethod::test1(multiEuler);
    //    MultiVariateMethod::test2_dim2(multiRunge);
    MultiVariateMethod::test1_dim1(multiRunge);


    return 0;
}

