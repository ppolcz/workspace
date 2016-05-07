/* 
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on June 13, 2014, 10:59 PM
 */

#include <cstdlib>
#include <polcz/util.h>
#include <polcz/cl_helper.h>
#include <polcz/cl_error_msg.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>

void blur_tester (CLHelper &);
void cnn_tester (CLHelper &);

/*
 * 
 */
int main (int argc, char** argv) {

    try 
    {
        CLHelper helper;
        helper.setup_all(CL_DEVICE_TYPE_GPU);
        helper.build_program("imgproc_Kernels.cl", "-x clc++");
                
//        blur_tester(helper);
        cnn_tester(helper);
    }
    catch (cl::Error & e)
    {
        P_PRINT_CL_ERROR_M(e, "error caught in the main try-catch block");
    }
    
    return 0;
}

