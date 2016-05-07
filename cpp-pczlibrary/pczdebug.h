/* 
 * File:   pczdebug.h
 * Author: Polcz Peter <ppolcz@gmail.com>
 *
 * Created on June 2, 2014, 11:00 PM
 */

#ifndef __PCZDEBUG_H
#define	__PCZDEBUG_H

#include <iostream>
#include <string>
#include <cstdlib>
#include <ctime>

#include <boost/format.hpp>

//#warning TODO ahhoz, hogy a PCZ_DEBUG(format, args...) jol mukodjon felul kellene definialni a boost::format , operatorat es \
lehetoleg ne kelljen ehhez modositani a boost konyvtar headerjeit

template <typename _Type>
boost::format operator, (boost::format format, _Type const& arg)
{
//    std::cout << "OPERATOR,\n";
    return (format % arg);
}

#ifndef __PCZ_NO_COUT_DEBUG
#  define PCZ_DEBUG(f, args...)    std::cout << "In file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << " - "  << (boost::format(f) , ##args).str() << std::endl
#  define PCZ_DEBUGC(c, f, args...)    std::cout << (boost::format(f) , ##args).str() << " " << c << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << std::endl
#  define PCZ_DEBUGG(f, args...)    std::cout << "\033[0;30m" << (boost::format(f) , ##args).str() << " \t\033[0;32m%" << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << "\033[0m\n"
#  define PCZ_DEBUGR(f, args...)    std::cout << "\033[0;30m" << (boost::format(f) , ##args).str() << " \t\033[0;31m#" << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << "\033[0m\n"
#  define PCZ_DEBUGB(f, args...)    std::cout << "\033[0;30m" << (boost::format(f) , ##args).str() << " \t\033[0;34m#" << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << "\033[0m\n"
#  define PCZ_DEBUGW(f, args...)    std::cout << "\033[0;30m" << (boost::format(f) , ##args).str() << " \t\033[0;37m//" << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << "\033[0m\n"
#  define PCZ_DEBUGT(f, args...)   std::cout << "TIME = " << time(0) << " In file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << " - "  << (boost::format(f) , ##args).str() << std::endl
#  define PCZ_DEBUG_W(a)           std::cout  << "In file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << " - " << #a << " = " << a << std::endl
#  define PCZ_DEBUGC_W(c,a)           std::cout  << #a << " = " << a << " " << c << " in file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << std::endl
#  define PCZ_DEBUGT_W(a)           std::cout  << "TIME = " << time(0) << " In file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << " - " << #a << " = " << a << std::endl
#  define PCZ_DEBUG_               std::cout << "In file " << __FILE__ << ":" << __LINE__ << " in " << __FUNCTION__ << " - "
#  define PCZ_TITLE(f, args...)    std::cout << "\n---------------------------------------------------\n"; PCZ_DEBUGW(f, ##args)
#  define PCZ_DEBUG_ARRAY(array, from, to) PCZ_DEBUG_ << #array << " = "; for (int i = from; i < (int) to; ++i) { std::cout << array[i] << " "; } std::cout << std::endl;
#else
#  define PCZ_DEBUG(f, args...)
#  define PCZ_DEBUGC(f, args...)
#  define PCZ_DEBUGR(f, args...)
#  define PCZ_DEBUGG(f, args...)
#  define PCZ_DEBUGB(f, args...)
#  define PCZ_DEBUGW(f, args...)
#  define PCZ_DEBUGT(f, args...)
#  define PCZ_DEBUG_W(a)
#  define PCZ_DEBUGC_W(a...)
#  define PCZ_DEBUGT_W(a)
#  define PCZ_DEBUG_               std::stringstream()
#  define PCZ_DEBUG_ARRAY(array, from, to)
#  define PCZ_TITLE(f, args...) 
#endif

#define PCZ_WATCH(watchee)       ((std::stringstream*) (&(std::stringstream() << " (field=" << #watchee << ", value=" << watchee << ") ")))->str()

#define PCZ_DINFO_ARG                    std::string const& dinfo
#define PCZ_DINFO_ARG_                   PCZ_DINFO_ARG = ""
#define PCZ_ADD_DINFO                    dinfo + PCZ_DEBUG_INFO
#define PCZ_DEBUG_INFO                   std::string(" - in file: ") + std::string(__FILE__) + std::string(":") + std::to_string(__LINE__) + std::string(" - ") + std::string(__PRETTY_FUNCTION__) + "\n"
#define PCZ_DEBUG_INFO_(file, line, fun) std::string(" - in file: ") + file + std::string(":") + std::to_string(line) + std::string(" - ") + fun

#define PCZ_ERROR_MSG_STR(msg)      (std::string(msg) + PCZ_DEBUG_INFO)
#define PCZ_ERROR_MSG_C_STR(msg)    (std::string(msg) + PCZ_DEBUG_INFO).c_str()
#define PCZ_ERROR_MSG_CODE(msg)     (std::string(#msg) + PCZ_DEBUG_INFO)
#define PCZ_ERROR_MSG_C_CODE(msg)   (std::string(#msg) + PCZ_DEBUG_INFO).c_str()

#define PCZ_STRINGIZE(arg) std::string(#arg)


#ifdef PCZ_SEGFAULT_DEBUG
#define PCZ_SEGFAULT_DEBUG
#define PCZ_SEG(watchee) PCZ_DEBUG_W(watchee)
#else
#define PCZ_SEG(watchee) 
#endif

// #define PCZ_DEBUG std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "\n";
// #define DEBUG_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "  --  "
// #define DEBUGP std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __PRETTY_FUNCTION__ << "\n";
// #define DEBUGP_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __PRETTY_FUNCTION__ << "  --  "
#define PCZ_WARNING(ex) std::cout << "WARNING in file: " << __FILE__ << ":" << __LINE__ << " - " << __PRETTY_FUNCTION__ << "  --  " << #ex
#define PCZ_WARNING_IF(condition, ex) if ( condition ) PCZ_WARNING(ex : condition) << std::endl;
#define PCZ_WARNING_IF_HINT(condition, ex, hint) if ( condition ) PCZ_WARNING(ex : condition) << " - " << #hint << std::endl;
#define PCZ_FILE_NOT_EXISTS(filename) "File " + filename + " not exists"
#define PCZ_PRINT_POINTER(ptr) DEBUG_ << ptr << std::endl
// #define WATCH(watchee) DEBUG_ << #watchee << " = " << watchee << std::endl
// #define WATCHQ(watchee) DEBUG_ << #watchee << " = " << QUOTE(watchee) << "\n";
// #define WATCHQ_(watchee) #watchee << " = " << QUOTE(watchee)

void __ptassert_ok (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function);

void __ptassert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function);

void __passert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function,
        const boost::format & __format) __THROW;

void __passert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function,
        const std::string & __format) __THROW;

template <typename _Type>
void __ptassertEqual_fail (
        const _Type & __var1,
        const _Type & __var2,
        const char *__var1_name,
        const char *__var2_name,
        const char *__file,
        unsigned int __line,
        const char *__function) {
    std::cout << "[TEST_FAILED]  assert(" << __var1_name << " == " << __var2_name << ")  "
            "In " << __file << ":" << __line << ", in " << __function << "\n"
            "\t > " << __var1_name << " = " << QUOTE(__var1) << "\n"
            "\t > " << __var2_name << " = " << QUOTE(__var2) << std::endl;
    __assert_fail("test failed - see above", __file, __line, __function);
}

#define pcz_assertEqual(var1, var2) \
    ((var1 == var2) ? \
    __ASSERT_VOID_CAST (0) : \
    __ptassertEqual_fail(var1, var2, #var1, #var2, __FILE__, __LINE__, __ASSERT_FUNCTION))

#define pcz_assert(expr, dbg) \
    ((expr) ? __ASSERT_VOID_CAST (0) : __passert_fail (__STRING(expr), __FILE__, __LINE__, __ASSERT_FUNCTION, dbg))

#define pcz_assertTest(expr) \
    ((expr) ? \
    __ptassert_ok (__STRING(expr), __FILE__, __LINE__, __ASSERT_FUNCTION) : \
    __ptassert_fail (__STRING(expr), __FILE__, __LINE__, __ASSERT_FUNCTION))

#define pcz_assertTestEqual(var1,var2) \
    ((var1 == var2) ? \
    __ptassert_ok (__STRING(var1 == var2), __FILE__, __LINE__, __ASSERT_FUNCTION) : \
    __ptassertEqual_fail(var1, var2, #var1, #var2, __FILE__, __LINE__, __ASSERT_FUNCTION))


#endif	/* __PCZDEBUG_H */

