/* 
 * File:   destruktor_test.h
 * Author: polpe
 *
 * Created on February 28, 2014, 2:49 PM
 */

#ifndef DESTRUKTOR_TEST_H
#define	DESTRUKTOR_TEST_H

class Base {
public:
    Base () {
        std::cout << "Base\n";
    }
    
    virtual ~Base () {
        std::cout << "~Base\n";
    }
};

class Childish : public Base {
public:
    Childish () {
        std::cout << "Child\n";
    }
    
    virtual ~Childish () {
        std::cout << "~Child\n";
    }
};

#endif	/* DESTRUKTOR_TEST_H */

