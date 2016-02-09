/* 
 * File:   PThreadController.cpp
 * Author: Polcz Péter
 * 
 * Created on January 28, 2014, 8:16 PM
 */

#include "PThreadController.h"

#include <exception>
#include <string>

#include "PBlockingQueue.hpp"

PThreadController::PThreadController ()
{
    start();
}

PThreadController::~PThreadController ()
{
    join();
}

void PThreadController::start ()
{
    tasks.continueWaiting();

    finished = false;
    detached = false;
    threads.resize(NUMBER_OF_THREADS);
    for (auto & t : threads) t = std::thread(&PThreadController::run, this);

    tasks.reset();
}

void PThreadController::join ()
{
    tasks.stopWaiting();
    finished = true;
    for (auto & t : threads) if (t.joinable()) t.join();
    threads.clear();
}

void PThreadController::restart ()
{
    join();
    start();
}

void PThreadController::reset ()
{
    tasks.reset();
    restart();
}

void PThreadController::detach ()
{
    detached = 1;
    for (auto & t : threads) if (t.joinable()) t.detach();
    threads.clear();
}

void PThreadController::run ()
{
    while ((!finished && !detached) || !tasks.isEmpty())
    {
        try
        {
            tasks.pop()();
        }
        catch (...)
        {
            continue;
        }
    }
}

void PThreadController::push (const std::function<void()> & function)
{
    if (detached || finished) throw PollException("The poll was already detached or finished, you cannot push more tasks!");
    tasks.push(function);
}

bool PThreadController::isFinished ()
{
    return finished;
}

bool PThreadController::isDetached ()
{
    return detached;
}

bool PThreadController::isPollable ()
{
    return !finished && !detached;
}

const unsigned PThreadController::NUMBER_OF_THREADS = std::thread::hardware_concurrency ();

struct MyType
{

    MyType () { }

    MyType (const MyType &) { }

    MyType (MyType &&) { }

    ~MyType () { }

    MyType& operator = (const MyType & t) { }

    void valami (int, int)
    {
        std::cout << "valami\n";
    }
};

int poll_main ()
{

    if (1)
    { /**
              * Poll test
              */
        PThreadController poll;

        poll.push([] {
            std::cout << "Whahoo\n";
        });
        poll.push(std::bind(&MyType::valami, MyType(), 12, 123));

        poll.reset();
        poll.join();
        poll.restart();

        poll.push([] {
            std::cout << "Whahoo\n";
        });
        poll.push(std::bind(&MyType::valami, MyType(), 12, 123));

        poll.reset();
    }
}

