/* 
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 8, 2014, 5:53 PM
 */

#include <cstdlib>
#include <iostream>

#include <polcz/pczdebug.h>

using namespace std;

// https://github.com/pfultz2/Cloak/wiki/C-Preprocessor-tricks,-tips,-and-idioms

#define CHECK_N(x, n, ...) n
#define CHECK(...) CHECK_N(__VA_ARGS__, 0,)
#define PROBE(x) x, 1,
#define IS_PAREN(x) CHECK(IS_PAREN_PROBE x)
#define IS_PAREN_PROBE(...) PROBE(~)

#define CAT(a, ...) PRIMITIVE_CAT(a, __VA_ARGS__)
#define PRIMITIVE_CAT(a, ...) a ## __VA_ARGS__
#define PCZ_RECURSION_(...) PCZ_RECURSION(__VA_ARGS__)
#define PCZ_RECURSION(firstarg, ...) CAT(firstarg, PCZ_RECURSION_(__VA_ARGS__))


#include <boost/preprocessor/cat.hpp>
#include <boost/preprocessor/seq/for_each.hpp>

#define __PCZ_UNUSED(r, data, elem) ;
#define PCZ_UNUSED(seq) BOOST_PP_SEQ_FOR_EACH(__PCZ_UNUSED,, seq)
#define PCZ_NONAME __NEVER_USE_THIS_VARIABLE_AGAIN_PLEASE__

#include "PEnumExample.h"

int main_reflection();

/*
 * 
 */
int main (int argc, char** argv)
{
    {
        int b = 0;

        std::cout << CHECK(faroktoll) << std::endl;
        std::cout << CHECK(1, 2) << std::endl;
        std::cout << CHECK(1, 2, 3) << std::endl;
        std::cout << CHECK(az) << std::endl;
        std::cout << CHECK(az, b) << std::endl;
        std::cout << CHECK(az, b, c) << std::endl;

        std::cout << IS_PAREN((a,b,c)) << std::endl;
        std::cout << IS_PAREN(12) << std::endl;
    //    std::cout << PCZ_RECURSION(1, 2, 3, 4, 5) << std::endl;

        (void) (argc);
        (void) (argv);
    }

    PCZ_DEBUG(PEnumExample::TR_DATE()->name);
    PCZ_DEBUG_W(PEnumExample::enum_metadata::n);
    
    {
        using namespace PEnumExample;
        PCZ_DEBUG(TR_ID()->sqltype);
        PCZ_DEBUG(NONE()->sqltype);
        NONE()->ize_bize();
    }
    
//    PCZ_UNUSED(((int, arg0))((float, arg1))((int, arg2)))
 
    return main_reflection();
}
