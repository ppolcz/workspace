/* 
 * File:   generated_meta_object_code.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on October 22, 2014, 10:02 PM
 */


template<class M, class T>
struct make_const
{
    typedef T type;
};

template<class M, class T>
struct make_const<const M, T>
{
    typedef typename std::add_const<T>::type type;
};

struct reflector
{

    template<int N, class T>
    static typename T::template field_data<N, T> get_field_data(T& x)
    {
        return typename T::template field_data<N, T>(x);
    }

    template<class T>
    struct fields
    {
        static const int n = T::fields_n;
    };
};

struct field_visitor
{

    template<class C, class Visitor, class I>
    void operator()(C& c, Visitor v, I)
    {
        v(reflector::get_field_data<I::value>(c));
    }
};

struct Person
{

    Person(const char *name, int age) : name(name), age(age) { }

private:
    static const int fields_n = 2;
    friend struct reflector;

    template<int N, class Self> struct field_data
    {
    };

    const char * name;

    template<class Self>
    struct field_data<0, Self>
    {
        Self & self;

        field_data(Self & self) : self(self) { }

        typename make_const<Self, const char *>::type & get()
        {
            return self.name;
        }

        typename boost::add_const<const char *>::type & get() const
        {
            return self.name;
        }

        const char * name() const
        {
            return "name";
        }
    };

    int age;

    template<class Self>
    struct field_data<1, Self>
    {
        Self & self;

        field_data(Self & self) : self(self) { }

        typename make_const<Self, int>::type & get()
        {
            return self.age;
        }

        typename boost::add_const<int>::type & get() const
        {
            return self.age;
        }

        const char * name() const
        {
            return "age";
        }
    };
};

struct print_visitor
{

    template<class FieldData>
    void operator()(FieldData f)
    {
        std::cout << f.name() << "=" << f.get() << std::endl;
    }
};

template<class C, class Visitor>
static void visit_each(C & c, Visitor v)
{
    typedef boost::mpl::range_c<int, 0, reflector::fields<C>::n> range;
    boost::mpl::for_each<range>(boost::bind<void>(field_visitor(), boost::ref(c), v, _1));
}

template<class T>
static void print_fields(T & x)
{
    visit_each(x, print_visitor());
}

static int main_NE_FUSS()
{
    Person p("Tom", 82);
    print_fields(p);
    return 0;
}

class MyEnum
{
private:
    static const int enum_n = 3;

    template<int N> struct enum_data
    {
    };
public:
    static const MyEnum FIRST;
private:

    template<class Self> struct enum_data<0, Self>
    {
        static constexpr const Self* value = &FIRST;
    };
public:
    static const MyEnum SECOND;
private:

    template<class Self> struct enum_data<1, Self>
    {
        static constexpr const Self* value = &SECOND;
    };
public:
    static const MyEnum NONE;
private:

    template<class Self> struct enum_data<2, Self>
    {
        static constexpr const Self* value = &NONE;
    };

    template <int N> struct get : public enum_getter<N, ETableNames>
    {
    };
};

class MyEnum
{
public:
    const string name;
    const int nr;
    static const int enum_n = 3;

    template<int N, class Self> struct enum_data
    {
    };
public:
    static const MyEnum FIRST;

    template<class Self> struct enum_data<0, Self>
    {
        static constexpr const Self* value = &FIRST;
    };
public:
    static const MyEnum SECOND;

    template<class Self> struct enum_data<1, Self>
    {
        static constexpr const Self* value = &SECOND;
    };
public:
    static const MyEnum NONE;

    template<class Self> struct enum_data<2, Self>
    {
        static constexpr const Self* value = &NONE;
    };

    template <int N> struct get : public enum_data<N, MyEnum>
    {
    };
};

const MyEnum MyEnum::FIRST = MyEnum("FIRST", 0);
const MyEnum MyEnum::SECOND = MyEnum("SECOND", 1);
const MyEnum MyEnum::NONE = MyEnum("NONE", 2);
