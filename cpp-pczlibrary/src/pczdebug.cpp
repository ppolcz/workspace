#include "../pczdebug.h"

void __ptassert_ok (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function) {
    std::cout << "[TEST_PASSED]  assert(" << __assertion << ")  In " << __file << ":" << __line << ", in " << __function << std::endl;
}

void __ptassert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function) {
    std::cout << "[TEST_FAILED]  assert(" << __assertion << ")  In " << __file << ":" << __line << ", in " << __function << std::endl;
    __assert_fail("test failed - see above", __file, __line, __function);
}

void __passert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function,
        const boost::format & __format) __THROW {
    std::cout << "\nPolczAssertion: \n" << __format << std::endl;
    __assert_fail(__assertion, __file, __line, __function);
}

void __passert_fail (
        const char *__assertion,
        const char *__file,
        unsigned int __line,
        const char *__function,
        const std::string & __format) __THROW {
    std::cout << "\nPolczAssertion: " << __format << std::endl;
    __assert_fail(__assertion, __file, __line, __function);
}
