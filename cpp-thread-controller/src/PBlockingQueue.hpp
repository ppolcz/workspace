/* 
 * File:   PBlockingQueue.hpp
 * Author: Polcz Péter
 *
 * Created on January 29, 2014, 6:07 PM
 */

#ifndef PBLOCKINGQUEUE_HPP
#define	PBLOCKINGQUEUE_HPP

#include <mutex>
#include <condition_variable>
#include <deque>
#include <iostream>
#include <cassert>

#include "pczexcept.h"

/**
 * Proof of concepts:
 * 
 * First I put elements into it.
 * If it is full than the thread will wait.
 */
template <typename T>
class PBlockingQueue
{
public:
    static const unsigned MAX_NUMBER_OF_ITEMS = 10;

private:
    std::mutex mutex;
    std::condition_variable cv;
    std::deque<T> q;

    bool waiting = true;
public:

    void stopWaiting();
    void continueWaiting();
    void reset();

    void push(T&& value);
    void push(T const& value);
    T pop();

    bool isWaiting() const;
    bool isEmpty() const;
    bool isFull() const;

private:
    bool isValid() const;
};

// ----------------------------------------------------------------------------------------------------------------------------------------------- //

template <typename T>
void PBlockingQueue<T>::stopWaiting()
{
    waiting = false;
    cv.notify_all();
}

template <typename T>
void PBlockingQueue<T>::continueWaiting()
{
    waiting = true;
}

template <typename T>
void PBlockingQueue<T>::reset()
{
    q.clear();
    continueWaiting();
}

template <typename T>
void PBlockingQueue<T>::push(T&& value)
{
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
void PBlockingQueue<T>::push(T const& value)
{
    std::unique_lock<std::mutex> lock(mutex);

    // [1] --> [1] if ( isFull() && isWaiting() )
    cv.wait(lock, [ = ]{return !isFull() || !isWaiting();});

    // [1] --> [2] if ( isFull() && !isWaiting() )
    if (isFull()) throw PInterruptedException("push was interrupted, waiting is unset and the queue is full, however push was requested.\n");

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
T PBlockingQueue<T>::pop()
{
    // create lock
    std::unique_lock<std::mutex> lock(mutex);

    // [1] --> [1] if ( isEmpty() && isWaiting() )
    cv.wait(lock, [ = ]{return !isEmpty() || !isWaiting();});

    // [1] --> [2] if ( isEmpty() && !isWaiting() )
    if (isEmpty()) throw PInterruptedException("pop was interrupted.");

    // [1] --> [3] if ( !isEmpty() )
    T value(std::move(q.front()));
    q.pop_front();
    cv.notify_one();

    return value;
}

template <typename T>
inline bool PBlockingQueue<T>::isWaiting() const
{
    return waiting;
}

template <typename T>
inline bool PBlockingQueue<T>::isEmpty() const
{
    return q.empty();
}

template <typename T>
inline bool PBlockingQueue<T>::isFull() const
{
    assert(q.size() <= MAX_NUMBER_OF_ITEMS);
    return MAX_NUMBER_OF_ITEMS == q.size();
}

template <typename T>
inline bool PBlockingQueue<T>::isValid() const
{
    return q.size() <= MAX_NUMBER_OF_ITEMS;
}

#endif	/* PBLOCKINGQUEUE_HPP */

