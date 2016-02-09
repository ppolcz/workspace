/* 
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 10, 2015, 4:01 PM
 */

#include <polcz/pczdebug.h>

#include <iostream>
#include <functional>
#include <initializer_list>
#include <vector>
#include <cmath>
#include <numeric>

template <int N, typename T>
struct vec
{
    static const int dim = N;
    typedef T dtype;

    vec() : data(N, 0) { }

    vec(std::initializer_list<T> l) : data(l)
    {
        // @todo: Is this really needed? Does it work properly?
        data.resize(N);
    }

    vec(const vec& b) : data(b.data.begin(), b.data.end()) { }

    vec(vec&& b) : data(std::move(b.data)) { }

    vec& operator=(const vec& b)
    {
        vec<N, T> v(b);
        std::swap(*this, v);
        return *this;
    }

    vec& operator=(vec&& b)
    {
        data = std::move(b.data);
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
        for (int i = 0; i < N; ++i) ret += this->operator[](i) * b[i];
        return ret;
    }

    vec sqr()
    {
        return this->operator*(*this);
    }

    T sum()
    {
        return std::accumulate(data.begin(), data.end(), 0.0);
    }

    T norm_sqr()
    {
        return (this->operator*(*this)).sum();
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
        for (int i = 0; i < N; ++i) this->operator[](i) op##= b[i]; \
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

    //private:
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

template <typename T>
struct result
{
    T x;
    typename T::dtype y;
    bool success;
    int iteration;
};

template <typename T>
std::ostream& operator<<(std::ostream& os, const result<T>& r)
{
    PCZ_TITLE("Results of the gradient search algorithm");
    PCZ_DEBUG_W(r.x);
    PCZ_DEBUG_W(r.y);
    PCZ_DEBUG_W(r.success);
    PCZ_DEBUG_W(r.iteration);

    return os;
}

typedef float dtype;
typedef vec<2, dtype> vec_2;
typedef std::function<dtype(const vec_2&)> function_2;
typedef std::function<vec_2(const vec_2&)> d_function_2;
typedef result<vec_2> result_2;

result_2 gradient_descent(const function_2& f, const d_function_2& df, dtype gamma,
    vec_2 x /* copy constructor */,
    dtype precision, int max_iteration,
    bool print = false)
{
    result_2 ret;

    auto x_old = x;
    dtype diff = precision + 1;

    int i = 0;
    for (; i < max_iteration && precision < diff; ++i)
    {
        x -= df(x) * gamma;
        diff = (x_old - x).norm();

        if (print) std::cout << "f(" << x << ") = " << f(x) << "    x_old = " << x_old << "    x = " << x << "    (x_old - x).norm() = " << (x_old - x).norm() << "    diff = " << diff << "    prec = " << precision << "    df(x) = " << df(x) << "    g*df(x) = " << df(x) * gamma << std::endl;
        x_old = x;
    }

    ret.success = !(precision < diff);
    ret.x = x;
    ret.y = f(x);
    ret.iteration = i - 1;

    return ret;
}

#include <type_traits>

int main(int, char**)
{
    {
        vec_2 v = {0.12f, 0.14f};

        auto f = [](const vec_2 & x)
        {
            return std::pow(x[0], 2) + std::pow(x[1], 2);
        };
        auto df = [](const vec_2 & x)
        {
            return vec_2{x[0]*2, (x[1] + 1)*2};
        };

        std::cout << "f({0, 0}) = " << f({0, 0}) << " == " << 0 << std::endl;
        std::cout << "f({1, 1}) = " << f({1, 1}) << " == " << 2 << std::endl;
        std::cout << "f({1, 2}) = " << f({1, 2}) << " == " << 5 << std::endl;
        std::cout << "f({1, 4}) = " << f({1, 4}) << " == " << 17 << std::endl;

        std::cout << gradient_descent(f, df, 0.1, v, 0.0001, 100, true);
    }

    // Rosenbrock function
    {
        vec_2 v = {0.12f, 0.14f};

        auto f = [](const vec_2 & x)
        {
            return std::pow(1 - x[0], 2) + 100 * std::pow(x[1] - std::pow(x[0], 2), 42);
        };
        auto df = [](const vec_2 & x)
        {
            return vec_2{ -2*(1-x[0])-400.0f*(x[1]-std::pow(x[0], 2))*x[0], 200*double(x[1]-std::pow(x[0], 2)) };
        };

        std::cout << gradient_descent(f, df, 0.001, v, 0.001, 300);
    }

    // trigonometric function
    {
        vec_2 v = {0.12f, 0.14f};

        auto f = [](const vec_2 & x)
        {
            return std::sin(3*x[0]) + std::sin(x[0]);
        };
        auto df = [](const vec_2 & x)
        {
            return vec_2 
            { 3 * std::cos(3*x[0]), std::cos(x[1]) };
        };

        std::cout << gradient_descent(f, df, 0.001, v, 0.001, 300);
    }
}
