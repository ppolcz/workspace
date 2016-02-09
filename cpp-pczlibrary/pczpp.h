/* 
 * File:   pczpp.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 24, 2014, 6:09 PM
 */

#ifndef PCZPP_H
#define	PCZPP_H

#include <boost/preprocessor.hpp>

/**
 * PCZ_PP_REM (enclosed) ANYTHING ==> enclosed ANYTHING
 * PCZ_PP_EAT (enclosed) ANYTHING ==> ANYTHING
 */
#define PCZ_PP_DOUBLE(...) ((__VA_ARGS__))
#define PCZ_PP_REM(...) __VA_ARGS__
#define PCZ_PP_EAT(...)

/**
 * PCZ_PP_GET_FIRST((TR_ID, "integer", 12)) ==> TR_ID
 */
#define PCZ_PP_GET_FIRST(x) BOOST_PP_VARIADIC_ELEM_0 x

/**
 * PCZ_PP_GET_BUT_FIRST((TR_ID, "integer", 12)) ==> , "integer", 12
 * PCZ_PP_GET_BUT_FIRST((TR_ID)) ==> 
 */
#define PCZ_PP_GET_BUT_FIRST_I(first,...) ,##__VA_ARGS__
#define PCZ_PP_GET_BUT_FIRST(x) PCZ_PP_GET_BUT_FIRST_I x

/**
 * 
 */
#define PCZ_PP_GET0(x) BOOST_PP_VARIADIC_ELEM_0 x
#define PCZ_PP_GET0_I(a0, ...) __VA_ARGS__
#define PCZ_PP_GET0_(x) PCZ_PP_GET1_I x

/**
 * 
 */
#define PCZ_PP_GET1(x) BOOST_PP_VARIADIC_ELEM_1 x
#define PCZ_PP_GET1_I(a0,a1, ...) __VA_ARGS__
#define PCZ_PP_GET1_(x) PCZ_PP_GET1_I x
#define PCZ_PP_GET1__I(a0,a1, ...) ,##__VA_ARGS__
#define PCZ_PP_GET1__(x) PCZ_PP_GET1__I x

/**
 * 
 */
#define PCZ_PP_GET2(x) BOOST_PP_VARIADIC_ELEM_2 x
#define PCZ_PP_GET2_I(a0,a1,a2, ...) __VA_ARGS__
#define PCZ_PP_GET2_(x) PCZ_PP_GET2_I x
#define PCZ_PP_GET2__I(a0,a1,a2, ...) ,##__VA_ARGS__
#define PCZ_PP_GET2__(x) PCZ_PP_GET2__I x

/**
 * PCZ_PP_SEQ_APPEND("EnumName", 0 PCZ_PP_GET_BUT_FIRST((TR_ID, "integer", 12))) ==> "EnumName", 0, "integer", 12
 * PCZ_PP_SEQ_APPEND("EnumName", 0 PCZ_PP_GET_BUT_FIRST((TR_ID))) ==> "EnumName", 0
 */
#define PCZ_PP_SEQ_APPEND(a,...) a, ##__VA_ARGS__


#endif	/* PCZPP_H */

