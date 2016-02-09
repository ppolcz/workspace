/* 
 * File:   main_virtual_things.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on January 25, 2015, 11:04 PM
 */

#include <iostream>
#include <polcz/pczutil.h>

template <typename _ChildT>
class Base
{
protected:
    float (_ChildT::* member_pointer)();
    
public:
    void f()
    {
        std::cout << (((_ChildT*) this)->*member_pointer)() << std::endl;
    }

    void g()
    {
        std::cout << "Base::g()\n";
    }
};

class Child : public Base<Child>
{
public:
    Child ()
    {
        this->member_pointer = &Child::kutyagumi;
    }
    
    float kutyagumi () { return 1.0f; }
    
    void g()
    {
        std::cout << "Child::g()\n";
    }
};

class VirtualBase
{
public:
    void name()
    {
        std::cout << "VirtualBase -> ";
    }
    
    virtual void g()
    {
        std::cout << "VirtualBase::g()\n";
    }
    
    virtual ~VirtualBase () { }
};

class VirtualChild : public VirtualBase
{
public:
    void name()
    {
        std::cout << "VirtualChild -> ";
    }
    
    void g()
    {
        std::cout << "VirtualChild::g()\n";
    }
};

void main_virtual_things()
{
    Child c;
    c.f();
    c.g();
    
    Base<Child>* b = new Child();
    b->f();
    b->g();
    
    VirtualBase* vb = new VirtualChild();
    vb->name();
    vb->g();
    
    delete b;
    delete vb;
}
