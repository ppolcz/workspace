/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   chrono_helper.hpp
 * Author: polpe
 *
 * Created on February 14, 2016, 9:07 PM
 */

#ifndef CHRONO_HELPER_HPP
#define CHRONO_HELPER_HPP

#include <chrono>
#include <cstdio>

typedef std::chrono::high_resolution_clock t_hclock;

template <typename time_point>
inline void print_elapsed_time (time_point const & start_time)
{
    auto end_time = t_hclock::now();
    int micro = std::chrono::duration_cast<std::chrono::microseconds>(end_time - start_time).count();

    int min = micro / 60e6;
    micro = micro - min * 60e6;
    int sec = micro / 1e6;
    micro = micro - sec * 1e6;
    int milli = micro / 1000;
    micro = micro - milli * 1000;
    if (min > 0) printf("%d [min] %d:%03d:%03d [usec]\n", min, sec, milli, micro);
    else if (sec > 0) printf("%d:%03d:%03d [usec]\n", sec, milli, micro);
    else if (milli > 0) printf("%d:%03d [usec]\n", milli, micro);
    else printf("%d [usec]\n", micro);
}

#define PP_CHRONO_START(id) auto start_time_##id = std::chrono::high_resolution_clock::now()
#define PP_CHRONO_END(id) print_elapsed_time(start_time_##id);

#endif /* CHRONO_HELPER_HPP */

