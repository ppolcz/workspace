/* 
 * File:   main_pp.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 17, 2015, 9:08 PM
 * 
 * g++ -E -std=c++11 -o main_pp.pre main_pp.cpp 
 */

#include <polcz/pczutil.h>

#include <boost/preprocessor/seq/for_each.hpp>
#include <boost/preprocessor/variadic/to_seq.hpp>
#include <boost/preprocessor/cat.hpp>

#define __PCZ_TYPEDEF_INTEGRAL_TMP(r, data, elem) typedef std::integral_constant<int, elem> BOOST_PP_CAT(elem, _t);
#define __PCZ_TYPEDEF_INTEGRAL_IMP(seq) BOOST_PP_SEQ_FOR_EACH(__PCZ_TYPEDEF_INTEGRAL_TMP,, seq)
#define __PCZ_TYPEDEF_INTEGRAL(...) __PCZ_TYPEDEF_INTEGRAL_IMP(BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__))

__PCZ_TYPEDEF_INTEGRAL(dim_x, dim_u, dim_y)
PCZ_UNUSED(asd,asda,sadas)
    
BOOST_PP_VARIADIC_TO_SEQ(asdas,asdasdas,asdasdasda)
    
#define __PCZ_DIMS_FROM_MODELT(from_t, typen) \
enum { dim_x = from_t::dim_x, dim_u = from_t::dim_u, dim_y = from_t::dim_y }; \
typedef typen from_t::dim_x_t dim_x_t; \
typedef typen from_t::dim_y_t dim_y_t; \
typedef typen from_t::dim_u_t dim_u_t; 

#define __PCZ_TYPEDEFS_FROM_MODELT(from_t, typen) __PCZ_DIMS_FROM_MODELT(from_t, typen) \
typedef typen from_t::dtype dtype; \
typedef typen from_t::x_t x_t;

#define __PCZ_TYPEDEFS_FROM_CONTROLLER(from_t, typen) \
__PCZ_TYPEDEFS_FROM_MODELT(from_t, typen) \
typedef from_t::model_t model_t; \
typedef from_t::sys_t sys_t;
    
__PCZ_TYPEDEFS_FROM_CONTROLLER(parent_t, typename)
