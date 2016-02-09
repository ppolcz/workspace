/* 
 * File:   main_friend.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 15, 2015, 8:57 PM
 */

#include <iostream>

class A 
{
    int a = 1;
    
    friend class B;
};

class AA : public A
{
    int aa = 2;
};

class AAA : public AA
{
    int aaa = 3;
};

class B
{
    int b = 1;

public:
    void f (const A &a, const AAA&)
    {
        std::cout << "B can access A: " << a.a << "\n";
        std::cout << "B cannot access AAA: " << "\n";
    }
};

class BB : public B
{
    int bb = 2;
};

class BBB : public BB
{
    int bbb = 3;

public:
    void f (const A&)
    {
        std::cout << "BBB cannot access A: " << "\n";
    }

};

int main_friend()
{
    A a;
    B b;
    AAA aaa;
    BBB bbb;
    
    b.f(a, aaa);
    bbb.f(a);
    
    
    return 0;
}

