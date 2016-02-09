/* 
 * File:   blocking_queue.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 2, 2015, 5:49 PM
 */

#ifndef BLOCKING_QUEUE_H
#define	BLOCKING_QUEUE_H

#include <mutex>
#include <condition_variable>
#include <deque>
#include <iostream>
#include <cassert>
#include <atomic>

/**
 * Proof of concepts:
 *
 * First I put elements into it.
 * If it is full than the thread will wait.
 * 
 * Data structure: FIFO
 * Wait-notify technique
 */
template <typename T>
class blocking_queue
{
public:
    typedef std::pair<T, bool> return_t;
    static const unsigned MAX_NUMBER_OF_ITEMS = 10;

private:
    std::mutex mutex;
    std::condition_variable cv;
    std::deque<T> queue;
    std::atomic_bool interrupted;

public:
    
    /* default constructor */
    blocking_queue() { reset(); }
    
    /* stop waiting if pull or push is was blocked */
    void interrupt();
    
    /* clear interrupted flag */
    void reset();
    
    /* clear dequeue */
    void clear();
    
    /* push new T instance into the queue or waiting if it's full */
    void push(T&& value);
    void push(T const& value);
    
    /* pop the first instance in the queue or waiting if empty */
    return_t pop();

    /* notify_one delegate method */
    void notify_one() { cv.notify_one(); }
    
    /* notify_all delegate method */
    void notify_all() { cv.notify_all(); }
    
    /* is empty? */
    bool empty() const;
    
    /* is full? */
    bool full() const;

private:
    /* is valid? */
    bool valid() const;
};

// ----------------------------------------------------------------------------------------------------------------------------------------------- //
// ----------------------------------------------------------------------------------------------------------------------------------------------- //
// ----------------------------------------------------------------------------------------------------------------------------------------------- //

template <typename T>
void blocking_queue<T>::interrupt()
{
    interrupted.store(true, std::memory_order_relaxed);

    /* notify all thread, which are waiting for push or pop operation */
    cv.notify_all();
}

template <typename T>
void blocking_queue<T>::reset()
{
    interrupted.store(false, std::memory_order_relaxed);
}

template <typename T>
void blocking_queue<T>::clear()
{
    queue.clear();
    reset();
}

template <typename T>
void blocking_queue<T>::push(T&& value)
{
    push(value);
}

template <typename T>
void blocking_queue<T>::push(T const& value)
{
    std::unique_lock<std::mutex> lock(mutex);

    cv.wait(lock, [ = ]{
        return !full() || interrupted.load();
    });

    queue.push_back(value);
    
    /* notify only one thread to continue, 
     * which surely is waiting for a pop operation */
    cv.notify_one();

    assert(valid());
}

template <typename T>
auto blocking_queue<T>::pop() -> return_t
{
    std::unique_lock<std::mutex> lock(mutex);

    cv.wait(lock, [ = ]{
        return !empty() || interrupted.load();
    });

    return_t ret;
    if (empty()) 
        ret = std::make_pair(T(), false);
    else
    {
        ret = std::make_pair(T(std::move(queue.front())), true);
        queue.pop_front();
    }
    
    /* notify only one thread,
     * which surely is waiting for a push operation */
    cv.notify_one();

    return ret;
}

template <typename T>
inline bool blocking_queue<T>::empty() const
{
    return queue.empty();
}

template <typename T>
inline bool blocking_queue<T>::full() const
{
    assert(queue.size() <= MAX_NUMBER_OF_ITEMS);
    return MAX_NUMBER_OF_ITEMS == queue.size();
}

template <typename T>
inline bool blocking_queue<T>::valid() const
{
    return queue.size() <= MAX_NUMBER_OF_ITEMS;
}

#endif	/* BLOCKING_QUEUE_H */

