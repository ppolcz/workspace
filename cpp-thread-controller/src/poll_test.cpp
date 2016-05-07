/* 
 * File:   poll_test.cpp
 * Author: Polcz Péter
 *
 * Created on Feb 20, 2014, 6:01:27 PM
 */

#include <ctime>
#include <cstdlib>
#include <iostream>
#include <vector>
#include <algorithm>
#include "PThreadController.h"

#define MATRIX_SIZE 2000
class Matrix
{
    unsigned m_size;
    std::vector<double> m_data;

public:

    Matrix (unsigned _size) : m_size (_size)
    {
        m_data.resize(m_size * m_size);
        std::transform(m_data.begin(), m_data.end(), m_data.begin(), [](int) {
            return drand48();
        });
    }

    double & operator() (unsigned i, unsigned j)
    {
        return m_data[i * m_size + j];
    }

    unsigned size () const
    {
        return m_size;
    }
};

void test1 ()
{
    Matrix a(MATRIX_SIZE), b(MATRIX_SIZE), c(MATRIX_SIZE);
    PThreadController poll;

    for (unsigned i = 0; i < MATRIX_SIZE; ++i)
    {
        auto worker = [&a, &b, &c, i] () {
            for (unsigned j = 0; j < MATRIX_SIZE; ++j)
            {
                c(i, j) = 0;
                for (unsigned k = 0; k < MATRIX_SIZE; ++k)
                    c(i, j) += a(i, k) * b(k, j);
            }
        };
        poll.push(worker);
    }

    poll.join();
}

void test2 ()
{
    Matrix a(MATRIX_SIZE), b(MATRIX_SIZE), c(MATRIX_SIZE);

    for (unsigned i = 0; i < MATRIX_SIZE; ++i)
        for (unsigned j = 0; j < MATRIX_SIZE; ++j)
        {
            c(i, j) = 0;
            for (unsigned k = 0; k < MATRIX_SIZE; ++k)
                c(i, j) += a(i, k) * b(k, j);
        }
}

int poll_tests ()
{
    srand48(time(0));

    {
        std::cout << "test1 (poll_test)" << std::endl;
        clock_t begin_time = clock();
        test1();
        std::cout << "time=" << float( clock() - begin_time) / CLOCKS_PER_SEC << " test1 (poll_test)\n" << std::endl;
    }

    {
        std::cout << "test2 (poll_test)" << std::endl;
        clock_t begin_time = clock();
        test2();
        std::cout << "time=" << float( clock() - begin_time) / CLOCKS_PER_SEC << " test2 (poll_test)" << std::endl;
    }
    return (EXIT_SUCCESS);
}
