/* 
 * File:   Runnable.h
 * Author: polpe
 *
 * Created on January 29, 2014, 6:09 PM
 */

#ifndef THREAD_H
#define	THREAD_H

class WorkerThread {
    std::thread m_thread;
    std::function<void() > m_function;

public:

    WorkerThread() { }

    WorkerThread(const std::function<void()> & function) {
        m_function = function;
    }

    void start() {
        m_thread = std::thread(m_function);
    }

    void join() {
        if (m_thread.joinable()) m_thread.join();
    }
};

#endif	/* THREAD_H */

