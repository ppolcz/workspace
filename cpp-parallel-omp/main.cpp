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
#include <chrono>

using namespace std;

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

    auto start = std::chrono::high_resolution_clock::now();

    std::cout << chrono::high_resolution_clock::period::den << std::endl;
    auto start_time = chrono::high_resolution_clock::now();
    double temp;
    for (double i = 0; i < 1000; i++)
        temp += temp + i * temp/10000;
    std::cout << temp << std::endl;
    auto end_time = chrono::high_resolution_clock::now();
    std::cout << chrono::duration_cast<chrono::seconds>(end_time - start_time).count() << ":";
    std::cout << chrono::duration_cast<chrono::microseconds>(end_time - start_time).count() << ":";
    return 0;
}

