/* 
 * File:   polpedelop.h
 * Author: deva
 *
 * Created on May 14, 2014, 1:10 PM
 */

#ifndef POLPEDELOP_H
#define	POLPEDELOP_H

#ifdef SEGFAULT_DEBUG_DELETE_OPERATOR
#define SEGFAULT_DEBUG_DELETE_OPERATOR
#warning THE PROJECT WAS COMPILED IN SEGMENTATION FAULT DEBUGGING MODE
#define POLPE_DELETE_OP void operator delete(void * maddress) { POLPESEG(maddress); }
#else
#define POLPE_DELETE_OP
#endif


#endif	/* POLPEDELOP_H */

