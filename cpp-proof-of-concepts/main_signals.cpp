/* 
 * File:   main_signals.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 2, 2015, 2:05 PM
 */

#include <iostream>
#include <cstdlib>
#include <csignal>

#define SIG_PCZ 123123

void kutyagumi(int n) { printf("signal kutyagumi: %d\nSIGINT = %d\nSIGILL = %d\n", n, SIGINT, SIGILL); } 

void main_signals()
{
    std::signal(SIGINT, kutyagumi); // working
    std::signal(SIGILL, kutyagumi); // working
    std::signal(SIG_PCZ, kutyagumi); // not working
    
    std::cout << "before\n";
    raise(SIGINT); // working
    raise(SIGILL); // working
    raise(SIG_PCZ); // not working
    std::cout << "after\n";
}

