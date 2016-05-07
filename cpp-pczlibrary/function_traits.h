/* 
 * File:   function_traits.h
 * Author: nawaz <http://stackoverflow.com/users/415784/nawaz>
 *
 * Created on October 25, 2014, 1:55 AM
 */

#ifndef FUNCTION_TRAITS_H
#define	FUNCTION_TRAITS_H

template<typename T>
struct function_traits;

template<typename R, typename ...Args>
struct function_traits<std::function<R(Args...)>>
{
    static const size_t nargs = sizeof...(Args);

    typedef R result_type;

    template <size_t i> struct arg
    { typedef typename std::tuple_element<i, std::tuple<Args...>>::type type; };
};

/**
 * I have made a little improvement on it in order to work on native member function pointers to
 */
template<class Class, typename R, typename ...Args>
struct function_traits<R(Class::*)(Args...)> : function_traits<std::function<R(Args...)>> { };

template<class Class, typename R, typename ...Args>
struct function_traits<R(Class::*)(Args...) const> : function_traits<std::function<R(Args...)>> { };


#endif	/* FUNCTION_TRAITS_H */

