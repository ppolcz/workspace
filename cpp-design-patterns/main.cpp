/* 
 * File:   main.cpp
 * Author: deva
 *
 * Created on February 11, 2014, 5:30 PM
 */

#include <string>
#include <vector>
#include <iostream>
#include <cstdlib>

#include <boost/shared_ptr.hpp>

// vtable

#define DEBUG std::cout << __PRETTY_FUNCTION__ << std::endl;
#define DEBUG_ std::cout << __PRETTY_FUNCTION__ << " - "

class Empty {
};

class EmptyMember {

    void f () { }
};

class EmptyVirtual {

    virtual void f () { }
};

class Base1 {
    //public:
    //    Base1 () {
    //        DEBUG
    //    }

    virtual void f () { }
    //    long long a = 12;
};

class Base2 {
public:

    Base2 () {
        DEBUG
    }
    //    virtual void f () { }
};

class Derived : public Base1 {
public:

    Derived () {
        DEBUG //_ << this << std::endl;
    }
    //    virtual void f () { }
};

template <size_t n>
constexpr int getsize (int (& tmp)[n]) {
    return n;
}

// union

struct Rgba {

    union {

        struct {
            char r, g, b, a;
        };
        char rgba[4];
        int color;
    };
};

#include "pattern_free.h"
#include "shared_ptr_demo.h"
#include "patterns/adaptor_pattern.h"
#include "destruktor_test.h"
#include "static_things.h"
#include "diamond_hierarchy.h"
#include "tests/Poll_Test.h"
//#include "diamond_solution.h"

using namespace std;

int modify_k ();
int & init_i ();
int & G2 = init_i ();

struct Globals {

    static int & global () {
        static int _G = G2 + 10;
        return _G;
    }
};

// first: Globals::global()::_G --> G2 --> init_i() --> modify_k()::k =,++ 1 --> modify_i()::i = 1 + 100 --> Globals::global()::_G = 101 + 10
int & G = Globals::global ();

int & init_i () {
    static int i = modify_k() + 100;
    return i;
}

int modify_i () {
    // this will be initialized once!
    // but the first static "i" is another variable than this "i"
    static int i = 0;
    i++;
    return i;
}

int modify_k () {
    static int k = 0;
    k++;
    return k;
}

int poll_tests ();

void myexit () {
    std::cout << "The program has terminated\n";
}

/** BUILD THINGS
 * Release: g++ -O2 -s (strip debugging symbols) -DNDEBUG (disable assertions)

ifeq ($(BUILD),debug)   
# "Debug" build - no optimization, and debugging symbols
CFLAGS += -O0 -g
else
# "Release" build - optimization, and no debug symbols
CFLAGS += -O2 -s -DNDEBUG
endif

all: foo

debug:
    make "BUILD=debug"

foo: foo.o
# The rest of the makefile comes here...

 * 
 */

int poll_main ();

#include <algorithm>
#include <string>
using std::string;

#include <boost/algorithm/string.hpp> // include Boost, a C++ library


int main (int argc, char** argv) {
    
    {
        string target = "N5boost10shared_ptrIN3pcl10PointCloudINS1_11PointXYZRGBEEEEE";
        boost::replace_all(target,"N3pcl10PointCloudINS1_11PointXYZRGBEEE", "___");
        std::cout << target << std::endl;
    }
    
    {
        std::cout << typeid(std::shared_ptr<int>).name() << " -- "
                << typeid(std::shared_ptr<int>).hash_code() << std::endl;
        std::cout << typeid(boost::shared_ptr<double>).name() << " -- "
                << typeid(boost::shared_ptr<double>).hash_code() << std::endl;
        std::cout << typeid(boost::shared_ptr<int>).name() << " -- "
                << typeid(boost::shared_ptr<int>).hash_code() << std::endl;
        std::cout << typeid(std::shared_ptr<int>).name() << " -- "
                << typeid(std::shared_ptr<int>).hash_code() << std::endl;
        std::cout << typeid(int).name() << " -- "
                << typeid(int).hash_code() << std::endl;
        
    }
    
    return poll_tests();
    return poll_main();

    { // SizeOfs
        std::string tmp = "asdasasdasdasdasdasdasdasasdasdd";
        std::vector<int> vec = {231, 123, 12, 31, 23, 12, 3123, 123, 12, 31, 2, 312, 3, 12, 31, 2, 312, 3, 123, 12, 3, 123, 12, 312, 3, 123, 123, 12};

        std::cout << "Sizeof(Empty) = " << sizeof (Empty) << std::endl;
        std::cout << "Sizeof(EmptyMember) = " << sizeof (EmptyMember) << std::endl;
        std::cout << "Sizeof(EmptyVirtual) = " << sizeof (EmptyVirtual) << std::endl;
        std::cout << "Sizeof(std::cout) = " << sizeof (std::cout) << std::endl;
        std::cout << "Sizeof(std::string) = " << sizeof (std::string) << std::endl;
        std::cout << "Sizeof(tmp) = " << sizeof (tmp) << std::endl;
        std::cout << "Sizeof(\"\") = " << sizeof ("") << std::endl;
        std::cout << "Sizeof(\"123456789\") = " << sizeof ("123456789") << std::endl;
        std::cout << "Sizeof(\"1234567890123456789\") = " << sizeof ("1234567890123456789") << std::endl;
        std::cout << "Sizeof(int) = " << sizeof (int) << std::endl;
        std::cout << "Sizeof(vec) = " << sizeof (vec) << std::endl;
        std::cout << "Sizeof(std::vector<int>) = " << sizeof (std::vector<int>) << std::endl;
        std::cout << "Sizeof(std::vector<double>) = " << sizeof (std::vector<double>) << std::endl;
    }

    { // virtual tables

        {
            std::cout << "Derived * child = new Derived()\n";
            //            Base1 * child = ;
            new Derived();
            //            std::cout << "(Derived*) child = " << (Derived*) child << std::endl;
            //            std::cout << "(Base1*)   child = " << (Base1*) child << std::endl;
            //            std::cout << "(Base2*)   child = " << (Base2*) child << std::endl;
        }
        {
            //            std::cout << "Base2 * child = new Derived()\n";
            //            Base2 * child = new Derived();
            //            std::cout << "(Derived*) child = " << (Derived*) child << std::endl;
            //            std::cout << "(Base1*)   child = " << (Base1*) child << std::endl;
            //            std::cout << "(Base2*)   child = " << (Base2*) child << std::endl;
            //
            //            // delete child; // seg-fault
            //            delete (Derived*) child; // seg-fault
        }
    }

    { // union
        Rgba color;
        color.color = 0xFF55FF66;
        std::cout << (int) (unsigned char) color.r << " " << (int) (unsigned char) color.g << " " << (int) (unsigned char) color.b << " " << (int) (unsigned char) color.a << "\n";
    }

    { // globals
        std::cout << "MY_GLOBAL_INT = " << MY_GLOBAL_INT << std::endl;
        std::cout << "StaticThings::SP_StaticPrivate[1] = " << StaticThings::SP_IntegerArray[1] << std::endl;
        std::cout << "StaticThings::SP_String = " << StaticThings::SP_String << std::endl;
    }

    dia::test();

    /* FONTOS
void foo1 (void)
{
    int arr[100];
    std::generate(std::begin(arr), std::end(arr), [](){return std::rand()%100;});
}

void foo2 (void)
{
    int arr[100];
    for (int *i = arr; i < arr+100; i++) *i = std::rand()%100;
}
     */


    int a[20];

    std::cout << getsize(a) << std::endl;
    std::cout << sizeof (a) / sizeof (int) << std::endl;

    // When the program terminates the function myexit will be called.
    atexit(myexit);

    std::cout << "i = " << init_i() << std::endl;
    std::cout << "k = " << modify_k() << " // Here you can see, that k was initialized first! " << std::endl;

    std::cout << "i = " << modify_i() << std::endl;
    std::cout << "i = " << modify_i() << std::endl;
    std::cout << "i = " << modify_i() << std::endl;

    std::cout << "G = " << G << std::endl;
    G++;
    std::cout << "G = " << G << std::endl;
    G = 120;
    std::cout << "G = " << G << std::endl;

    {
        Childish valami;
    }

    {
        Parent * main = new Parent("main");
        Parent * sec = new Parent("sec", main);
        Parent * third = new Child("third", sec);
        Child * forth = new Child("forth", third);

        // delete third; // ez sem okoz gondot

        delete main;
    }

    SharedPtrDemo::test();

    // poll_tests();
    AdaptorPattern::main();

    //    diamond_solution();
    return 0;
}

