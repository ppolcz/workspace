/*
 * File:   BlockingQueue_Test.h
 * Author: polpe
 *
 * Created on Apr 9, 2014, 5:35:33 PM
 */

#ifndef BLOCKINGQUEUE_TEST_H
#define	BLOCKINGQUEUE_TEST_H

#include <cppunit/extensions/HelperMacros.h>
#include "PBlockingQueue.hpp"

class PBlockingQueue_Test : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE (PBlockingQueue_Test);

    CPPUNIT_TEST (simpleUseCase);
    CPPUNIT_TEST (popInterruptUseCase);
    CPPUNIT_TEST (tooMutchPushUseCase);

    CPPUNIT_TEST_SUITE_END ();

public:
    PBlockingQueue_Test ();
    virtual ~PBlockingQueue_Test ();
    void setUp ();
    void tearDown ();

private:
    void simpleUseCase ();
    void popInterruptUseCase ();
    void tooMutchPushUseCase ();
};

struct MyType {

    MyType () { }

    MyType (const MyType &) { }

    MyType (MyType &&) { }

    ~MyType () { }

    MyType& operator = (const MyType & t) { }

    void valami (int, int) {
        std::cout << "valami\n";
    }
};

#endif	/* BLOCKINGQUEUE_TEST_H */

