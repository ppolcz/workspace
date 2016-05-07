/* 
 * File:   cpp_enum.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on October 23, 2014, 12:54 PM
 */

#include <iostream>
#include <boost/bind.hpp>
#include <boost/mpl/range_c.hpp>
#include <boost/mpl/for_each.hpp>
// #include <boost/mpl/map.hpp>

#include <boost/preprocessor.hpp>
#include <boost/preprocessor/variadic/size.hpp>
#include <boost/preprocessor/cat.hpp>
#include <boost/preprocessor/variadic.hpp>
#include <boost/preprocessor/seq/for_each.hpp>

#include <polcz/pczstring.h>
#include <polcz/pczdebug.h>

#define PCZ_VARIADIC_ISEMPTY(...) PCZ_VARIADIC_ISEMPTY_I(__VA_ARGS__, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
#define PCZ_VARIADIC_ISEMPTY_I(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19, e20, e21, e22, e23, e24, e25, e26, e27, e28, e29, e30, e31, e32, e33, e34, e35, e36, e37, e38, e39, e40, e41, e42, e43, e44, e45, e46, e47, e48, e49, e50, e51, e52, e53, e54, e55, e56, e57, e58, e59, e60, e61, e62, e63, size, ...) size

#define PCZ_PP_CALL(macro, ...) macro __VA_ARGS__

//PCZ_VARIADIC_ISEMPTY(,)
//PCZ_VARIADIC_ISEMPTY(1,)
//PCZ_VARIADIC_ISEMPTY(1, 2,)

/**
 * Rendkivul hasznos dolog: BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__)
 * ez azt jelenti, hogy vegig lehet iteralni az argumentumokon
 */

/**
 * PCZ_REM (enclosed) ANYTHING ==> enclosed ANYTHING
 * PCZ_EAT (enclosed) ANYTHING ==> ANYTHING
 */
#define PCZ_REM(...) __VA_ARGS__
#define PCZ_EAT(...)

/**
 * PCZ_ENUM_GET_NAME((EnumName)(4, "str")) ==> EnumName
 */
#define PCZ_ENUM_GET_NAME(x) PCZ_DETAIL_NAME ( PCZ_TO_TUPLE_ARG x )
#define PCZ_DETAIL_NAME(...) PCZ_DETAIL_NAME_HEAD(__VA_ARGS__)
#define PCZ_DETAIL_NAME_HEAD(a,...) PCZ_REM a
#define PCZ_TO_TUPLE_ARG(...) (__VA_ARGS__),

/**
 * PCZ_ENUM_GET_CONSTRUCTOR_P((EnumName)(4, "str")) ==> (4, "str")
 */
#define PCZ_ENUM_GET_CONSTRUCTOR_P(x) PCZ_EAT x

/**
 * PCZ_ENUM_GET_DEFINITION((EnumName)(4, "str")) ==> EnumName(4, "str")
 */
#define PCZ_ENUM_GET_DEFINITION(x) PCZ_REM x

// PCZ_ENUM_GET_NAME((EnumName)(enum, constructor))

#define PCZ_ENUM_DECLARATION(ClassName, ...) \
public: \
    const string name; \
    const int nr; \
    static const int enum_n = BOOST_PP_VARIADIC_SIZE(__VA_ARGS__); \
    template<int N, class Self> struct enum_data {}; \
    BOOST_PP_SEQ_FOR_EACH_I(PCZ_ENUM_DECLARE_EACH, ClassName, BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__)) \
    template <int N> struct get : public enum_data<N, ClassName> {}; 


#define PCZ_ENUM_DEFINITION(ClassName, ...) \
    BOOST_PP_SEQ_FOR_EACH_I(PCZ_ENUM_DEFINE_EACH, ClassName, BOOST_PP_VARIADIC_TO_SEQ(__VA_ARGS__))

#define PCZ_ENUM_DECLARE_EACH(r, ClassName, i, x) \
public: \
    static const ClassName PCZ_ENUM_GET_NAME(x); \
    template<class Self> \
    struct enum_data<i, Self> { static constexpr const Self* value = &PCZ_ENUM_GET_NAME(x); };

#define PCZ_ENUM_DEFINE_EACH(r, ClassName, i, x) \
    const ClassName ClassName:: PCZ_ENUM_GET_NAME(x) = ClassName( \
    BOOST_PP_STRINGIZE(PCZ_ENUM_GET_NAME(x)), i);

    
#define PCZ_ENUM_NAME_ARG const string& name
#define PCZ_ENUM_CONSTRUCTOR(enum_name, ...) enum_name (const string& name, int nr, ##__VA_ARGS__) : name(name), nr(nr)

#define MY_ENUM (MyEnum, \
    (FIRST)(barmit irhatok ide meg), \
    (SECOND)(asdasd), \
    (NONE)())

class MyEnum {
    PCZ_PP_CALL(PCZ_ENUM_DECLARATION, MY_ENUM);
    PCZ_ENUM_CONSTRUCTOR(MyEnum) {}
};

PCZ_PP_CALL(PCZ_ENUM_DEFINITION, MY_ENUM);

//struct reflector
//{
//    //Get enum_data at index N
//
//    template<int N, class T>
//    static typename T::template enum_data<N, T> get_enum_data (T& x)
//    {
//        return typename T::template enum_data<N, T>(x);
//    }
//
//    // Get the number of fields
//
//    template<class T>
//    struct fields
//    {
//        static const int n = T::fields_n;
//    };
//};
//
//struct field_visitor
//{
//    template<class C, class Visitor, class I>
//    void operator()(C& c, Visitor v, I)
//    {
//        v(reflector::get_enum_data<I::value>(c));
//    }
//};
//
//
//template<class C, class Visitor>
//void visit_each(C & c, Visitor v)
//{
//    typedef boost::mpl::range_c<int,0,reflector::fields<C>::n> range;
//    boost::mpl::for_each<range>(boost::bind<void>(field_visitor(), boost::ref(c), v, _1));
//}
//
//struct Person
//{
//    Person(const char *name, int age) : name(name), age(age) { }
//
//private:
//    PCZ_ENUM_DECLARATION
//    (
//        (const char *) name,
//        (int) age
//    )
//};
//
//struct print_visitor
//{
//    template<class FieldData>
//    void operator()(FieldData f)
//    {
//        std::cout << f.name() << "=" << f.get() << std::endl;
//    }
//};
//
//template<class T>
//void print_fields(T & x)
//{
//    visit_each(x, print_visitor());
//}

#define EColumnNames_ENUMS (\
    (TR_REMARK)("varchar(25)"),\
    (TR_ID)("integer autoincrement primary key unique"),\
    (TR_AMOUNT)("integer"),\
    (NONE)()\
)

class ETableNames
{
    template<int N, class Self>
    struct enum_getter{};
public:

    const string name;
    const int nr;

public:
    static const ETableNames TRANZACTIONS;
private:
    template<class Self>
    struct enum_getter<0, Self> { static constexpr const Self* value = &TRANZACTIONS; };

public:
    static const ETableNames COSTLIST;
private:
    template<class Self>
    struct enum_getter<1, Self> { static constexpr const Self* value = &COSTLIST; };

private:
    int id = 0;

    PCZ_ENUM_CONSTRUCTOR(ETableNames, int id), id(id) { }
    PCZ_ENUM_CONSTRUCTOR(ETableNames)
    { 
        std::cout << "Ez is mukodik am\n";
    }
        
public:

    string getSQLName() const
    {
        return name + "_Blah";
    }
    
    int getId() const
    {
        return id;
    }

    template <int N>
    struct get_enum : public enum_getter<N, ETableNames> {};
};

class EColumnNames
{
public:

    template <int N, class Self> struct enum_instance
    {
    };

    template <class Self> struct enum_instance<0, Self>
    {
        static constexpr const char* name = "TR_REMARK";
    };
    static const enum_instance<0, EColumnNames> TR_REMARK;
};

/**
 * Ez a megoldas tetszik a legjobban
 */
namespace EOtherColumnNames
{

/* Ami legeneralodik */

template <int N> struct enum_instance
{
};

template <> struct enum_instance<0>
{
    static constexpr const char* name = "TR_REMARK";
};
static const enum_instance<0> TR_REMARK;

/* Ami legeneralodik [VEGE] */
}

const EColumnNames::enum_instance<0, EColumnNames> EColumnNames::TR_REMARK = EColumnNames::enum_instance<0, EColumnNames>();

const ETableNames ETableNames::TRANZACTIONS = ETableNames("TRANZACTIONS", 0);
const ETableNames ETableNames::COSTLIST = ETableNames("COSTLIST", 1, 1);

struct MyOtherEnum
{
    struct TR_ID_t {
        const int a;
    }; 
    static constexpr const TR_ID_t TR_ID = {0};
};

namespace FunctionEnum {
/* PCZ_ENUM */
struct PEnum
{
    const string name;
    const int nr;
/* PCZ_ENUM_END */
    PCZ_ENUM_CONSTRUCTOR(PEnum) {}

/* PCZ_ENUM_INSTANCES */
};
static inline const PEnum* FIRST_ENUM()
{
    static const PEnum VALUE = PEnum("FIRST_ENUM", 0);
    return &VALUE;
}
/* PCZ_ENUM_INSTANCES_END */
}

#include "template_things.h"

void main_enum()
{
    PCZ_DEBUG(ETableNames::TRANZACTIONS.name) << std::endl;
    
    std::cout << ETableNames::TRANZACTIONS.name << std::endl;
    std::cout << ETableNames::TRANZACTIONS.getSQLName() << std::endl;
    std::cout << ETableNames::COSTLIST.name << std::endl;
    std::cout << ETableNames::COSTLIST.getSQLName() << std::endl;
    std::cout << EColumnNames::TR_REMARK.name << std::endl;
    std::cout << ETableNames::get_enum<0>::value->name << std::endl;

    {
        using namespace EOtherColumnNames;
        std::cout << TR_REMARK.name << std::endl;
    }
    
    std::cout << MyOtherEnum::TR_ID.a;
    
    PCZ_DEBUG("Ezek mar generalva vannak: ");
    PCZ_DEBUG(MyEnum::get<0>::value->name);
    PCZ_DEBUG(MyEnum::SECOND.name);
    
    PCZ_DEBUG("Fuggveny megkozelitesben: ");
    std::cout << FunctionEnum::FIRST_ENUM()->name << std::endl;
}