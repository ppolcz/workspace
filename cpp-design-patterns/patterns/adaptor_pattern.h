/* 
 * File:   adaptor_pattern.h
 * Author: polpe
 *
 * Created on February 23, 2014, 1:29 PM
 */

#ifndef ADAPTOR_PATTERN_H
#define	ADAPTOR_PATTERN_H

#include <iostream>

class DesiredInterface {
public:
    virtual void desiredMember () = 0;
};

class DesiredImpl : public DesiredInterface {
    virtual void desiredMember () {
        std::cout << "desiredMember\n";
    }
};

class OldInterface {
public:

    virtual void oldMember () {
        std::cout << "oldMember\n";
    }
};

class Adapter : public DesiredInterface, private OldInterface {
public:

    virtual void desiredMember () {
        oldMember();
    }
};

class AdaptorPattern {
public:

    static void main () {
        DesiredInterface * desired = new Adapter;
        desired->desiredMember();
        delete desired;
        
        desired = new DesiredImpl;
        desired->desiredMember();
        delete desired;
    }
};

#endif	/* ADAPTOR_PATTERN_H */

