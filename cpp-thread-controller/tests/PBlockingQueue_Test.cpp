/*
 * File:   BlockingQueue_Test.cpp
 * Author: polpe
 *
 * Created on Apr 9, 2014, 5:35:33 PM
 */

#include "PBlockingQueue_Test.h"

#include <thread>

CPPUNIT_TEST_SUITE_REGISTRATION (PBlockingQueue_Test);

PBlockingQueue_Test::PBlockingQueue_Test () { }

PBlockingQueue_Test::~PBlockingQueue_Test () { }

void PBlockingQueue_Test::setUp () { }

void PBlockingQueue_Test::tearDown () { }

/**
 * BlockingQueue test
 *  - when some instances remain in the queue and they are not popped
 */
void PBlockingQueue_Test::simpleUseCase ()
{

    PBlockingQueue<MyType> q;
    q.push(MyType());
    q.push(MyType());
    q.pop();
}

void PBlockingQueue_Test::popInterruptUseCase ()
{
    PBlockingQueue<MyType> que;
    std::thread releaser([&] {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        que.stopWaiting();
    });

    MyType a;
    que.push(MyType());
    que.push(a);

    a = que.pop();
    a = que.pop();
    try
    {
        a = que.pop();
    }
    catch (PInterruptedException & ex)
    {
    }

    releaser.join();
}

/**
 * BlockingQueue test
 *  - when to much instances are pushed and there is not enough space in the queue
 */
void PBlockingQueue_Test::tooMutchPushUseCase ()
{
    int elvegzes_sorrendje = 0;

    PBlockingQueue<MyType> que;
    std::thread popper([&] {
        std::this_thread::sleep_for(std::chrono::seconds(1));

        CPPUNIT_ASSERT(++elvegzes_sorrendje == 3);

        // EZ FOGJA FELSZABADITANI A BERAGADAST
        que.pop();

        CPPUNIT_ASSERT(++elvegzes_sorrendje == 4);
    });

    CPPUNIT_ASSERT(++elvegzes_sorrendje == 1);

    for (int i = 0; i < que.MAX_NUMBER_OF_ITEMS; ++i) que.push(MyType());

    CPPUNIT_ASSERT(++elvegzes_sorrendje == 2);

    // ITT BE FOG RAGADNI
    que.push(MyType());

    CPPUNIT_ASSERT(++elvegzes_sorrendje == 5);

    popper.join();
}
