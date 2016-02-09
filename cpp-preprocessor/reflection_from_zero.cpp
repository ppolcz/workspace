#include <boost/preprocessor.hpp>

#define __REFLECT(...) BOOST_PP_VARIADIC_SIZE(__VA_ARGS__)
#define REFLECT(...) __REFLECT(__VA_ARGS__)

//REFLECT(
//    (const char*) name,
//    (int) id
//)
//    
//n = BOOST_PP_VARIADIC_SIZE(1, alma, korte)