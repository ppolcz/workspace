/* 
 * File:   pczreflection.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 25, 2014, 1:52 AM
 */

#ifndef PCZREFLECTION_H
#define	PCZREFLECTION_H

#include "pczstring.h"
#include "pczmeta.h"
#include "function_traits.h"

template <int N, class T>
struct meta_method : public T::template method_data<N, T>
{
    typedef typename T::template method_data<N, T> method_data;
    typedef typename method_data::method_type method_type;

    static constexpr const method_type fpointer = method_data::fpointer;
    static constexpr const int arg_nr = method_data::arg_nr;
    static constexpr const char* name = method_data::name;

    T& t;

    meta_method(T & t) : t(t) { }

    string get_signature() const
    {
        string ret = string(this->data(0)) + " " + name + "(";
        for (int i = 2; i <= arg_nr; ++i) ret += string(this->data(i)) + ", ";
        return ret + this->data(arg_nr+1) + ")";
    }

    string get_uml_signature() const
    {
        string ret = string(name) + "(";
        for (int i = 2; i <= arg_nr; ++i) ret += string(this->data(i)) + ", ";
        return ret + this->data(arg_nr+1) + ") : " + this->data(0);
    }
    
    template <typename... Args> typename function_traits<method_type>::result_type call(Args... args)
    { return (t.*fpointer)(args...); }
};

template <int N, class T>
constexpr const char* meta_method<N, T>::name;

template <int N, class T>
constexpr const int meta_method<N, T>::arg_nr;

struct reflector
{
    template <int N, class T>
    static meta_method<N, T> method(T& t)
    {
        return meta_method<N, T>(t);
    };
    
    /**
     * Get field_data at index N
     */
    template<int N, class T>
    static typename T::template field_data<N, T> field (T& t)
    {
        return typename T::template field_data<N, T>(t);
    }

    /**
     * Get number of fields
     */
    template<class T>
    struct fields
    {
        static const int n = T::fields_n;
    };

    /**
     * Get number of fields
     */
    template<class T>
    struct methods
    {
        static const int n = T::method_n;
    };
};


#endif	/* PCZREFLECTION_H */

