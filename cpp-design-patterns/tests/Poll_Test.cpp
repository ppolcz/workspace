/*
 * File:   Poll_Test.cpp
 * Author: polpe
 *
 * Created on Apr 9, 2014, 6:36:39 PM
 */

#include "Poll_Test.h"

#include <mutex>

#include "thread/blocking_queue.h"
#include "thread/poll.h"

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

CPPUNIT_TEST_SUITE_REGISTRATION (Poll_Test);

Poll_Test::Poll_Test () { }

Poll_Test::~Poll_Test () { }

void Poll_Test::setUp () { }

void Poll_Test::tearDown () { }

void Poll_Test::simpleUseCase () {
    numberOfEvaluatedFunctions() = 0;

    Poll poll;

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

void Poll_Test::manyTaskUseCase () {
    numberOfEvaluatedFunctions() = 0;

    Poll poll;
    int N = 50000;

    for (int i = 0; i < N; ++i) {
        poll.push(std::bind(&MyType::valami, MyType(), 1112, 1123));
    }

    poll.join();
    
    CPPUNIT_ASSERT(numberOfEvaluatedFunctions() == N);
}

