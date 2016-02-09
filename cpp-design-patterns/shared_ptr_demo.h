/* 
 * File:   SharedPtrDemo.h
 * Author: polpe
 *
 * Created on February 12, 2014, 12:51 AM
 */

#ifndef SHAREDPTRDEMO_H
#define	SHAREDPTRDEMO_H

#include <memory>
#include <cassert>
#include <iostream>

class SharedPtrDemo {
public:
    SharedPtrDemo();
    SharedPtrDemo(const SharedPtrDemo& orig);
    virtual ~SharedPtrDemo();

    void test1();
private:
    std::shared_ptr<int> array;

public:
    static void test();
};

#endif	/* SHAREDPTRDEMO_H */

