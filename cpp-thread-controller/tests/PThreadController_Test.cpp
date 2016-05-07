/*
 * File:   PThreadController_Test.cpp
 * Author: deva
 *
 * Created on May 16, 2014, 11:12:12 AM
 */

#include <mutex>

#include "PThreadController_Test.h"
#include "PBlockingQueue.hpp"
#include "PThreadController.h"

int& numberOfEvaluatedFunctions () {
    static int NR_FUNCTIONS = 0;
    return NR_FUNCTIONS;
}

std::mutex m;

void incNumber () {
    m.lock();
    ++numberOfEvaluatedFunctions();
    m.unlock();
}

struct MyType {

    MyType () { }

    MyType (const MyType &) { }

    MyType (MyType &&) { }

    ~MyType () { }

    MyType& operator = (const MyType & t) { }

    void valami (int a, int b) {
        incNumber();
        for (int i = 0; i < 10000; ++i)
            b = a * i + a * a + b * b + i * a * a * b + b * b * a * a + a * b * a * (float) i * b * a + (float) a / (float) b;

        // The problem is, that for the std::cout object I do not made a mutex, therefore this can cause an error.
        // std::cout << std::this_thread::get_id() << " : ";
        // DEBUG_FUNCTION
    }

};

CPPUNIT_TEST_SUITE_REGISTRATION (PThreadController_Test);

PThreadController_Test::PThreadController_Test () { }

PThreadController_Test::~PThreadController_Test () { }

void PThreadController_Test::setUp () { }

void PThreadController_Test::tearDown () { }

void PThreadController_Test::simpleUseCase () {
    numberOfEvaluatedFunctions() = 0;

    PThreadController poll;

    // -------- 

    poll.push([] {
        incNumber();
        std::cout << "Whahoo\n";
    });
    poll.push(std::bind(&MyType::valami, MyType(), 12, 123));

    poll.join();
    CPPUNIT_ASSERT(numberOfEvaluatedFunctions() == 2);

    // --------

    int N = 100;

    poll.start();
    for (int i = 0; i < N; ++i)
        poll.push(std::bind(&MyType::valami, MyType(), 1112, 1123));

    poll.join();
    CPPUNIT_ASSERT(numberOfEvaluatedFunctions() == (2 + N));
}

void PThreadController_Test::manyTaskUseCase () {
    numberOfEvaluatedFunctions() = 0;

    PThreadController poll;
    int N = 50000;

    for (int i = 0; i < N; ++i) {
        poll.push(std::bind(&MyType::valami, MyType(), 1112, 1123));
    }

    poll.join();
    
    CPPUNIT_ASSERT(numberOfEvaluatedFunctions() == N);
}

