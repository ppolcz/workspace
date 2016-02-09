/* 
 * File:   main_template_specialization.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 15, 2015, 11:05 PM
 */

#include <iostream>

template <int N, int M>
struct A
{
    enum { value = N };
};

template <int M>
struct A<1, M> {};

void main_template_specialization()
{
    // std::cout << A<1,2>::value << std::endl; // error
    std::cout << A<2,2>::value << std::endl;
}

