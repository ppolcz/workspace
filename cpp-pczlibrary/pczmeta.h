/* 
 * File:   pczmeta.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 24, 2014, 9:51 PM
 */

#ifndef PCZMETA_H
#define	PCZMETA_H

#include "pczpp.h"

/**
 * (a1, b1)(a2, b2)(a3, b3)...(an, bn) ==> ((a1, b1))((a2, b2))((a3, b3))...((an, bn)) 
 */
#define DETAIL_ADD_PAREN_1(...) ((__VA_ARGS__)) DETAIL_ADD_PAREN_2
#define DETAIL_ADD_PAREN_2(...) ((__VA_ARGS__)) DETAIL_ADD_PAREN_1
#define DETAIL_ADD_PAREN_1_END
#define DETAIL_ADD_PAREN_2_END
#define PCZ_PP_SEQ_TO_DOUBLE_SEQ(seq) BOOST_PP_CAT(DETAIL_ADD_PAREN_1 seq,_END) 

/**
 * PCZ_GET_TYPE((int) x) ==> int
 */
#define PCZ_GET_TYPE(type_and_name) PCZ_FIRST_ARG ( PCZ_TOARG type_and_name )
#define PCZ_FIRST_ARG(...) PCZ_FIRST_ARG_(__VA_ARGS__)
#define PCZ_FIRST_ARG_(a,...) PCZ_PP_REM a
#define PCZ_TOARG(...) (__VA_ARGS__),


#define PCZ_FUN_GET_SIGNATURE_I(...) PCZ_FUN_GET_SIGNATURE_II(__VA_ARGS__)
#define PCZ_FUN_GET_SIGNATURE_II(ret, name, ...) ret name (__VA_ARGS__);

#define PCZ_FUN_GET_SIGNATURE(x) \
    PCZ_FUN_GET_SIGNATURE_I(PCZ_PP_GET0(x) \
        BOOST_PP_SEQ_FOR_EACH(PCZ_FUN_GET_TYPE_,, BOOST_PP_VARIADIC_TO_SEQ((PCZ_PP_GET1(x)) PCZ_PP_GET1__(x))))
#define PCZ_FUN_GET_TYPE_(r, data, x) , PCZ_PP_REM x


#define PCZ_FUN_GET_ARG_TYPES_I(...) PCZ_FUN_GET_ARG_TYPES_II(__VA_ARGS__)
#define PCZ_FUN_GET_ARG_TYPES_II(ret, name, ...) __VA_ARGS__

#define PCZ_FUN_GET_ARG_TYPES(x) \
    PCZ_FUN_GET_ARG_TYPES_I(PCZ_PP_GET0(x) \
        BOOST_PP_SEQ_FOR_EACH(PCZ_FUN_FORMAT_ARG_,, BOOST_PP_VARIADIC_TO_SEQ((PCZ_PP_GET1(x)) PCZ_PP_GET1__(x))))
#define PCZ_FUN_FORMAT_ARG(r, data, x) PCZ_PP_REM x
#define PCZ_FUN_FORMAT_ARG_(r, data, x) , PCZ_GET_TYPE(x)


#define PCZ_FUN_GET_NAMES(x) \
    BOOST_PP_STRINGIZE(PCZ_PP_GET0(x)) \
    BOOST_PP_SEQ_FOR_EACH(PCZ_FUN_ARG_TYPE_NAME_,, BOOST_PP_VARIADIC_TO_SEQ((PCZ_PP_GET1(x)) PCZ_PP_GET1__(x)))
#define PCZ_FUN_ARG_TYPE_NAME_(r, data, x) , BOOST_PP_STRINGIZE(PCZ_GET_TYPE(x))

#define PCZ_FUN_GET_NR_ARGS(x) PCZ_FUN_GET_NR_ARGS_I x
#define PCZ_FUN_GET_NR_ARGS_I(ret,name,...) BOOST_PP_VARIADIC_SIZE(__VA_ARGS__)

/**
 * Examples:
 * 
 * PCZ_PP_GET0((return_t, method_name, (int) arg0, (const char*) arg1, (double) arg2)) ==> return_t
 * PCZ_PP_GET1((return_t, method_name, (int) arg0, (const char*) arg1, (double) arg2)) ==> method_name
 * PCZ_PP_GET1_((return_t, method_name, (int) arg0, (const char*) arg1, (double) arg2)) ==> (int) arg0, (const char*) arg1, (double) arg2
 * PCZ_FUN_GET_SIGNATURE((return_t, method_name, (int) arg0, (const char*) arg1, (double) arg2)) ==> return_t method_name (int arg0 , const char* arg1 , double arg2);
 * PCZ_FUN_GET_NAMES((return_t, method_name, (int) arg0, (const char*) arg1, (double) arg2)) ==> "return_t" , "method_name" , "int" , "const char*" , "double"
 * PCZ_FUN_GET_NAMES((return_t, method_name)) ==> "return_t" , "method_name"
 * PCZ_FUN_GET_SIGNATURE((return_t, method_name)) ==> return_t method_name ();
 */

#define PCZ_REFLECTABLE_METHODS(x) \
    static const int methods_n = BOOST_PP_SEQ_SIZE(PCZ_PP_SEQ_TO_DOUBLE_SEQ(x)); \
    template <int N, class Self> struct method_data { }; \
    BOOST_PP_SEQ_FOR_EACH_I(PCZ_REFLECT_EACH_METHOD, , PCZ_PP_SEQ_TO_DOUBLE_SEQ(x))

#define PCZ_REFLECT_EACH_METHOD(r, d, i, x) \
    PCZ_FUN_GET_SIGNATURE(x) \
    template <class Self> \
    struct method_data<i, Self> \
    { \
        typedef method_data<i, Self> type; \
        typedef PCZ_PP_GET0(x) (Self::*method_type)(PCZ_FUN_GET_ARG_TYPES(x)); \
        \
        static constexpr const method_type fpointer = &Self::PCZ_PP_GET1(x); \
        \
        static constexpr const char* name = BOOST_PP_STRINGIZE(PCZ_PP_GET1(x)); \
        static constexpr const int arg_nr = BOOST_PP_VARIADIC_SIZE x - 2; \
        static const char* data(int n) \
        { \
            static const char* values[] = {PCZ_FUN_GET_NAMES(x)}; \
            return values[n]; \
        } \
    };

//PCZ_REFLECTABLE_METHODS(
//    (void, f)
//    (int, g, (int) i, (const char*) a)
//)

//    PCZ_REFLECTABLE_METHODS(
//        (void, () f)
//        (int, (const) g, (int) i, (const char*) a)
//        )

/**
 * PCZ_REFLECTABLE_METHODS(
 *     (void, f)
 *     (int, g, (int) i, (const char*) a)
 * )
 * 
 * will result in:
 *
 * template <int N, class Self> struct method_data { };
 * 
 * void f();
 * template <class Self> struct method_data<0, Self>
 * {
 *     typedef method_data<0, Self> type;
 *     typedef void (Self::*method_type)();
 *     static constexpr const method_type fpointer = &Self::f;
 *     static constexpr const char* name = "f";
 *     static constexpr const int arg_nr = 2 - 2;
 * 
 *     static const char* data(int n)
 *     {
 *         static const char* values[] = {"void", "f"};
 *         return values[n];
 *     }
 * };
 * 
 * int g(int i, const char* a);
 * template <class Self> struct method_data<1, Self>
 * {
 *     typedef method_data<1, Self> type;
 *     typedef int (Self::*method_type)(int, const char*);
 *     static constexpr const method_type fpointer = &Self::g;
 *     static constexpr const char* name = "g";
 *     static constexpr const int arg_nr = 4 - 2;
 * 
 *     static const char* data(int n)
 *     {
 *         static const char* values[] = {"int", "g", "int", "const char*"};
 *         return values[n];
 *     }
 * };
 */


// Strip off the type
#define PCZ_FIELD_GET_NAME(x) PCZ_PP_EAT x
// Show the type without parenthesis
#define PCZ_FIELD_GET_DECLARATION(x) PCZ_PP_REM x

template<class M, class T>
struct make_const
{
    typedef T type;
};

template<class M, class T>
struct make_const<const M, T>
{
    typedef typename std::add_const<T>::type type;
};

#define PCZ_REFLECTABLE_FIELDS(...) \
static const int fields_n = BOOST_PP_VARIADIC_SIZE(__VA_ARGS__); \
friend struct reflector; \
template<int N, class Self> struct field_data {}; \
BOOST_PP_SEQ_FOR_EACH_I(PCZ_REFLECT_EACH_FIELD, data, BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__))

#define PCZ_REFLECT_EACH_FIELD(r, data, i, x) \
PCZ_FIELD_GET_DECLARATION(x); \
template<class Self> \
struct field_data<i, Self> \
{ \
    Self & self; \
    field_data(Self & self) : self(self) {} \
    \
    typename make_const<Self, PCZ_GET_TYPE(x)>::type & get() \
    { \
        return self.PCZ_FIELD_GET_NAME(x); \
    } \
    typename boost::add_const<PCZ_GET_TYPE(x)>::type & get() const \
    { \
        return self.PCZ_FIELD_GET_NAME(x); \
    } \
    const char * name() const \
    { \
        return BOOST_PP_STRINGIZE(PCZ_FIELD_GET_NAME(x)); \
    } \
}; \

#endif	/* PCZMETA_H */
