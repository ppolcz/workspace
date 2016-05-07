/* 
 * File:   cgal_kiegeszito.h
 * Author: polpe
 *
 * Created on February 13, 2013, 9:25 AM
 */

#ifndef CGAL_KIEGESZITO_H
#define	CGAL_KIEGESZITO_H

#include <string>

#include <CGAL/Cartesian.h>

typedef CGAL::Cartesian<double> Kernel;

namespace CGAL {

    enum Axis {
        AXIS_X = 0,
        AXIS_Y = 1,
        AXIS_Z = 2,
        PLANE_YZ = 0,
        PLANE_XZ = 1,
        PLANE_XY = 2
    } ;

    inline Point_3<Kernel> to_3d (Plane_3<Kernel> plain, Point_2<Kernel> p, Axis axis = AXIS_Z) {
        Kernel::RT A = plain.a();
        Kernel::RT B = plain.b();
        Kernel::RT C = plain.c();
        Kernel::RT D = plain.d();

        Kernel::RT x1;
        Kernel::RT x2;
        Kernel::RT x3;

        switch (axis) {
            case AXIS_Z:
                x1 = p.x();
                x2 = p.y();
                x3 = -A / C * x1 - B / C * x2 - D / C;
                break; 
            case AXIS_X:
                x2 = p.x();
                x3 = p.y();
                x1 = -B / A * x2 - C / A * x3 - D / A;
                break;
            case AXIS_Y:
                x1 = p.x();
                x3 = p.y();
                x2 = -A / B * x1 - C / B * x3 - D / B;
                break;
        }
        return Point_3<Kernel > (x1, x2, x3);
    }

    inline Point_2<Kernel> to_2d (Point_3<Kernel> p, Axis axis = AXIS_Z) {

        Kernel::RT x;
        Kernel::RT y;

        switch (axis) {
            case AXIS_Z:
                x = p.x();
                y = p.y();
                break;
            case AXIS_X:
                x = p.y();
                y = p.z();
                break;
            case AXIS_Y:
                x = p.x();
                y = p.z();
                break;
        }
        return Point_2<Kernel > (x,y);
    }


}

#endif	/* CGAL_KIEGESZITO_H */

