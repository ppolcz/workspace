/* 
 * File:   main_move_2.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 10, 2015, 4:32 PM
 */

#include <polcz/pczdebug.h>

#include <type_traits>
#include <cstdlib>
#include <vector>
#include <initializer_list>

template <int N, typename T>
struct vec
{
    static const int dim = N;
    typedef T dtype;

    int id = rand();

    vec() : data(N, 0) { }

    vec(std::initializer_list<T> l) : data(l)
    {
        PCZ_DEBUGG("initializer list constructor");
        data.resize(N);
    }

    vec(const vec& b) : data(b.data.begin(), b.data.end())
    {
        PCZ_DEBUGB("copy constructor");
    }

    vec(vec&& b) : data(std::move(b.data)) { }

    vec& operator=(const vec& b)
    {
        PCZ_DEBUGB("assignment operator");
        vec<N, T> v(b);
        std::swap(*this, v);
        return *this;
    }

    vec& operator=(vec&& b)
    {
        PCZ_DEBUGR("move assignment operator");
        PCZ_DEBUG("b.data = %x, id = %d", b.data.data(), b.id);
        PCZ_DEBUG("t.data = %x, id = %d", data.data(), id);

        data = std::move(b.data);

        PCZ_DEBUG("b.data = %x, id = %d", b.data.data(), b.id);
        PCZ_DEBUG("t.data = %x, id = %d", data.data(), id);

        return *this;
    }

    T& operator[](int i)
    {
        return data[i];
    }

    const T& operator[](int i) const
    {
        return data[i];
    }

    T dot(const vec& b)
    {
        T ret;
        for (int i = 0; i < N; ++i) ret += operator[](i) * b[i];
        return ret;
    }

    T norm_sqr()
    {
        T ret;
        for (int i = 0; i < N; ++i) ret += std::pow(operator[](i), 2);
        return ret;
    }

    T norm()
    {
        return std::pow(norm_sqr(), 0.5);
    }

    #define DEFINE_OPERATOR(op) \
    vec<N,T> operator op (const vec& b) \
    { \
        vec<N,T> a = *this; \
        for (int i = 0; i < N; ++i) a[i] op##= b[i]; \
        return a; \
    } \
    \
    vec<N,T>& operator op##= (const vec& b) \
    { \
        for (int i = 0; i < N; ++i) operator[](i) op##= b[i]; \
        return *this; \
    } \
    \
    vec<N,T> operator op (const T& b) \
    { \
        vec<N,T> a = *this; \
        for (int i = 0; i < N; ++i) a[i] op##= b; \
        return a; \
    }

    DEFINE_OPERATOR(+)
    DEFINE_OPERATOR(-)
    DEFINE_OPERATOR(*)
    DEFINE_OPERATOR( /)

    ~vec()
    {
        PCZ_DEBUG("destructor: %x, id = %d", data.data(), id);
        PCZ_DEBUG_W(data.size());
    }

private:
    std::vector<T> data;
};

template <int N, typename T>
std::ostream& operator<<(std::ostream& os, const vec<N, T>& v)
{
    os << "[ ";
    for (int i = 0; i < N; ++i) os << v[i] << " ";
    os << "]";
    return os;
}

typedef float dtype;
typedef vec<2, dtype> vec_2;

void main_move_2()
{
    PCZ_TITLE("initializer list");
    vec_2 x = {0.1, 0.4};

    PCZ_TITLE("copy constructor");
    auto x_old = x;

    PCZ_TITLE("assignment operator ---> delegation to move assignment operator");
    x = x_old;

    PCZ_TITLE("some operation + assignment");
    x_old = x * 2 + x_old;

    PCZ_DEBUG_W(x);
    PCZ_DEBUG_W(x_old);

    std::cout << std::is_move_constructible<vec_2>::value << "\n";

    vec_2 v = {0.12f, 0.14f};
    auto v3 = v;
    vec_2 v2 = {0.52f, 0.54f};
    std::cout << v << v2 << v + v2 << std::endl;
    std::cout << v3 << std::endl;
}

