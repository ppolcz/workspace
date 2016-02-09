/* 
 * File:   pczprotocol.h
 * Author: Polcz Péter <ppolcz@gmail.com>
 *
 * Created on July 30, 2014, 3:05 PM
 */

#ifndef PCZPROTOCOL_H
#define	PCZPROTOCOL_H

#include <string>
#include "pczdebug.h"
#include "pczexcept.h"

#define CHECK_FILE_PROTOCOL_DECLARATION(s, name) { \
    std::string n = #name, dummy; \
    getline(s, dummy); \
    if (dummy.length() < n.length() || dummy.substr(0, n.length()) != n) \
    throw FileException("texture gl parameters file in wrong format.\n" \
            "Expected: |" + n + "|, got: |" + dummy + "|", DEBUG_INFO); }

#endif	/* PCZPROTOCOL_H */

