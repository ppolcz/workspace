/* 
 * File:   pczutil.h
 * Author: Polcz Peter <ppolcz@gmail.com>
 *
 * Created on September 8, 2014, 5:35 PM
 */

#ifndef __PCZUTIL_H
#define __PCZUTIL_H

//template <typename... _ArgTypes>
//inline pcz_Unused (_ArgTypes const& ...) { }

#include <boost/preprocessor.hpp>
#include <boost/preprocessor/control/if.hpp>
#include <boost/preprocessor/cat.hpp>
#include <boost/preprocessor/seq/for_each.hpp>
#include <boost/preprocessor/variadic/to_seq.hpp>

#define __PCZ_UNUSED(r, data, elem) (void) elem;
#define PCZ_UNUSED_IMP(seq) BOOST_PP_SEQ_FOR_EACH(__PCZ_UNUSED,, seq)
#define PCZ_UNUSED(...) PCZ_UNUSED_IMP(BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__))


#define __PCZ_DELETE(r, data, elem) delete elem;
#define PCZ_DELETE(...) BOOST_PP_SEQ_FOR_EACH(__PCZ_DELETE,, PCZ_UNUSED_IMP(BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__)))


#define PCZ_NONAME __NEVER_USE_THIS_VARIABLE_AGAIN_PLEASE__

#ifndef PCZU
#define PCZU __attribute__((unused))
#endif

#define PCZ_DEPRECATED __attribute__((deprecated))

#endif /* __PCZUTIL_H */