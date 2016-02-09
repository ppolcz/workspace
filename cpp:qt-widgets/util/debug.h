/* 
 * File:   debug.h
 * Author: polpe
 *
 * Created on February 23, 2014, 3:42 PM
 */

#ifndef DEBUG_H
#define	DEBUG_H

#include <iostream>
#define DEBUG_INFO std::string(" - in file: ") + std::string(__FILE__) + std::string(":") + std::to_string(__LINE__) + std::string(" - ") + std::string(__FUNCTION__) + std::string("()")
#define DEBUG std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "()\n";
#define DEBUG_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "  --  "
#define WARNING(ex) std::cout << "WARNING in file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "()  --  " << #ex
#define WARNING_IF(condition, ex) if ( condition ) WARNING(ex : condition) << std::endl;
#define WARNING_IF_HINT(condition, ex, hint) if ( condition ) WARNING(ex : condition) << " - " << #hint << std::endl;
#define FILE_NOT_EXISTS(filename) "File " + filename + " not exists"
#define PRINT_POINTER(ptr) DEBUG_ << ptr << std::endl
#define WATCH(watchee) DEBUG_ << #watchee << " = " << watchee << std::endl
#define WATCH_(watchee) #watchee << " = " << watchee
#define DEBUG_ARRAY(array, from, to) DEBUG_ << #array << " = "; for (int i = from; i < to; ++i) { std::cout << array[i] << " "; } std::cout << std::endl;

#endif	/* DEBUG_H */

