/* 
 * File:   pattern_free.cpp
 * Author: deva
 * 
 * Created on February 11, 2014, 5:30 PM
 */

int MY_GLOBAL_INT = 15;

#include "pattern_free.h"

#include <iostream>

Parent::Parent (std::string _name, Parent * _parent) : name (_name), parent (_parent) {
    if (parent) parent->addChild(this);
    std::cout << "Parent::Parent  -  " << this << "  " << name << "\n";
}

Parent::Parent (const Parent& orig) { }

Parent::~Parent () {
    std::cout << "Parent::~Parent  -  " << this << "  " << name << "\n";
    if (parent != 0) parent->removeChild(this);
    for (auto & child : children) {
        delete child;
    }
}

void Parent::addChild (Parent* child) {
    children.push_back(child);
}

void Parent::removeChild (Parent* child) {
    for (auto it = children.begin(); it != children.end(); ++it) {
        if (*it == child) {
            children.erase(it);
            break;
        }
    }
}

// ------------------------------------------------

Child::Child (std::string name, Parent * parent) : Parent (name, parent) {
    std::cout << "Child::Child  -  " << this << "  " << name << "\n";
}

Child::~Child () {
    std::cout << "Child::~Child  -  " << this << "  " << name << "\n";
}
