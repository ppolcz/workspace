/* 
 * File:   main_eigen.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 17, 2015, 5:10 PM
 */

#include <polcz/pczdebug.h>

#include <Eigen/Dense>

#include <iostream>
#include <functional>

template <typename _Type, int X>
struct nonlinear_feedback_controller
{
    typedef _Type dtype;
    typedef Eigen::Matrix<dtype, X, 1> x_t;
    typedef Eigen::Matrix<dtype, X, 1> g_t;
    typedef dtype u_t;
    typedef x_t f_t;
    typedef x_t k_lqr_t;
    
    typedef std::function<f_t(const x_t&)> funcf_t;
    typedef std::function<g_t(const x_t&)> funcg_t;
    
    void set_f(funcf_t&& f)
    {
        this->f = std::move(f);
    }
    
    void set_g(funcf_t&& g)
    {
        this->g = std::move(g);
    }
    
    void set_ks(const k_lqr_t& K, const dtype& k)
    {
        this->K = K;
        this->k = k;
    }
    
    u_t apply(const x_t& x)
    {
        auto b = 1.0f / K.dot(g(x));
        auto a = -b * K.dot(f(x));
        
        PCZ_DEBUG_W(g(x).transpose());
        PCZ_DEBUG_W(K.transpose());
        PCZ_DEBUG_W(K.dot(g(x)));
        PCZ_DEBUG_W(1.0f / K.dot(g(x)));
        PCZ_DEBUG_W(f(x).transpose());
        PCZ_DEBUG_W(K.dot(f(x)));
        PCZ_DEBUG_W(-b);
        PCZ_DEBUG_W(-b * K.dot(f(x)));
        PCZ_DEBUG_W(a);
        PCZ_DEBUG_W(-k * K.dot(x));
        PCZ_DEBUG_W(b * (-k * K.dot(x)));
        PCZ_DEBUG_W(a + b * (-k * K.dot(x)));

        return a + b * (-k * K.dot(x));
    }
    
private:
    funcf_t f;
    funcg_t g;
    
    k_lqr_t K;
    u_t k;
};

void main_eigen()
{
    using namespace Eigen;
    typedef Matrix<float, 4, 1> x_t;
    
    x_t x, K;
    x << 1, 2, 3, 4;
    K << 4, 3, 2, 1;
    
    PCZ_DEBUG_W(x.dot(K));
    
    typedef nonlinear_feedback_controller<float, 4> nfc_t;
    
    nfc_t nfc;
    nfc.set_ks(K, 2);
    
    nfc.set_f([](const nfc_t::x_t& x)
    {
        return x;
    });
    
    nfc.set_g([](const nfc_t::x_t& x)
    {
        return x;
    });
    
    PCZ_DEBUG_W(nfc.apply(x));
    
    PCZ_DEBUG_W(x.data());
    float plain_x[4] = { 2, 4, 9, 1 };
    
    std::copy(plain_x, plain_x + 4, x.data());
    PCZ_DEBUG_W(x.transpose());
    PCZ_DEBUG("plain_x = %f %f %f %f", plain_x[0], plain_x[1], plain_x[2], plain_x[3]);
    
    plain_x[2] = 10;
    plain_x[1] = 9;
}

