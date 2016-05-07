/* 
 * File:   POldRecord.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 1, 2014, 11:21 PM
 */

#include <polcz/pczdebug.h>

#include "strtk.hpp"
#include "POldRecord.h"

const string POldRecord::NO_VAL = "__EMPTY__";
const string POldRecord::DELIMITER = "|";
const QString POldRecord::DATE_FORMAT = "yyyy-MM-dd";

const QString POldRecord::SZAMLA_OTP = "otp";
const QString POldRecord::SZAMLA_KEZ = "készpénz";

bool POldRecord::loadFromString (const string& line)
{
    std::vector<string> tokens;
    strtk::parse(line, DELIMITER, tokens);

    /* If there is no date specified */
    if (tokens[0] == NO_VAL) return false;
    
    m_date = QDate::fromString(tokens[0].c_str(), DATE_FORMAT);
    m_alberletHonnan = tokens[9].c_str();
    m_mastolKicsoda = tokens[11].c_str();
    m_comment = tokens[12] == NO_VAL ? "" : tokens[12].c_str();

    #define __PARSE_INTEGER(var, index) \
    try { \
        var = (tokens[index] == NO_VAL ? 0 : std::stoi(tokens[index])); } \
    catch (std::invalid_argument& e) { \
        var = 0; \
        if (tokens[index] != "-" && tokens[index] != "???" && index != 10) \
            std::cout << e.what() << "\n" << line << "\n" << tokens[index] << "\n"; \
        if (index == 10) \
            m_comment += QString(" + Alberlet: ") + QString(tokens[10].c_str());  }
    /* Parse integer values */
    __PARSE_INTEGER(m_potpEgyenleg, 1);
    __PARSE_INTEGER(m_potpBevetel, 2);
    __PARSE_INTEGER(m_potpKivetel, 3);
    __PARSE_INTEGER(m_potpKiadas, 4);
    __PARSE_INTEGER(m_pkezEgyenleg, 5);
    __PARSE_INTEGER(m_pkezBevetel, 6);
    __PARSE_INTEGER(m_pkezKiadas, 7);
    __PARSE_INTEGER(m_alberletKoltseg, 8);
    __PARSE_INTEGER(m_mastolOsszeg, 10);
    __PARSE_INTEGER(m_htOtp, 13);
    __PARSE_INTEGER(m_htKez, 14);
    #undef __PARSE_INTEGER
    
//    DEBUG_W(m_potpEgyenleg);
//    DEBUG_W(m_potpBevetel);
//    DEBUG_W(m_potpKivetel);
//    DEBUG_W(m_potpKiadas);
//    DEBUG_W(m_pkezEgyenleg);
//    DEBUG_W(m_pkezBevetel);
//    DEBUG_W(m_pkezKiadas);
//    DEBUG_W(m_mastolOsszeg);
//    DEBUG_W(m_htOtp);
//    DEBUG_W(m_htKez);
//    DEBUG_W(m_comment.toStdString());
    return true;
}
