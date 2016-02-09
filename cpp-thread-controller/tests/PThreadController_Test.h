/*
 * File:   PThreadController_Test.h
 * Author: deva
 *
 * Created on May 16, 2014, 11:12:12 AM
 */

#ifndef PTHREADCONTROLLER_TEST_H
#define	PTHREADCONTROLLER_TEST_H

#include <cppunit/extensions/HelperMacros.h>

class PThreadController_Test : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE (PThreadController_Test);

    CPPUNIT_TEST (simpleUseCase);
    CPPUNIT_TEST (manyTaskUseCase);

    CPPUNIT_TEST_SUITE_END ();

public:
    PThreadController_Test ();
    virtual ~PThreadController_Test ();
    void setUp ();
    void tearDown ();

private:
    void simpleUseCase ();
    void manyTaskUseCase ();
};


#endif	/* PTHREADCONTROLLER_TEST_H */

