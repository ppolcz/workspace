/* 
 * File:   pattern_free.h
 * Author: deva
 *
 * Created on February 11, 2014, 5:30 PM
 */

#ifndef PATTERN_FREE_H
#define	PATTERN_FREE_H

extern int MY_GLOBAL_INT;

#include <vector>
#include <string>

class Parent {
public:
    Parent (std::string name = "papa", Parent * parent = 0);
    Parent (const Parent& orig);
    virtual ~Parent ();

protected:
    void addChild(Parent * child);
    void removeChild(Parent * child);
    std::string name;

private:
    Parent * parent;
    std::vector<Parent*> children;
};

// ------------------------------------------------

class Child : public Parent {
public:
    Child(std::string name, Parent * parent = 0);
    virtual ~Child ();
};

#endif	/* DUMMY_H */

