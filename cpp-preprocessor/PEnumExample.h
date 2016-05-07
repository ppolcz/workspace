/* 
 * File:   PEnumExample.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 23, 2014, 11:08 PM
 */

#ifndef PENUMEXAMPLE_H
#define	PENUMEXAMPLE_H

#include <iostream>
#include <polcz/pczstring.h>
#include "pczenum.h"

namespace PEnumExample
{
P_FENUM{}

P_FENUM_CONSTRUCTOR(const string& sqltype), sqltype(sqltype) { }

const string sqltype = "[NO_TYPE]";

private:
void kutyagumi();

public:
void ize_bize();

P_FENUM_VALUES(
    (TR_ID, "integer"),
    (TR_DATE, "varchar(50)"),
    (NONE))
}

#endif	/* PENUMEXAMPLE_H */

