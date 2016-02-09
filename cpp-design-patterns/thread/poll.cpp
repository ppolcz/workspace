/* 
 * File:   Poll.cpp
 * Author: polpe
 * 
 * Created on January 28, 2014, 8:16 PM
 */

#include "poll.h"

#include <exception>
#include <string>

#include "blocking_queue.h"

Poll::Poll () {
    start();
}

Poll::~Poll () {
    join();
}

void Poll::start () {
    tasks.continueWaiting();

    finished = false;
    detached = false;
    threads.resize(NUMBER_OF_THREADS);
    for (auto & t : threads) t = std::thread(&Poll::run, this);

    tasks.reset();
}

void Poll::join () {
    tasks.stopWaiting();
    finished = true;
    for (auto & t : threads) if (t.joinable()) t.join();
    threads.clear();
}

void Poll::restart () {
    join();
    start();
}

void Poll::reset () {
    tasks.reset();
    restart();
}

void Poll::detach () {
    detached = 1;
    for (auto & t : threads) if (t.joinable()) t.detach();
    threads.clear();
}

void Poll::run () {
    while ((!finished && !detached) || !tasks.isEmpty()) {
        try {
            tasks.pop()();
        } catch (...) {
            continue;
        }
    }
}

void Poll::push (const std::function<void()> & function) {
    if (detached || finished) throw PollException("The poll was already detached or finished, you cannot push more tasks!");
    tasks.push(function);
}

bool Poll::isFinished () {
    return finished;
}

bool Poll::isDetached () {
    return detached;
}

bool Poll::isPollable () {
    return !finished && !detached;
}

unsigned Poll::NUMBER_OF_THREADS = std::thread::hardware_concurrency ();

struct MyType {

    MyType () { }

    MyType (const MyType &) { }

    MyType (MyType &&) { }

    ~MyType () { }

    MyType& operator = (const MyType & t) { }

    void valami (int, int) {
        DEBUG_FUNCTION
    }
};

int poll_main () {

    if (1) { /**
              * Poll test
              */
        Poll poll;

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

