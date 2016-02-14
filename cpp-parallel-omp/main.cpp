/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: polpe
 *
 * Created on February 14, 2016, 8:31 PM
 */

#include <iostream>
#include <cstdlib>
#include "chrono_helper.hpp"

extern "C"
{
void main_linked (int argc, char* argv[]);
void main_mandel ();
void main_mdlj (int argc, char* argv[]);
}

/*
 * 
 */
int main (int argc, char** argv)
{

    //    main_linked(argc, argv);
    //    main_mandel();
    //    main_mdlj(argc, argv);

    PP_CHRONO_START(summation);
    double temp;
    for (double i = 0; i < 1000000000; i++)
        temp += temp + i * temp / 10000;
    std::cout << temp << std::endl;
    PP_CHRONO_END(summation);

    return 0;
}

