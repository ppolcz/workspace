/* 
 * File:   main_move.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 4, 2015, 8:12 PM
 */

#include <polcz/pczdebug.h>

#include <functional>
#include <thread>
#include <vector>

void print_add(int a, int b)
{
    PCZ_DEBUG("sum = %d", a+b);
}

void join_thread(std::thread&& thread)
{
    thread.join();
    PCZ_DEBUG("thread joined using rvalue reference");
}

void join_thread(std::thread& thread)
{
    thread.join();
    PCZ_DEBUG("thread joined using simple reference");
}

struct thread_joiner
{
    std::thread thread;
    
    thread_joiner(std::thread&& t) : thread(std::move(t)) { thread.join(); }
};

struct simple_object
{
    simple_object() { std::cout << "simple_object::create\n"; }
    ~simple_object() { std::cout << "simple_object::destroy\n"; }
};

simple_object return_simple_object()
{
    simple_object s;
    PCZ_DEBUG("simple_object created - pointer = %x", &s);
    return s;
}

#define METHOD_NR 3
void main_move()
{
    PCZ_TITLE("std::move operation: threads");
    
    std::function<void(int, int)> fun = &print_add;
    std::thread thread(fun, 1, 1);
    
#if METHOD_NR == 1

    join_thread(std::move(thread));

#elif METHOD_NR == 2

    join_thread(thread);

#elif METHOD_NR == 3
    
    std::thread thread2 (std::move(thread));
    // std::thread thread2 (thread); // error: use of deleted function ‘std::thread::thread(std::thread&)’

    PCZ_DEBUG("thread.joinable() = %d", thread.joinable()); // -> 0
    PCZ_DEBUG("thread2.joinable() = %d", thread2.joinable()); // -> 1
    
    thread_joiner(std::move(thread2));

#endif
    
    PCZ_TITLE("std::move operation: vector");
    
    // move a vector
    std::vector<int> v = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    PCZ_DEBUG("vector.size() = %d,  pointer to data = %x", v.size(), v.data());
    
    std::vector<int> v2 = std::move(v);
    PCZ_DEBUG("vector_2.size() = %d,   vector.size() = %d,   pointer_2 = %x, pointer_1 = %x", v2.size(), v.size(), v2.data(), v.data());
    
    PCZ_TITLE("std::move operation: return by value");
    
    simple_object s = return_simple_object(); // here s is not a new instance, but is the same as in the function
    PCZ_DEBUG("simple_object is moved to this variable - pointer = %x", &s);
}

