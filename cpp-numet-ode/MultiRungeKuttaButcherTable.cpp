/* 
 * File:   MultiRungeKuttaButcherTable.cpp
 * Author: polpe
 * 
 * Created on February 1, 2014, 6:57 PM
 */

#include "MultiRungeKuttaButcherTable.h"

MultiRungeKuttaButcherTable::MultiRungeKuttaButcherTable(int dimension) : MultiVariateMethod(dimension)
{
    selectFehlbergMethod();
}

MultiRungeKuttaButcherTable::MultiRungeKuttaButcherTable(const MultiRungeKuttaButcherTable& orig) { }

MultiRungeKuttaButcherTable::~MultiRungeKuttaButcherTable()
{
    if (k != 0) delete[] k;
}

void MultiRungeKuttaButcherTable::selectFehlbergMethod()
{
    order = 6;
    k = new double[order];
    a = &fehlberg_a[0][0];
    b = fehlberg_b;
    c = fehlberg_c;
}

/**
 * This is not finished or not working.
 */
void MultiRungeKuttaButcherTable::calculateNextValue()
{
//    for (int i = 0; i < order; ++i)
//    {
//        double xn = x;
//        for (int j = 0; j < i; ++j)
//        {
//            xn += a[i][j] * k[j];
//        }
//        k[i] = h * f(t + c[i] * h, xn);
//        x += k[i] * b[i];
//    }
}

double MultiRungeKuttaButcherTable::fehlberg_a[5][5] = {
    {0.25, 0, 0, 0},
    {0.09375, 0.28125, 0, 0},
    {0.87938097405553026855, -3.27719617660446062813, 3.3208921256258534365, 0},
    {2.03240740740740740741, -8.0, 7.17348927875243664717, -0.20589668615984405458, 0},
    {-0.2962962962962962963, 2.0, -1.38167641325536062378, 0.45297270955165692008, -0.275}
};

double MultiRungeKuttaButcherTable::fehlberg_b[6] = {
    0.11851851851851851852, 0, 0.51898635477582846004, 0.50613149034201665781, -0.18, 0.03636363636363636364
};

double MultiRungeKuttaButcherTable::fehlberg_c[6] = {
    0, 0.25, 0.375, 0.85714285714285714286, 1, 0.5
};
