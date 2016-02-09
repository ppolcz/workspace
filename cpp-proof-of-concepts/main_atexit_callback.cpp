/* 
 * File:   main_thread_1.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on February 2, 2015, 1:51 PM
 */

#include <cstdlib>
#include <iostream>
#include <polcz/pczdebug.h>

struct destructror_class
{
    destructror_class() { std::cout << "constructor\n"; }
    
    void print() { std::cout << "print\n"; }
    
    ~destructror_class() { std::cout << "destructor\n"; }
};

void at_exit_clean()
{
    std::cout << "at_exit_clean\n";
}

void at_exit_clean_2()
{
    std::cout << "at_exit_clean_2\n";
}

void at_exit_success()
{
    std::cout << "\n\nThe program has terminated successfully!\n";
}

void main_atexit_callback()
{
    std::atexit(at_exit_success);
    const int result = std::atexit(at_exit_clean);
    const int result_2 = std::atexit(at_exit_clean_2);
    
    destructror_class c;
    c.print();
    
    std::cout << result << " - " << result_2 << std::endl;
}