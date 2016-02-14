/* 
 * File:   pczexcept.h
 * Author: polpe
 *
 * Created on March 25, 2014, 4:07 PM
 */

#ifndef POLCZEXCEPT_H
#define	POLCZEXCEPT_H

#include <stdexcept>
#include <string>
#include "pczdebug.h"

using std::string;

#define P_THROW_IF(test, extype) if (test) throw extype((string)#test + " failed", PCZ_DEBUG_INFO)
#define P_THROW_IF_MSG(test, extype, msg) if (test) throw extype((string)#test + " failed; \nExtra message: " + msg, PCZ_DEBUG_INFO)

#define P_DECLARE_EXCEPTION(exname, parentname) \
class exname : public parentname { \
public: \
    exname (string const & msg = "", string const & stackTrace = "") : parentname (msg, stackTrace) { } \
}

class ReadError : public std::exception {
public:
    virtual const char* what () const throw ();
};

class NoKeypairLoaded : public std::exception {
public:
    virtual const char* what () const throw ();
};

#define addStackEntry_() addStackEntry(PCZ_DEBUG_INFO)
#define printStackTrace_() printStackTrace(PCZ_DEBUG_INFO)

class PException : public std::exception {
private:
    string m_msg;
    string m_stackTrace;

    void p_setStackTrace (string stackTrace);
    void p_setMsg (string msg);
public:
    PException (string const & msg = "", string const & stackTrace = "");
    virtual const char * what () const throw ();
    virtual void addStackEntry (string const & entry) throw ();
    virtual void printStackTrace (string const & entry) throw ();

    // not a bad idea, but not the best either
    static PException & F (PException && rv, string const & msg = "", string const & stackTrace = "");
};

P_DECLARE_EXCEPTION (IOException, PException);
P_DECLARE_EXCEPTION (PNotImplementedException, PException);
P_DECLARE_EXCEPTION (FileException, IOException);
P_DECLARE_EXCEPTION (FileNotFoundException, FileException);
P_DECLARE_EXCEPTION (IllegalFileFormatException, FileException);
P_DECLARE_EXCEPTION (IllegalFileExtException, FileException);
P_DECLARE_EXCEPTION (FileNotReadableException, FileException);
P_DECLARE_EXCEPTION (FileNotGoodException, FileException);
P_DECLARE_EXCEPTION (XmlException, FileException);
P_DECLARE_EXCEPTION (XmlHasErrorException, XmlException);
P_DECLARE_EXCEPTION (XmlFormatException, XmlException);
P_DECLARE_EXCEPTION (CryptographyException, PException);
P_DECLARE_EXCEPTION (CipherException, CryptographyException);
P_DECLARE_EXCEPTION (RSAException, CipherException);
P_DECLARE_EXCEPTION (AESException, CipherException);
P_DECLARE_EXCEPTION (RSADecryptionFailedException, RSAException);
P_DECLARE_EXCEPTION (RSAEncryptionFailedException, RSAException);
P_DECLARE_EXCEPTION (AESDecryptionFailedException, AESException);
P_DECLARE_EXCEPTION (AESEncryptionFailedException, AESException);

P_DECLARE_EXCEPTION (AlgorithmWorkflowException, PException);
P_DECLARE_EXCEPTION (ReconstructionException, AlgorithmWorkflowException);

P_DECLARE_EXCEPTION (PczAssertionError, PException);

void __passert_fail_throw (
        const string & assertion,
        const string & comment,
        const string & file,
        unsigned int line,
        const string & fun);

void __passert_fail_throw (
        const string & assertion,
        const boost::format & comment,
        const string & file,
        unsigned int line,
        const string & fun);

void __pwarning_fail (
        const string & assertion,
        const string & comment,
        const string & file,
        unsigned int line,
        const string & fun);

void __pwarning_fail (
        const string & assertion,
        const boost::format & comment,
        const string & file,
        unsigned int line,
        const string & fun);

#define pczassert(cond, comment) \
    ((cond) ? \
    __ASSERT_VOID_CAST (0) : \
    __passert_fail_throw(#cond, comment, __FILE__, __LINE__, __ASSERT_FUNCTION))

#define pczwarning(cond, comment) \
    ((cond) ? \
    __ASSERT_VOID_CAST (0) : \
    __pwarning_fail(#cond, comment, __FILE__, __LINE__, __ASSERT_FUNCTION))

#endif	/* POLCZEXCEPT_H */

