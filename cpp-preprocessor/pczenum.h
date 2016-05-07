/* 
 * File:   pczenum.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 23, 2014, 11:08 PM
 */

#ifndef PCZENUM_H
#define	PCZENUM_H

#include <boost/preprocessor.hpp>

/**
 * PCZ_PP_REM (enclosed) ANYTHING ==> enclosed ANYTHING
 * PCZ_PP_EAT (enclosed) ANYTHING ==> ANYTHING
 */
#define PCZ_PP_REM(...) __VA_ARGS__
#define PCZ_PP_EAT(...)

#define PCZ_PP_GET_FIRST(x) BOOST_PP_VARIADIC_ELEM_0 x

#define PCZ_PP_GET_BUT_FIRST_I(first,...) ,##__VA_ARGS__
#define PCZ_PP_GET_BUT_FIRST(x) PCZ_PP_GET_BUT_FIRST_I x

#define PCZ_PP_SEQ_APPEND(a,...) a, ##__VA_ARGS__

//PCZ_PP_GET_FIRST((TR_ID, "integer", 12))
//PCZ_PP_GET_BUT_FIRST((TR_ID, "integer", 12))
//
//PCZ_PP_SEQ_APPEND("EnumName", 0 PCZ_PP_GET_BUT_FIRST((TR_ID, "integer", 12)))
//PCZ_PP_SEQ_APPEND("EnumName", 0 PCZ_PP_GET_BUT_FIRST((TR_ID)))

#define P_FENUM_T PEnum

#define P_FENUM_CONSTRUCTOR(...) P_FENUM_T (const string& name, int nr, ##__VA_ARGS__) : name(name), nr(nr)

#define P_FENUM \
class P_FENUM_T { \
public: \
    const string name; \
    const int nr; \
    P_FENUM_T (const string& name, int nr) : name(name), nr(nr)

#define P_FENUM_VALUES(...) \
}; \
template<int N, class Self> struct enum_data {}; \
BOOST_PP_SEQ_FOR_EACH_I(PCZ_ENUM_DECLARE_EACH, P_FENUM_T, BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__)) \
struct enum_metadata { \
    static const int n = BOOST_PP_VARIADIC_SIZE(__VA_ARGS__); \
    template<int N> struct get : public enum_data<N, P_FENUM_T> {}; \
}; 

#define PCZ_ENUM_DECLARE_EACH(r, ClassName, i, x) \
static inline const ClassName* PCZ_PP_GET_FIRST(x)() \
{ \
    static const ClassName VALUE = ClassName(PCZ_PP_SEQ_APPEND(BOOST_PP_STRINGIZE(PCZ_PP_GET_FIRST(x)), i PCZ_PP_GET_BUT_FIRST(x))); \
    return &VALUE; \
} \
template<class Self> \
struct enum_data<i, Self> { static constexpr const Self* value = PCZ_PP_GET_FIRST(x)(); };

#endif	/* PCZENUM_H */

