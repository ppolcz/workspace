/* 
 * File:   thread_object.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 3, 2015, 10:16 PM
 */

#ifndef THREAD_OBJECT_H
#define	THREAD_OBJECT_H

#include <polcz/pczdebug.h>
#include <polcz/pczutil.h>

#include <thread>

#include "blocking_queue.h"

class thread_object
{    
public:
    typedef std::function<void()> command_t;
    
    thread_object();
    
    ~thread_object();
    
    void queue_task(command_t&& task);
    
    void PCZ_DEPRECATED interrupt();
    
    void terminate();

    void join();
    
    void reset();
    
    bool joinable();
    
private:
    void run();
    
    std::thread t;
    blocking_queue<command_t> q;
    std::atomic_bool terminated;
    std::atomic_bool joined;
};

#endif	/* THREAD_OBJECT_H */

