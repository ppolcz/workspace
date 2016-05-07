/* 
 * File:   main_multiple_inheritence.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 15, 2015, 11:59 PM
 */

#include <cstdio>

struct A
{
    virtual void f() { }
    virtual void g() { printf("A::g()\n"); f(); }
};

struct B
{
    virtual void f() { printf("B::f()\n"); }
    virtual void g() { }
};

struct AB : virtual public A, virtual public B
{
    using A::g;
    using B::f;
};
 
void main_multiple_inheritance()
{
    AB ab;
    ab.f();
    ab.g();
    // not working as I have thought it will
}

