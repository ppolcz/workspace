/* 
 * File:   polczexcept.cpp
 * Author: polpe
 * 
 * Created on March 25, 2014, 4:07 PM
 */

#include "../pczexcept.h"

const char* ReadError::what () const throw ()
{
    return "Problem reading BIO.";
}

const char* NoKeypairLoaded::what () const throw ()
{
    return "No keypair loaded.";
}

PException::PException (const std::string& msg, const std::string& stackTrace) : m_msg (msg), m_stackTrace (stackTrace) { }

void PException::p_setStackTrace (std::string stackTrace)
{
    m_stackTrace = stackTrace;
}

void PException::p_setMsg (std::string msg)
{
    m_msg = msg;
}

const char * PException::what () const throw ()
{
    return ((std::string) "\nMessage: " + m_msg + "\nStackTrace:\n" + m_stackTrace + "\n").c_str();
}

void PException::addStackEntry (const std::string& entry) throw ()
{
    m_stackTrace += "\n" + entry;
}

void PException::printStackTrace (const std::string& entry) throw ()
{
    addStackEntry(entry);
    std::cout << "\n <> PException catched!" << what();
}

PException& PException::F (PException&& rv, const std::string& msg, const std::string& stackTrace)
{
    rv.p_setMsg(msg);
    rv.p_setStackTrace(stackTrace);
    return rv;
}

void __passert_fail_throw (
        const std::string & assertion,
        const boost::format & comment,
        const std::string & file,
        unsigned int line,
        const std::string & fun)
{
    throw PczAssertionError((std::string) "`" + assertion + "' - " + comment.str(), PCZ_DEBUG_INFO_(file, line, fun));
}

void __passert_fail_throw (
        const std::string & assertion,
        const std::string & comment,
        const std::string & file,
        unsigned int line,
        const std::string & fun)
{
    throw PczAssertionError((std::string) "`" + assertion + "' - " + comment, PCZ_DEBUG_INFO_(file, line, fun));
}

void __pwarning_fail (
        const std::string & assertion,
        const boost::format & comment,
        const std::string & file,
        unsigned int line,
        const std::string & fun)
{
    std::cout << "[WARNING] " << PCZ_DEBUG_INFO_(file, line, fun) << "\n\t"
            "Assertion failed: " << assertion << "\n\t"
            "Comment: " << comment;
}

void __pwarning_fail (
        const std::string & assertion,
        const std::string & comment,
        const std::string & file,
        unsigned int line,
        const std::string & fun)
{
    std::cout << "[WARNING] " << PCZ_DEBUG_INFO_(file, line, fun) << "\n\t"
            "Assertion failed: " << assertion << "\n\t"
            "Comment: " << comment;
}

