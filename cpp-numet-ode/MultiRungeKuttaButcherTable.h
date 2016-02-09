/* 
 * File:   MultiRungeKuttaButcherTable.h
 * Author: polpe
 *
 * Created on February 1, 2014, 6:57 PM
 */

#ifndef MULTIRUNGEKUTTABUTCHERTABLE_H
#define	MULTIRUNGEKUTTABUTCHERTABLE_H

#include "MultiVariateMethod.h"

class MultiRungeKuttaButcherTable : public MultiVariateMethod {
public:
    MultiRungeKuttaButcherTable(int dimension = 3);
    MultiRungeKuttaButcherTable(const MultiRungeKuttaButcherTable& orig);
    virtual ~MultiRungeKuttaButcherTable();

    virtual void selectFehlbergMethod();

    virtual void calculateNextValue();

private:

    //    std::vector<std::vector<double>> a;
    //    std::vector<double> c;
    //    std::vector<double> 

    double * a, * b, * c, * d;
    double * k = 0;
    int order = 0;

    static double fehlberg_a[5][5];
    static double fehlberg_b[6];
    static double fehlberg_c[6];
    static double fehlberg_d[6];
};

#endif	/* MULTIRUNGEKUTTABUTCHERTABLE_H */

