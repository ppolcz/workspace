/* 
 * File:   diamond_hierarchy.cpp
 * Author: polpe
 * 
 * Created on March 31, 2014, 10:51 PM
 */

#include <iostream>
#include "diamond_hierarchy.h"
#include "pattern_free.h"

#define PCZ_DEBUG std::cout << __PRETTY_FUNCTION__ << std::endl;

namespace dia {

class Base {
public:

    virtual void f () {
        PCZ_DEBUG
    }
};

class A : public Base {
public:

    virtual void f () {
        PCZ_DEBUG
    }
};

class B : public Base {
public:
};

class Child : virtual public A, public B {
public:
};

// --------------------------------------------------------------- //

class IBase {
protected:
    virtual void t () = 0;
};

class IBaseA : protected IBase {
public:
    virtual void g () = 0;
    virtual void f () = 0;
    virtual void h () = 0;
};

class IBaseB : protected IBase {
protected:
    virtual void g () = 0;
};

class Impl : public IBaseB, public IBaseA {
public:

    virtual void g () {
        PCZ_DEBUG
    }

    virtual void f () { }

    virtual void h () { }

    virtual void t () { }

};

// --------------------------------------------------------------- //

/**
 * If virtual inheritance:
 * Child : virtual public A, virtual public B {}
 * then "(A*) child" is not valid
 */

void test () {
    Child * child = new Child();
    // child->f(); // error
    child->A::f();
    child->B::f();
    // (A::Base *) child; // error
    delete child;

    Impl * a = new Impl();
    IBaseA * b = new Impl();
    // IBaseB * b = new Impl(); // error - inaccessible
    // IBase * b = new Impl(); // error - ambiguous 

    B * base = new Child();
    std::cout << "         base = " << base << std::endl;
    std::cout << "(Child*) base = " << (Child*) base << std::endl;

    std::cout << "(Impl*)   b = " << (Impl*) b << std::endl;
    std::cout << "(IBaseA*) b = " << (IBaseA*) b << std::endl;
    std::cout << "(IBaseB*) b = " << (IBaseB*) b << std::endl;
    std::cout << "(IBase*)  b = " << (IBase*) b << std::endl;


    a->g();
    b->g();

    delete a;
    // delete b; // segmentation fault
    delete (Impl*) b;
}

} /* namespace dia */
