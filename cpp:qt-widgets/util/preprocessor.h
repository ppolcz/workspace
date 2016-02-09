/* 
 * File:   preprocessor.h
 * Author: polpe
 *
 * Created on February 15, 2014, 8:04 PM
 */

#ifndef PREPROCESSOR_H
#define	PREPROCESSOR_H

#define CAT(a, ...) PRIMITIVE_CAT(a, __VA_ARGS__)
#define PRIMITIVE_CAT(a, ...) a ## __VA_ARGS__

#define ARGS_TO_VALUES(argc, args...) PRIMITIVE_CAT(ARGS_TO_VALUES_,argc)(args)
#define ARGS_TO_VALUES_0(...)
#define ARGS_TO_VALUES_1(...) value1
#define ARGS_TO_VALUES_2(...) value1, value2
#define ARGS_TO_VALUES_3(...) value1, value2, value3

#define ARGS_TO_ARGVAL(argc, args...) PRIMITIVE_CAT(ARGS_TO_ARGVAL_,argc)(args)
#define ARGS_TO_ARGVAL_0()
#define ARGS_TO_ARGVAL_1(arg1) arg1 value1
#define ARGS_TO_ARGVAL_2(arg1, arg2) arg1 value1, arg2 value2
#define ARGS_TO_ARGVAL_3(arg1, arg2, arg3) arg1 value1, arg2 value2, arg3 value3

#endif	/* PREPROCESSOR_H */

