/* 
 * File:   PMyClass.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 24, 2014, 6:26 PM
 */

#ifndef PMYCLASS_H
#define	PMYCLASS_H

#include <boost/mpl/range_c.hpp>
#include <boost/type_traits/add_const.hpp>

#include <functional>
#include <tuple>

#include <polcz/pczreflection.h>

class PMyClass
{
public:
    PMyClass();
    virtual ~PMyClass();

    template <class T>
    T template_method(const T& a)
    {
        return a;
    }

    PCZ_REFLECTABLE_FIELDS(
        (int) int_field,
        (string) string_field
        )

    PCZ_REFLECTABLE_METHODS(
        (void, f)
        (int, g, (int) i, (const char*) a)
        )

public:

    /**
     * This is a manual example, how to create meta data of a method
     */
    int method(double a, int b);

    template <class Self>
    struct method_data<-3, Self>
    {
        typedef method_data<-3, Self> type;
        typedef int (Self::*method_type)(double, int);

        static constexpr const method_type fpointer = &Self::method;
        static constexpr const char* name = "method";
        static constexpr const int arg_nr = 4 - 2;

        static const char* data(int n)
        {
            static const char* values[] = {"int", "method", "double", "int"};
            return values[n];
        }
    };
};


#endif	/* PMYCLASS_H */

