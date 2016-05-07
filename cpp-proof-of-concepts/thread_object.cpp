/* 
 * File:   thread_object.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on February 3, 2015, 10:16 PM
 */

#include "thread_object.h"
#define __PCZ_NO_COUT_DEBUG

thread_object::thread_object()
{
    reset();
    t = std::thread(&thread_object::run, this);
}

thread_object::~thread_object()
{
    if (joinable()) join();
}

void thread_object::queue_task(command_t&& task)
{
    if (joined.load()) throw std::invalid_argument("thread_object was already joined before this task was queued");
    q.push(std::move(task));
}

void thread_object::interrupt()
{
    terminated.store(true, std::memory_order_relaxed);
    q.interrupt();
}

void thread_object::terminate() 
{
    terminated.store(true, std::memory_order_relaxed);
    q.interrupt();
}

void thread_object::join()
{
    joined.store(true, std::memory_order_relaxed);
    if (q.empty()) terminate();
    t.join();
}

void thread_object::reset()
{
    terminated.store(false, std::memory_order_relaxed);
    joined.store(false, std::memory_order_relaxed);
}

bool thread_object::joinable() 
{
    return !joined.load();
}

void thread_object::run()
{
    // PCZ_DEBUG("!%d && (!%d || !%d) = %d", interrupted.load(), joined.load(), q.empty(), !interrupted.load() && (!joined.load() || !q.empty()));
    while (!terminated.load() && (!joined.load() || !q.empty()))
    { try { q.pop().first(); } catch (std::bad_function_call&) { } }

    PCZ_DEBUG("main loop ended");
}
