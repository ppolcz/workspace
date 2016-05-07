/*
 * File:   BlockingQueue_Test.cpp
 * Author: polpe
 *
 * Created on Apr 9, 2014, 5:35:33 PM
 */

#include "BlockingQueue_Test.h"

#include <thread>

CPPUNIT_TEST_SUITE_REGISTRATION (BlockingQueue_Test);

BlockingQueue_Test::BlockingQueue_Test () { }

BlockingQueue_Test::~BlockingQueue_Test () { }

void BlockingQueue_Test::setUp () { }

void BlockingQueue_Test::tearDown () { }

/**
 * BlockingQueue test
 *  - when some instances remain in the queue and they are not popped
 */
void BlockingQueue_Test::simpleUseCase () {

    BlockingQueue<MyType> q;
    q.push(MyType());
    q.push(MyType());
    q.pop();
}

void BlockingQueue_Test::popInterruptUseCase () {
    BlockingQueue<MyType> que;
    std::thread releaser([&] {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        que.stopWaiting();
    });

    MyType a;
    que.push(MyType());
    que.push(a);

    a = que.pop();
    a = que.pop();
    try {
        a = que.pop();
    } catch (InterruptedException & ex) {
    }

    releaser.join();
}

/**
 * BlockingQueue test
 *  - when to much instances are pushed and there is not enough space in the queue
 */
void BlockingQueue_Test::tooMutchPushUseCase () {
    BlockingQueue<MyType> que;
    std::thread popper([&] {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        que.pop();
    });

    for (int i = 0; i < 101; ++i) {
        que.push(MyType());
    }

    popper.join();
}
