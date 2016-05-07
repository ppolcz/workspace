/* 
 * File:   main_thread_1.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 2, 2015, 2:05 PM
 */

#include "thread_object.h"

#include <polcz/pczdebug.h>

#include <iostream>
#include <cstdlib>
#include <thread>
#include <list>
#include <exception>
#include <type_traits>

typedef std::function<void()> command_t;

struct functional
{
    typedef std::function<void()> function_t;
    
    functional(function_t&& fun) : fun(std::move(fun)) { PCZ_DEBUG(); }
    
    void operator()()
    {
        fun();
    }
    
private:
    function_t fun;
};

void launch_command(command_t&& fun)
{
    fun();
}

void launch_command(command_t&& fun, thread_object *to)
{
    to->queue_task(std::move(fun));
}

struct signal
{
    std::list<command_t> slots;

    void emit()
    {
        for (auto& s : slots) s();
    }
    
    void connect(const command_t& slot)
    {
        slots.push_back(slot);
    }
};

struct worker
{
    void public_run(int arg)
    {
        std::cout << "worker::run(int " << arg << ")\n";
        std::this_thread::sleep_for(std::chrono::seconds(4));
    }
};

struct signal_raiser : public thread_object
{
    void run(int)
    {
        
    }
};

void main_blocking_queue()
{
    // testing std::deque
    {
        std::deque<int> d;
        // d.pop_front(); // never call pop if the queue is empty! 

        d.push_back(123);
        std::cout << d.back() << " - " << d.front() << "    -> " << (!d.empty() ? d.front() : -1) << std::endl;

        d.pop_front();
        std::cout << d.back() << " -- " << d.front() << "    -> " << (!d.empty() ? d.front() : -1) << std::endl;

        d.push_back(13);
        std::cout << d.back() << " --- " << d.front() << "    -> " << (!d.empty() ? d.front() : -1) << std::endl;

        d.pop_back();
        std::cout << d.back() << " ---- " << d.front() << "    -> " << (!d.empty() ? d.front() : -1) << std::endl;
    }
    
    // testing blocking_queue
    std::thread pusher, popper;
    blocking_queue<int> q;

    pusher = std::thread([&q]()
    {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        std::cout << "push 1\n";
        q.push(1);

        std::this_thread::sleep_for(std::chrono::seconds(1));
        std::cout << "push 2\n";
        q.push(2);

        std::this_thread::sleep_for(std::chrono::seconds(1));
        std::cout << "push 3\n";
        q.push(3);

        std::this_thread::sleep_for(std::chrono::seconds(1));
        std::cout << "terminate this!\n";

        q.interrupt();
        std::cout << "waiting finished\n";
    });

    popper = std::thread([&q]()
    {
        blocking_queue<int>::return_t val;

        val = q.pop();
        std::cout << val.first << "  validity = " << val.second << std::endl;

        val = q.pop();
        std::cout << val.first << "  validity = " << val.second << std::endl;

        val = q.pop();
        std::cout << val.first << "  validity = " << val.second << std::endl;

        val = q.pop();
        std::cout << val.first << "  validity = " << val.second << std::endl;
    });
    
    worker w;
    std::thread t(std::bind(&worker::public_run, w, 12));
    
    popper.join();
    pusher.join();
    t.join();    
}

#define PCZ_SLEEP(unit, amount) std::this_thread::sleep_for(std::chrono::unit(amount))

void thread_object_test_1()
{
    PCZ_TITLE("join() called BEFORE termination");
    
    thread_object to;
    to.queue_task([] { PCZ_SLEEP(milliseconds, 400); std::cout << "  - sleep_for 1 in test_1 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 500); std::cout << "  - sleep_for 2 in test_1 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 200); std::cout << "  - sleep_for 3 in test_1 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 300); std::cout << "  - sleep_for 4 in test_1 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 800); std::cout << "  - sleep_for 5 in test_1 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 400); std::cout << "  - sleep_for 6 in test_1 \n"; });
    
    to.join();
}

void thread_object_test_2()
{
    PCZ_TITLE("join() called AFTTER termination, queue task after joined (exception expected)");
    
    thread_object to;
    to.queue_task([] { PCZ_SLEEP(milliseconds, 200); std::cout << "  - sleep_for 1 in test_2 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 300); std::cout << "  - sleep_for 2 in test_2 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 500); std::cout << "  - sleep_for 3 in test_2 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 100); std::cout << "  - sleep_for 4 in test_2 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 600); std::cout << "  - sleep_for 5 in test_2 \n"; });
    to.queue_task([] { PCZ_SLEEP(milliseconds, 300); std::cout << "  - sleep_for 6 in test_2 \n"; });
    
    PCZ_SLEEP(seconds, 4);
    PCZ_DEBUG("main thread -- END, calling join()");
    
    to.join();
    try {
        to.queue_task([]{});
    } 
    catch (std::exception& e)
    {
        PCZ_DEBUG(e.what());
    }
}

void main_thread_1()
{
    PCZ_TITLE("testing thread_object on concurrent threads");
    std::thread t1(&thread_object_test_1);
    std::thread t2(&thread_object_test_2);
    
    t1.join();
    t2.join();
}

