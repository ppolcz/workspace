/* 
 * File:   PThreadController.h
 * Author: Polcz Péter
 *
 * Created on January 28, 2014, 8:16 PM
 */

#ifndef PTHREAD_CONTROLLER_H
#define	PTHREAD_CONTROLLER_H

#include <vector>
#include <thread>

#include "PBlockingQueue.hpp"

class PThreadController
{
    static const unsigned NUMBER_OF_THREADS;

    std::vector< std::thread > threads;
    PBlockingQueue< std::function< void() > > tasks;

    int detached = 0;
    int finished = 0;

private:
    void run();

public:
    PThreadController();
    PThreadController(const PThreadController& orig) = delete;
    virtual ~PThreadController();

    void start();
    void join();
    void detach();
    void restart();
    void reset();

    void push(const std::function<void()> & function);

    bool isPollable();
    bool isDetached();
    bool isFinished();
};

// ---------------------------------------------------------------------------------------------------------------------- //

class PollException : std::exception
{
    std::string msg = "PollException";
public:

    PollException(const std::string & msg = "") : msg(msg) { }

    const char* what() const noexcept
    {
        return msg.c_str();
    }
};

int poll_main();

#endif	/* PTHREAD_CONTROLLER_H */

