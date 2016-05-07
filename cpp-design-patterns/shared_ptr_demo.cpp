/* 
 * File:   SharedPtrDemo.cpp
 * Author: polpe
 * 
 * Created on February 12, 2014, 12:51 AM
 */

#include "shared_ptr_demo.h"

struct DummyStruct {
    int a;

    DummyStruct (int _a = 0) : a (_a) {
        std::cout << "DummyStruct::DummyStruct\n";
    }

    DummyStruct (const DummyStruct & other) : a (other.a) {
        std::cout << "DummyStruct::DummyStruct - Copy\n";
    }

    DummyStruct (DummyStruct&& other) {
        a = std::move(other.a);
        std::cout << "DummyStruct::DummyStruct - Move\n";
    }

    ~DummyStruct () {
        std::cout << "DummyStruct::~DummyStruct\n";
    }
};

SharedPtrDemo::SharedPtrDemo () { }

SharedPtrDemo::SharedPtrDemo (const SharedPtrDemo& orig) { }

SharedPtrDemo::~SharedPtrDemo () { }

void SharedPtrDemo::test1 () {
    array = std::shared_ptr<int>(new int[15]);
    assert(array.use_count() == 1);
    assert(array.unique());

    auto p = array;
    assert(array.use_count() == 2);

    auto pp = p;
    assert(array.use_count() == 3);

    pp.reset();
    assert(!pp);
    assert(array.use_count() == 2);
    assert(array);

    array.reset();
    assert(p.unique());

    // stack variables cannot be wrapped into a shared_ptr (consider: int a; delete &a; )
    size_t memory_address;
    std::shared_ptr<DummyStruct> app;
    {
        DummyStruct a = 4; /* constructor will be invoked */
        std::cout << " ------------------- copy constructor will be invoked:\n";
        app = std::make_shared<DummyStruct>(a /* copy constructor will be invoked */);

        memory_address = (size_t) (&a);
    }

    assert(app);
    assert(app->a == 4);
    assert(memory_address != (size_t) (app.get())); // the memory addresses are not same. First in the stack, second in the heap.

    {
        DummyStruct * a = new DummyStruct(4); /* constructor will be invoked */
        std::cout << " ------------------- no copy constructor:\n";
        std::shared_ptr<DummyStruct> ap(a); /* no copy constructor */

        memory_address = (size_t) a;
        std::cout << a << std::endl;
        
        // just for fun
        app = ap;
    }

    assert(app);
    assert(app->a == 4);
    assert(memory_address == (size_t) (app.get())); // the memory addresses are the same
}

void SharedPtrDemo::test () {
    SharedPtrDemo demo;
    demo.test1();
}

