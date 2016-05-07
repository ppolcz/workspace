/* 
 * File:   diamond_solution.cpp
 * Author: polpe
 * 
 * Created on April 1, 2014, 4:01 PM
 */

#include "diamond_solution.h"
#include <iostream>
#define DEBUG_INFO std::string(" - in file: ") + std::string(__FILE__) + std::string(":") + std::to_string(__LINE__) + std::string(" - ") + std::string(__FUNCTION__) + std::string("()")
#define DEBUG std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "()\n";
#define DEBUG_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "  --  "
#define WATCH(watchee) std::cout << "at line: " << __LINE__ << "  " << #watchee << " = " << watchee << std::endl
#define PCZ_DEBUG std::cout << "at line: " << __LINE__  << std::endl; 

class A {
    
};

class B {
public:
    
};

class C {
public:
    
};

class D : public B, public C{
public:
    
};

int diamond_solution () {
    WATCH(sizeof(A));
    WATCH(sizeof(B));
    WATCH(sizeof(C));
    WATCH(sizeof(D));
}
