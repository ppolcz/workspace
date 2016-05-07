/* 
 * File:   static_things.h
 * Author: polpe
 *
 * Created on March 31, 2014, 10:29 PM
 */

#ifndef STATIC_THINGS_H
#define	STATIC_THINGS_H

#include <string>

class StaticThings {
public:
    StaticThings ();
    StaticThings (const StaticThings& orig);
    virtual ~StaticThings ();

public:
    static constexpr int SP_IntegerArray[] = {
        12, 24, 12, 123
    };

    static const std::string SP_String;
};

#endif	/* STATIC_THINGS_H */

