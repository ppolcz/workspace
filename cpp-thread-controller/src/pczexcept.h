/* 
 * File:   pczexcept.h
 * Author: Polcz Péter
 *
 * Created on May 16, 2014, 11:26 AM
 */

#ifndef PCZEXCEPT_H
#define	PCZEXCEPT_H

#include <exception>
#include <string>

class PInterruptedException : public std::exception
{
    std::string msg;
public:
    PInterruptedException(std::string const & _msg = "");
    const char * what();
};

#endif	/* PCZEXCEPT_H */

