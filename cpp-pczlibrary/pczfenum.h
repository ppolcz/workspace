/* 
 * File:   pczfenum.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 24, 2014, 6:08 PM
 */

#ifndef PCZFENUM_H
#define	PCZFENUM_H

#include "pczpp.h"

#define P_FENUM_T PEnum

#define P_FENUM_CONSTRUCTOR(...) P_FENUM_T (const string& name, int nr, ##__VA_ARGS__) : name(name), nr(nr)

#define P_FENUM \
struct P_FENUM_T { \
    /**
     * required fields */ \
    const string name; \
    const int nr; \
    /** \
     * Default constructor's signature */ \
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


#endif	/* PCZFENUM_H */

