/* 
 * File:   cnn_tester.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on June 14, 2014, 5:07 PM
 */

#include <polcz/util.h>
#include <polcz/cl_helper.h>
#include <polcz/cl_error_msg.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>

void cnn_tester (CLHelper & helper) 
{
    using namespace cv;
    
    Mat img = imread("/home/polpe/Architecture/Linux_kernel_and_OpenGL_video_games.svg.png", IMREAD_UNCHANGED);
//    Mat img = imread("/home/polpe/Pictures/lena.bmp", IMREAD_UNCHANGED);
    Mat img_after_cnn = Mat(img.rows, img.cols, img.type());
    std::vector<float> zerosf(img.rows * img.cols * 4, 0);

    cl::Event event;
    cl::NDRange global_range(img.cols, img.rows);

    cl::size_t<3> origin;
    cl::size_t<3> region;
    {
        origin[0] = origin[1] = origin[2] = 0;
        region[0] = img.cols;
        region[1] = img.rows;
        region[2] = 1;
    }
    ::size_t row_pitch, slice_pitch;
    
    cl::Kernel to_float4(helper.program, "to_float4");
    cl::Kernel to_uchar4(helper.program, "to_uchar4");
    cl::Kernel cnn(helper.program, "cnn");

    typedef std::vector<float> kernel_type;
    #define TEMPLATE_NUMBER 5
    #if TEMPLATE_NUMBER == 1
        kernel_type A = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        kernel_type B = {1, 1, 1, 1, 0, 1, 1, 1, 1};
        std::for_each(B.begin(), B.end(), [](float & a){ a /= 8.0f; });
        float z = 0.0f;
        int itnum = 1000;
        float dt = 1.0f;
    #elif TEMPLATE_NUMBER == 2
        kernel_type B = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        kernel_type A = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        std::for_each(A.begin(), A.end(), [](float & a){ a /= 9.0f; });
        float z = 0.0f;
        int itnum = 1000;
        float dt = 1.0f;
    #elif TEMPLATE_NUMBER == 3
        kernel_type B = {0, 0, 0, 0, 1, 0, 0, 0, 0};
        kernel_type A = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        std::for_each(A.begin(), A.end(), [](float & a){ a /= 10.0f; });
        std::for_each(B.begin(), B.end(), [](float & b){ b /= 10.0f; });
        float z = 0.0f;
        int itnum = 1000;
        float dt = 1.0f;
    #elif TEMPLATE_NUMBER == 4
        kernel_type B = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        kernel_type A(9,0);
        std::for_each(B.begin(), B.end(), [](float & b){ b /= 9.0f; });
        float z = 0.0f;
        int itnum = 1;
        float dt = 1.0f;
    #elif TEMPLATE_NUMBER == 5
        kernel_type A = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        kernel_type B(9,0);
        std::for_each(A.begin(), A.end(), [](float & a){ a /= 5.0f; });
        std::for_each(B.begin(), B.end(), [](float & b){ b /= 9.0f; });
        float z = 0.0f;
        int itnum = 20;
        float dt = 1.3f;
    #elif TEMPLATE_NUMBER == 6
        kernel_type A = {0, 1, 0, -1, 0, 1, 0, -1, 0};
        kernel_type B = {1, 0, 1, 0, 1, 0, 1, 0, 1};
        float z = 0.0f;
        int itnum = 1000;
        float dt = 1.0f;
    #endif
    
    cl::ImageFormat bgra_format(CL_RGBA, CL_UNSIGNED_INT8);
    cl::ImageFormat cnn_fromat(CL_RGBA, CL_FLOAT);

    cl::Image2D img_in(helper.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, bgra_format, img.cols, img.rows, 0, img.data);
    cl::Image2D img_out(helper.context, CL_MEM_READ_WRITE, bgra_format, img.cols, img.rows);
    cl::Image2D img_u(helper.context, CL_MEM_READ_WRITE, cnn_fromat, img.cols, img.rows);
    cl::Image2D img_x0(helper.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, cnn_fromat, img.cols, img.rows, 0, zerosf.data());
    cl::Image2D img_x1(helper.context, CL_MEM_READ_WRITE, cnn_fromat, img.cols, img.rows);
    
    cl::Buffer buff_A(helper.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, A.size() * sizeof(float), A.data());
    cl::Buffer buff_B(helper.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, B.size() * sizeof(float), B.data());

        
    // convert img_in to float4 and copy to img_u, img_x0
    {
        to_float4.setArg(0, img_in);
        to_float4.setArg(1, img_u);
        helper.command_queue.enqueueNDRangeKernel(to_float4, cl::NullRange, global_range, cl::NullRange, NULL, &event);
        event.wait();
        
        to_float4.setArg(1, img_x0);
        helper.command_queue.enqueueNDRangeKernel(to_float4, cl::NullRange, global_range, cl::NullRange, NULL, &event);
        event.wait();
    }
    
    // process CNN
    {
        cnn.setArg(0, img_u);
        cnn.setArg(2, buff_A);
        cnn.setArg(3, buff_B);
        cnn.setArg(4, z);
        cnn.setArg(5, dt);
        
        for (int i = 0; i < itnum; ++i) 
        {
            {
                cnn.setArg(1, img_x0);
                cnn.setArg(6, img_x1);
                helper.command_queue.enqueueNDRangeKernel(cnn, cl::NullRange, global_range, cl::NullRange, NULL, &event);
                event.wait();
            }
            
            if (++i == itnum) break;
            
            {
                cnn.setArg(1, img_x1);
                cnn.setArg(6, img_x0);
                helper.command_queue.enqueueNDRangeKernel(cnn, cl::NullRange, global_range, cl::NullRange, NULL, &event);
                event.wait();
            }
        }
    }
    
    // convert to unsigned char format
    {
        to_uchar4.setArg(0, (itnum % 2 == 1) ? img_x1 : img_x0);
        to_uchar4.setArg(1, img_out);
        helper.command_queue.enqueueNDRangeKernel(to_uchar4, cl::NullRange, global_range, cl::NullRange, NULL, &event);
        event.wait();
    }
    
    uchar* pointer = static_cast<uchar*>(helper.command_queue.enqueueMapImage( 
            img_out, CL_BLOCKING, CL_MAP_READ, origin, region, &row_pitch, &slice_pitch));
    std::copy(pointer, pointer + img.rows * img.cols * img.channels(), img_after_cnn.data);

    imshow("original", img);
    imshow("after CNN", img_after_cnn);
    waitKey(0);
}