/* 
 * File:   template_things.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 23, 2014, 5:17 PM
 */

#ifndef TEMPLATE_THINGS_H
#define	TEMPLATE_THINGS_H

#include <polcz/pczstring.h>

namespace Test_If_It_Is_Not_Multiply_Defined {
struct PEnum
{
    string name;
    int nr;
    PEnum (const string& name, int nr) : name(name), nr(nr) {}
};
static const PEnum* FIRST_ENUM()
{
    static const PEnum VALUE = PEnum("FIRST_ENUM", 0);
    return &VALUE;
}
}

#endif	/* TEMPLATE_THINGS_H */

