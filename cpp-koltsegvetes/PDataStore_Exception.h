/* 
 * File:   PDataStore_Exception.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on September 1, 2014, 12:07 PM
 */

#ifndef PDATASTORE_EXCEPTION_H
#define	PDATASTORE_EXCEPTION_H

#include <polcz/pczexcept.h>
P_DECLARE_EXCEPTION(PDBException, PException);
P_DECLARE_EXCEPTION(PDBIsNotOpenException, PDBException);
P_DECLARE_EXCEPTION(PDBIsOpenException, PDBException);
P_DECLARE_EXCEPTION(PTableExists, PDBException);
P_DECLARE_EXCEPTION(PTableDoesntExists, PDBException);
P_DECLARE_EXCEPTION(PSqlCommandError, PDBException);
P_DECLARE_EXCEPTION(PModelError, PDBException);

#endif	/* PDATASTORE_EXCEPTION_H */

