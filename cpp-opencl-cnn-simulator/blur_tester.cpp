/* 
 * File:   blur_tester.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on June 15, 2014, 12:37 PM
 */

#include <polcz/util.h>
#include <polcz/cl_helper.h>
#include <polcz/cl_error_msg.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>

void blur_tester (CLHelper & helper)
{
    using namespace cv;
    
    Mat img = imread("/home/polpe/Architecture/Linux_kernel_and_OpenGL_video_games.svg.png", IMREAD_UNCHANGED);
    imshow("OpenGL", img);
    int mask_size = 3;
    // std::vector<float> mask = {1, 2, 1, 2, 3, 2, 1, 2, 1};
    std::vector<float> mask((2 * mask_size + 1) * (2 * mask_size + 1), 1);
    DEBUG_W(mask.size());
    float sum = 0;
    std::for_each(mask.begin(), mask.end(), [&sum](float & a){ sum += a; });
    std::for_each(mask.begin(), mask.end(), [&sum](float & a){ a /= sum; });

    cl::ImageFormat format(CL_RGBA, CL_UNSIGNED_INT8);

    cl::Image2D img_in(helper.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, img.cols, img.rows, 0, img.data);
    cl::Image2D img_out(helper.context, CL_MEM_READ_WRITE, format, img.cols, img.rows);
    cl::Buffer buff_mask(helper.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, mask.size() * sizeof(float), mask.data());

    cl::Kernel gaussian_blur_kernel(helper.program, "gaussian_blur");
    gaussian_blur_kernel.setArg(0, img_in);
    gaussian_blur_kernel.setArg(1, buff_mask);
    gaussian_blur_kernel.setArg(2, img_out);
    gaussian_blur_kernel.setArg(3, mask_size);

    cl::Event event;
    helper.command_queue.enqueueNDRangeKernel(gaussian_blur_kernel, 
            cl::NullRange, cl::NDRange(img.cols, img.rows), cl::NullRange, NULL, &event);

    cl::size_t<3> origin;
    cl::size_t<3> region;
    origin[0] = origin[1] = 0;
    origin[2] = 0;
    region[0] = img.cols;
    region[1] = img.rows;
    region[2] = 1;
    ::size_t row_pitch, slice_pitch;

    event.wait();

    uchar* pointer = static_cast<uchar*>(helper.command_queue.enqueueMapImage(img_out, CL_BLOCKING, CL_MAP_READ, 
            origin, region, &row_pitch, &slice_pitch));
    std::copy(pointer, pointer + img.rows * img.cols * img.channels(), img.data);

    imshow("blurred", img);
    waitKey(0);
}