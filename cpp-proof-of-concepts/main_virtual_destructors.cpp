/* 
 * File:   main_virtual_destructors.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on January 26, 2015, 4:53 PM
 */

#include <iostream>

struct A
{
    virtual ~A()
    {
        std::cout << "~A" << std::endl;
    }
    int memberA;
};

struct B
{
    virtual ~B()
    {
        std::cout << "~B" << std::endl;
    }
    int memberB;
};

struct C
{
    /**
     * In this case:
     * C* c = abc1;
     * only this destructor will be called however invalid pointer error will be raised.
     */
    ~C ()
    {
        std::cout << "~C" << std::endl;
    }
    int memberC;
};

struct ABC : public A, public B, public C
{
    virtual ~ABC()
    {
        std::cout << "~ABC" << std::endl;
    }
};

struct D : public ABC
{
    virtual ~D()
    {
        std::cout << "~D\n";
    }
};

void main_virtual_destructors()
{
    ABC* ab1 = new ABC();
    ABC* ab2 = new ABC();

    A* a = ab1;
    B* b = ab2;

    delete a;
    delete b;
    
    ABC* abc1 = new ABC();
    delete abc1;
    
    std::cout << "---\n";
    D* d = new D();
    delete d;
    std::cout << "---\n";
    A* ad = new D();
    delete ad;
    std::cout << "---\n";
}