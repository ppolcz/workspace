/* 
 * File:   main_dynamic_link_constructor.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on January 26, 2015, 7:01 PM
 */

#include <iostream>

struct BaseClass
{
    BaseClass()
    {
        // THINGS TO LEARN:
        // warning: pure virtual ‘virtual void BaseClass::f()’ called from constructor
        // f();
    }
    
    virtual ~BaseClass()
    {
        // THINGS TO LEARN:
        // warning: pure virtual ‘virtual void BaseClass::f()’ called from destructor
        // f();
        
        /* pure virtual method called, terminate called without an active exception */
        // g(); 
    }
    
    void g()
    {
        std::cout << "BaseClass::g() --> ";
        f();
    }
    
    virtual void f() = 0;
};

struct ChildClass : public BaseClass
{
    ChildClass() : BaseClass() {}
    
    void f() 
    {
        std::cout << "ChildClass::f()" << std::endl;
    }
};

int main_dynamic_link_constructor()
{
    {
        ChildClass c;
        c.f();
        c.g();
    }
    {
        ChildClass* c = new ChildClass();
        c->f();
        c->g();

        delete c;
    }
    {
        BaseClass* c = new ChildClass();
        c->f();
        c->g();

        delete c;
    }
    return 0;
}