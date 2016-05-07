/* 
 * File:   BlockingQueue.h
 * Author: polpe
 *
 * Created on January 29, 2014, 6:07 PM
 */

#ifndef BLOCKINGQUEUE_H
#define	BLOCKINGQUEUE_H

#include <mutex>
#include <condition_variable>
#include <deque>
#include <iostream>
#include <cassert>

#define DEBUG std::cout << __FILE__ << ":" << __LINE__ << " , " << __FUNCTION__ << std::endl;
#define DEBUG_ std::cout << __FILE__ << ":" << __LINE__ << " , " << __FUNCTION__ << " --- "
#define DEBUG_FUNCTION std::cout << __PRETTY_FUNCTION__ << std::endl;
#define DEBUG_FUNCTION_BEGIN std::cout << "BEGIN " << __FUNCTION__ << std::endl;
#define DEBUG_FUNCTION_END std::cout << "END " << __FUNCTION__ << std::endl;
#define NEW_SECTION std::cout << "\n==================================\n\n"
#define WATCH(a) std::cout << __FILE__ << ":" << __LINE__ << " , " << __FUNCTION__ << "  " << #a << " = " << a << std::endl;

class InterruptedException : public std::exception {
    std::string msg;
public:

    InterruptedException (std::string const & _msg = "") : msg (_msg) { }

    const char * what () {
        return (std::string("Interrupted: ") + msg).c_str();
    }
};

// =============================================================================================================================================== //

/**
 * Proof of concepts:
 * 
 * First I put elements into it.
 * If it is full than the thread will wait.
 */
template <typename T>
class BlockingQueue {
    static const unsigned MAX_NUMBER_OF_ITEMS = 10;

private:
    std::mutex mutex;
    std::condition_variable cv;
    std::deque<T> q;

    bool waiting = true;
public:

    void stopWaiting ();
    void continueWaiting ();
    void reset ();

    void push (T&& value);
    void push (T const& value);
    T pop ();

    bool isWaiting () const;
    bool isEmpty () const;
    bool isFull () const;

private:
    bool isValid () const;
};

// ----------------------------------------------------------------------------------------------------------------------------------------------- //

template <typename T>
void BlockingQueue<T>::stopWaiting () {
    waiting = false;
    cv.notify_all();
}

template <typename T>
void BlockingQueue<T>::continueWaiting () {
    waiting = true;
}

template <typename T>
void BlockingQueue<T>::reset () {
    q.clear();
    continueWaiting();
}

template <typename T>
void BlockingQueue<T>::push (T&& value) {
    push(value);
}

/**
 * Possible states:
 * [1] wait
 * [2] throw
 * [3] pushed 
 * @param value
 */
template <typename T>
void BlockingQueue<T>::push (T const& value) {
    std::unique_lock<std::mutex> lock(mutex);

    // [1] --> [1] if ( isFull() && isWaiting() )
    cv.wait(lock, [ = ]{return !isFull() || !isWaiting();});

    // [1] --> [2] if ( isFull() && !isWaiting() )
    if (isFull()) throw InterruptedException("push was interrupted, waiting is unset and the queue is full, however push was requested.\n");

    // [1] --> [3] if ( !isFull() )
    q.push_back(value);
    cv.notify_one();

    assert(isValid());
}

/**
 * Possible states:
 * [1] wait
 * [2] throw
 * [3] pop and return 
 * @return
 */
template <typename T>
T BlockingQueue<T>::pop () {
    // create lock
    std::unique_lock<std::mutex> lock(mutex);

    // [1] --> [1] if ( isEmpty() && isWaiting() )
    cv.wait(lock, [ = ]{return !isEmpty() || !isWaiting();});

    // [1] --> [2] if ( isEmpty() && !isWaiting() )
    if (isEmpty()) throw InterruptedException("pop was interrupted.");

    // [1] --> [3] if ( !isEmpty() )
    T value(std::move(q.front()));
    q.pop_front();
    cv.notify_one();

    return value;
}

template <typename T>
inline bool BlockingQueue<T>::isWaiting () const {
    return waiting;
}

template <typename T>
inline bool BlockingQueue<T>::isEmpty () const {
    return q.empty();
}

template <typename T>
inline bool BlockingQueue<T>::isFull () const {
    assert(q.size() <= MAX_NUMBER_OF_ITEMS);
    return MAX_NUMBER_OF_ITEMS == q.size();
}

template <typename T>
inline bool BlockingQueue<T>::isValid () const {
    return q.size() <= MAX_NUMBER_OF_ITEMS;
}

#endif	/* BLOCKINGQUEUE_H */

