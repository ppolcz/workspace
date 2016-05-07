/* 
 * File:   Poll.h
 * Author: polpe
 *
 * Created on January 28, 2014, 8:16 PM
 */

#ifndef POLL_H
#define	POLL_H

#include <vector>
#include <thread>

#include "blocking_queue.h"

class Poll {
    static unsigned NUMBER_OF_THREADS;

    std::vector< std::thread > threads;
    BlockingQueue< std::function< void() > > tasks;

    int detached = 0;
    int finished = 0;

private:
    void run ();

public:
    Poll ();
    Poll (const Poll& orig) = delete;
    virtual ~Poll ();

    void start ();
    void join ();
    void detach ();
    void restart ();
    void reset ();
    
    void push (const std::function<void()> & function);

    bool isPollable ();
    bool isDetached ();
    bool isFinished ();
};

// ---------------------------------------------------------------------------------------------------------------------- //

class PollException : std::exception {
    std::string msg = "PollException";
public:

    PollException (const std::string & msg = "") : msg (msg) { }

    const char* what () const noexcept {
        return msg.c_str();
    }
};

int poll_main ();

#endif	/* POLL_H */

