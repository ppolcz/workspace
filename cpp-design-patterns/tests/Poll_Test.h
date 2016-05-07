/*
 * File:   Poll_Test.h
 * Author: polpe
 *
 * Created on Apr 9, 2014, 6:36:39 PM
 */

#ifndef POLL_TEST_H
#define	POLL_TEST_H

#include <cppunit/extensions/HelperMacros.h>

class Poll_Test : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE (Poll_Test);

    CPPUNIT_TEST (simpleUseCase);
    CPPUNIT_TEST (manyTaskUseCase);

    CPPUNIT_TEST_SUITE_END ();

public:
    Poll_Test ();
    virtual ~Poll_Test ();
    void setUp ();
    void tearDown ();

private:
    void simpleUseCase ();
    void manyTaskUseCase ();
};


#endif	/* POLL_TEST_H */

