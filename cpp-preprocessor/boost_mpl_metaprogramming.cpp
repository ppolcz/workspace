/* 
 * File:   boost_mpl_metaprogramming.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on October 22, 2014, 10:41 PM
 */

#include <iostream>
#include <boost/mpl/for_each.hpp>
#include <boost/mpl/size.hpp>
#include <boost/mpl/back_inserter.hpp>
#include <boost/mpl/copy.hpp>
#include <boost/mpl/push_back.hpp>
#include <boost/mpl/vector.hpp>
#include <boost/mpl/range_c.hpp>
#include <boost/static_assert.hpp>

#include "template_things.h"

template <class F, int X>
struct twice
{
    enum { once = F::template apply<X>::value }; // f(x)
    enum { value = F::template apply<once>::value }; // f(f(x))
};

template <class F, class X>
struct twice2
{
    typedef typename F::template apply<X::value>::type once; // f(x)
    typedef typename F::template apply<once::value>::type type; // f(f(x))
};

namespace detail {
    template <int N> struct factorial
    {
        enum
        {
            value = N * factorial < N - 1 > ::value
        };
    };

    template<> struct factorial<0>
    {
        enum
        {
            value = 1
        };
    };
}

template <int N>
struct integral_t
{
    enum { value = N };
};

struct factorial_f
{
    template <int N> struct apply
    { 
        enum { value = detail::factorial<N>::value }; 
    };
};

/**
 * This is a meta function!
 */
struct increment_f
{
    template <int N> struct apply
    {
        typedef integral_t<N+1> type;
        enum { value = N + 1 };
    };
};

struct print_index
{
    template <typename I>
    void operator()(I)
    { std::cout << I::value << std::endl; }
};

void mpl_main ()
{
    std::cout << factorial_f::apply<4>::value << std::endl;
    std::cout << twice<increment_f, 4>::value << std::endl;
    std::cout << twice2<increment_f, integral_t<6>>::type::value << std::endl;
    
    {
        namespace bm = boost::mpl;
        
//        typedef bm::copy<
//            bm::range_c<int, 0, 50>, 
//            bm::push_back<bm::_,bm::_>, 
//            bm::vector<>
//            >::type numbers;
        
        /**
         * Igazabol fogalmam sincs, hogy ez mire is jo...
         */
        typedef bm::copy<
            bm::range_c<int, 1, 50>, 
            bm::back_inserter< bm::vector<int> >
            >::type numbers;
        
        BOOST_STATIC_ASSERT( bm::size<numbers>::type::value == 50 );
        
        /**
         * Rendkivul hasznos dolog!
         */
        bm::for_each<bm::range_c<int, 1, 50>>(print_index());
    }
}