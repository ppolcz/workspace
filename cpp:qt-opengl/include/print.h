/* 
 * File:   print.h
 * Author: polpe
 *
 * Created on October 9, 2012, 11:17 PM
 */

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <algorithm>
#include <iterator>
#include <boost/lexical_cast.hpp>
#include "pcl/point_types.h"


#ifndef PRINT_H
#define	PRINT_H

class Printer {
public:

    template <typename _Type>
    static void print_point(_Type a, _Type b) {
        std::cout << "(" << a << "," << b << ")";
    }

    template <typename _Type>
    static void print_point(_Type a, _Type b, _Type c) {
        std::cout << "(" << a << "," << b << "," << c << ")";
    }

    template <typename _Type>
    static std::string print_point_str(_Type a, _Type b) {
        std::stringstream tmp;
        tmp << "(" << a << "," << b << ")";
        return tmp.str();
    }

    template <typename _PointType>
    static void print_point(_PointType p) {
        std::cout << "(" << (float) p.x << "," << (float) p.y << "," << (float) p.z << ")";
    }

    template <typename _PointType>
    static std::string print_point_str(_PointType p) {
        std::stringstream tmp;
        tmp << "(" << (int) p.x << "," << (int) p.y << "," << (int) p.z << ")";
        return tmp.str();
    }

    template <typename _Type>
    static void print_array(_Type * a, unsigned int height, unsigned int width, std::ostream & stream = std::cout) {
        for (unsigned int i = 0; i < height; i++) {
            stream << "| ";
            for (unsigned int j = 0; j < width; j++) {
                stream.width(10);
                stream.precision(4);
                stream << (double) a[i * width + j] << " ";
            }
            stream << " |\n";
        }
    }

    template <typename T>
    static std::string print_array_matlab(T * a, unsigned int height, unsigned int width) {
        std::string str = "[";
        for (unsigned int i = 0; i < height - 1; i++) {
            str += " ";
            for (unsigned int j = 0; j < width - 1; j++) {
                str += QString::number((double) a[i * width + j]).toStdString() + ", ";
            }
            str += QString::number((double) a[i * width + width - 1]).toStdString();
            str += ";";
        }
        for (unsigned int j = 0; j < width - 1; j++) {
            str += QString::number((double) a[(height - 1) * width + j]).toStdString() + ", ";
        }
        str += QString::number((double) a[(height - 1) * width + width - 1]).toStdString();
        str += "]";
        return str;
    }

    template <typename Iterator>
    static void print_array(Iterator __begin, Iterator __end) {
        std::cout << "[  ";
        std::copy(__begin, __end, std::ostream_iterator<double>(std::cout, "  "));
        std::cout << "]\n";
    }

    static void print_newline(unsigned int nr = 1) {
        for (unsigned int i = 0; i < nr; i++) {
            std::cout << std::endl;
        }
    }

    static void print_string(std::string str) {
        std::cout << "|" << str << "|";
    }

};
#endif	/* PRINT_H */

