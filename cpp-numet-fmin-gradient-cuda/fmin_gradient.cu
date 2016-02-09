/* 
 * File:   main.cu
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 13, 2015, 10:15 AM
 */

// System includes
#include <iostream>
#include <stdio.h>
#include <assert.h>

// CUDA runtime
#include <cuda_runtime.h>

// Helper functions and utilities to work with CUDA
#include <helper_cuda.h>
#include <helper_string.h>
#include <helper_functions.h>

#ifndef __CUDA_ENABLED__
#define blockIdx
#define blockDim
#define threadIdx
#define atomicExch
#define atomicCAS
#define atomicAdd
#endif

//__device__ __forceinline__ float func(float x, float y)
//{
//    return x * x + y * y; // 18 clock cycles ( - clock()'s clock cycles )
//}
//
//__device__ __forceinline__ void dfunc(float x, float y, float &dx, float &dy)
//{
//    dx = 2*x;
//    dy = 2*y;
//}

/*
 * Matlab: 
[x,y] = meshgrid([-10:0.1:10]); surf(x, y, sin(3*x) + sin(y)), view(0,90), shading interp, hold on, plot3(-0.523599, -1.482375, 100, 'sr') 
 */
//__device__ __forceinline__ float func(float x, float y)
//{
//    return sin(3*x) + sin(y); // ~764 clock cycles
//}
//
//__device__ __forceinline__ void dfunc(float x, float y, float &dx, float &dy)
//{
//    dx = 3*cos(3*x);
//    dy = cos(y);
//}

/*
 * Matlab: 
[x,y] = meshgrid([-3:0.1:3]); surf(x, y, sin(2*x) + sin(3*y) + (x+1).^2 + y), shading interp, hold on, plot3(-0.857750, -0.636904, 100, 'sr'), plot3(-0.857553, -2.731273, 100, 'sr'), view(0,90)
 */
__device__ __forceinline__ float func(float x, float y)
{
    return sin(2*x) + sin(3*x) + (x+1)*(x+1) + y; // ~700 clock cycles - trigonometric functions
}

__device__ __forceinline__ void dfunc(float x, float y, float &dx, float &dy)
{
    dx = 2*cos(2*x) + 2*(x+1);
    dy = 3*cos(3*y) + 1;
}

/* multiply gradient with gamma scalar */
__device__ __forceinline__ 
void dfunc_gamma(float x, float y, float &dx, float &dy, float gamma)
{
    dfunc(x,y,dx,dy);
    dx *= gamma;
    dy *= gamma;
}

/* meaning the will produce some 128 bit load instead of several 32 bit load */
__device__ __align__(16) float variable[1024];

__device__ __align__(16) float4 result;
__device__ int lock;

__global__ void gradient_search(
    float *_x, float *_y,
    int size, float gamma, float precision, int iternum)
{
    atomicExch(&lock, 0);
    
    // arguments: 6x 32bit (the pointers could be 64bit)
    
    //float4 xx = make_float4(1,2,3,4);
    //float4 yy = make_float4(3,4,5,6);
    //float4 zz = xx + yy; // not oke
    
    float x, y, dx, dy, x_old, y_old, tmp; 
    {
        int i = blockIdx.x * blockDim.x + threadIdx.x;
        x = _x[i];
        y = _y[i];
    }
    
    x_old = x;
    y_old = y;
    
    /* gradient descent iteration */
    int it = 0;
    float diff = precision + 1;
    for (; it < iternum && precision < diff; ++it)
    {
        // gradient search step
        dfunc_gamma(x, y, dx, dy, gamma);
        x -= dx;
        y -= dy;

        // calculate Euclidean distance between [x, y] and [x_old, y_old]
        diff = x_old - x;
        diff *= diff;
        tmp = y_old - y;
        tmp *= tmp;
        diff += tmp;
        diff = sqrt(diff);
        
        // store new function arguments
        x_old = x;
        y_old = y;
    }
    
    int k = 0;
    float fmin = func(x,y);

    /* if it == iternum, the gradient method has not found a good solution */
    if (it < iternum)
    {
        /* if the gradient search succeeded, then update the result, 
         * thread safety is critical */
        
        // IMPORTANT: this is a good and safe solution for mutual exclusion
        bool wait = true;
        int max = 10000;
        for (; k < max && wait; ++k) {
            if (atomicCAS(&lock, 0, 1) == 0) {

                // thread safe code [BEGIN]

                if (result.z > fmin) 
                {
                    result.x = x;
                    result.y = y;
                    result.z = fmin;
                }

                // thread safe code [END]
                
                wait = false;
                atomicExch(&lock, 0);
            }
        } 
    }
    

    // IMPORTANT: this is not a working solution for mutual exclusion, causes hard freezing
    // --
    // for (int k = 0; atomicCAS(&lock, 0, 1) && k < 100; ++k);
    // 
    // printf("thread %d: %d\n", threadIdx.x, result.x);
    // result.x += 1;
    // 
    // atomicExch(&lock,0);

    printf("x = [%f, %f], f = %f, iteration = %d, k = %d\n", x, y, fmin, it-1, k);
    
    if (blockIdx.x * blockDim.x + threadIdx.x == 0)
    {
        printf("\nbest: x = [%f, %f], f = %f\n\n", result.x, result.y, result.z);
    }
}

__global__ void gradient_search_dt(
    float *_x, float *_y, float *_r, int4 *_t,
    int dtnr, int size, float gamma, float precision, int iternum)
{
    clock_t t0 = clock(), t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16;
    
    atomicExch(&lock, 0);
    t1 = clock(); // 22
    
    float x, y, dx, dy, x_old, y_old, tmp; 
    int i = blockIdx.x * blockDim.x + threadIdx.x;
    x = _x[i];
    y = _y[i];
    t2 = clock(); // 500-700 - global memory access
    
    x_old = x;
    y_old = y;
    t3 = clock(); // 7 - this takes effectively no time
    
    /* gradient descent iteration */
    int it = 0;
    float diff = precision;
    diff += 123;
    t4 = clock(); // 6
    
    diff += 0.1;
    t5 = clock(); // 146-147 - floating point addition !!!
    
    for (; it < iternum && precision < diff; ++it)
    {
        // gradient search step
        dfunc_gamma(x, y, dx, dy, gamma);
        x -= dx;
        y -= dy;

        // calculate Euclidean distance between [x, y] and [x_old, y_old]
        diff = x_old - x;
        diff *= diff;
        tmp = y_old - y;
        tmp *= tmp;
        diff += tmp;
        diff = sqrt(diff);
        
        // store new function arguments
        x_old = x;
        y_old = y;
    }
    t6 = clock(); // 7000-9000
    
    int k = 0;
    float fmin = func(x,y);
    t7 = clock(); // it depends on func(...)

    /* if it == iternum, the gradient method has not found a good solution */
    if (it < iternum)
    {
        /* if the gradient search succeeded, then update the result, 
         * thread safety is critical */
        
        // IMPORTANT: this is a good and safe solution for mutual exclusion
        bool wait = true;
        int max = 10000;
        t9 = clock();
        for (; k < max && wait; ++k) {
            t10 = clock();
            if (atomicCAS(&lock, 0, 1) == 0) {

                // thread safe code [BEGIN]
                if (_r[3] > fmin) 
                {
                    _r[0] = x;
                    _r[1] = y;
                    _r[3] = fmin;
                }

                // thread safe code [END]
                
                wait = false;
                atomicExch(&lock, 0);
            }
            t11 = clock(); // t11 - t10 ~ equal on all threads
        } 
    }
    t8 = clock();

    t12 = clock();
    if (i == 0)
    {
        x = 0;
        x += sin(sqrt(0.123));
        x -= 0.1;
        x += -0.023;
    } 
    else if (i == 1)
    {
        x = 0;
        x += sqrt(sin(0.243));
    }
    else if (i == 2)
    {
        x = 0;
        x += sqrt(sin(0.243));
    }
    else if (i == 3)
    {
        x = 0;
        x += sqrt(sin(0.243));
    }
    t13 = clock();
    
    _t[dtnr * i + 0] = make_int4(t1 - t0, t2 - t1, t3 - t2, t4 - t3);
    _t[dtnr * i + 1] = make_int4(t5 - t4, t6 - t5, t7 - t6, t8 - t7);
    _t[dtnr * i + 2] = make_int4(t11 - t9, t11 - t10, 0, t8 - t0);
    _t[dtnr * i + 3] = make_int4(t13 - t12, float_as_int(x), it-1, k-1);
}

__global__ void gradient_search(
    float *_x, float *_y, float *_r, int4 *_t,
    int size, float gamma, float precision, int iternum)
{
    clock_t t0 = clock(), t1, t2, t3, t4, t5, t6, t7, t8;
    
    atomicExch(&lock, 0);
    t1 = clock();
    
    float x, y, dx, dy, x_old, y_old, tmp; 
    int i = blockIdx.x * blockDim.x + threadIdx.x;
    x = _x[i];
    y = _y[i];
    t2 = clock();
    
    x_old = x;
    y_old = y;
    t3 = clock();
    
    /* gradient descent iteration */
    int it = 0;
    float diff = precision + 1;
    for (; it < iternum && precision < diff; ++it)
    {
        // gradient search step
        dfunc_gamma(x, y, dx, dy, gamma);
        x -= dx;
        y -= dy;

        // calculate Euclidean distance between [x, y] and [x_old, y_old]
        diff = x_old - x;
        diff *= diff;
        tmp = y_old - y;
        tmp *= tmp;
        diff += tmp;
        diff = sqrt(diff);
        
        // store new function arguments
        x_old = x;
        y_old = y;
    }
    t4 = clock();
    
    int k = 0;
    float fmin = func(x,y);

    /* if it == iternum, the gradient method has not found a good solution */
    if (it < iternum)
    {
        /* if the gradient search succeeded, then update the result, 
         * thread safety is critical */
        
        // IMPORTANT: this is a good and safe solution for mutual exclusion
        bool wait = true;
        int max = 10000;
        t5 = clock();
        for (; k < max && wait; ++k) {
            if (atomicCAS(&lock, 0, 1) == 0) {

                // thread safe code [BEGIN]
                t6 = clock();
                if (_r[3] > fmin) 
                {
                    _r[0] = x;
                    _r[1] = y;
                    _r[3] = fmin;
                }

                // thread safe code [END]
                
                wait = false;
                atomicExch(&lock, 0);
            }
        } 
    }
    t7 = clock();
    t8 = clock();

    _t[2 * i + 0] = make_int4(t1 - t0, t2 - t0, t3 - t0, t4 - t0);
    _t[2 * i + 1] = make_int4(t5 - t0, t6 - t0, t7 - t0, t8 - t0);
}

template<int N>
int array_size(float (&array)[N])
{
    return N;
}

void gradient_search_launch()
{
    float x[] = { -1, 1, 1, -1, 2, 2, -2, -2, -2, -2, 2, 2 };
    float y[] = { -1, -1, 1, 1, 2, 1, 2, 1, -2, -1, -2, -2 };
    printf("array size = %d\n", array_size(x));
    
    float *d_x, *d_y, *d_r;
    int4 *d_dt;
    
    const int size = array_size(x);
    const int dtnr = 16;
    int mem_size = sizeof(float) * size;
    int dt_size = sizeof(int) * dtnr * size;

    int dt[size * dtnr];
    
    cudaMalloc((void**) &d_x, mem_size);
    cudaMalloc((void**) &d_y, mem_size);
    cudaMalloc((void**) &d_r, 16);
    cudaMalloc((void**) &d_dt, dt_size);
    
    cudaMemcpy(d_x, x, mem_size, cudaMemcpyHostToDevice);
    cudaMemcpy(d_y, y, mem_size, cudaMemcpyHostToDevice);
    cudaMemcpy(d_dt, dt, dt_size, cudaMemcpyHostToDevice);
    
    gradient_search_dt<<<1,size>>>(d_x, d_y, d_r, d_dt, dtnr/4, size, 0.1, 0.001, 1000);
    
    float res[4];
    
    cudaError_t error;
    cudaEvent_t stop;
    
    error = cudaEventCreate(&stop);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to create stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }
    
    // Record the stop event
    error = cudaEventRecord(stop, NULL);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to record stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    // Wait for the stop event to complete
    error = cudaEventSynchronize(stop);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to synchronize on the stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

//    float msecTotal = 0.0f;
//    error = cudaEventElapsedTime(&msecTotal, start, stop);
//
//    if (error != cudaSuccess)
//    {
//        fprintf(stderr, "Failed to get time elapsed between events (error code %s)!\n", cudaGetErrorString(error));
//        exit(EXIT_FAILURE);
//    }
//
//    // Compute and print the performance
//    float msecPerMatrixMul = msecTotal / nIter;
//    double flopsPerMatrixMul = 2.0 * (double)dimsA.x * (double)dimsA.y * (double)dimsB.x;
//    double gigaFlops = (flopsPerMatrixMul * 1.0e-9f) / (msecPerMatrixMul / 1000.0f);
//    printf(
//        "Performance= %.2f GFlop/s, Time= %.3f msec, Size= %.0f Ops, WorkgroupSize= %u threads/block\n",
//        gigaFlops,
//        msecPerMatrixMul,
//        flopsPerMatrixMul,
//        threads.x * threads.y);

    // Copy result from device to host
    cudaMemcpy(res, d_r, array_size(res) * sizeof(float), cudaMemcpyDeviceToHost);
    cudaMemcpy(dt, d_dt, dt_size, cudaMemcpyDeviceToHost);
    
    std::cout << "result:\n";
    printf("x = [%f, %f], f = %f\n", res[0], res[1], res[3]);

    for (int i = 0; i < size; ++i)
    {
        printf("thread %d, clock cycles: [ ", i);
        for (int j = 0; j < dtnr; ++j)
        {
            if (j % 4 == 0 && j != 0) std::cout << "| ";
            std::cout << dt[i*dtnr + j] << " ";
        }
        std::cout << "]\n";
    }
    
    cudaFree(d_x);
    cudaFree(d_y);
    cudaFree(d_r);
    cudaFree(d_dt);
}

/**
 * Matrix multiplication (CUDA Kernel) on the device: C = A * B
 * wA is A's width and wB is B's width
 */
template <int BLOCK_SIZE> 
__global__ void matrixMulCUDA(float *C, float *A, float *B, int wA, int wB)
{
    // Block index
    int bx = blockIdx.x;
    int by = blockIdx.y;

    // Thread index
    int tx = threadIdx.x;
    int ty = threadIdx.y;

    // Index of the first sub-matrix of A processed by the block
    int aBegin = wA * BLOCK_SIZE * by;

    // Index of the last sub-matrix of A processed by the block
    int aEnd   = aBegin + wA - 1;

    // Step size used to iterate through the sub-matrices of A
    int aStep  = BLOCK_SIZE;

    // Index of the first sub-matrix of B processed by the block
    int bBegin = BLOCK_SIZE * bx;

    // Step size used to iterate through the sub-matrices of B
    int bStep  = BLOCK_SIZE * wB;

    // Csub is used to store the element of the block sub-matrix
    // that is computed by the thread
    float Csub = 0;

    // Loop over all the sub-matrices of A and B
    // required to compute the block sub-matrix
    for (int a = aBegin, b = bBegin;
         a <= aEnd;
         a += aStep, b += bStep)
    {

        // Declaration of the shared memory array As used to
        // store the sub-matrix of A
        __shared__ float As[BLOCK_SIZE][BLOCK_SIZE];

        // Declaration of the shared memory array Bs used to
        // store the sub-matrix of B
        __shared__ float Bs[BLOCK_SIZE][BLOCK_SIZE];

        // Load the matrices from device memory
        // to shared memory; each thread loads
        // one element of each matrix
        As[ty][tx] = A[a + wA * ty + tx];
        Bs[ty][tx] = B[b + wB * ty + tx];

        // Synchronize to make sure the matrices are loaded
        __syncthreads();

        // Multiply the two matrices together;
        // each thread computes one element
        // of the block sub-matrix
#pragma unroll

        for (int k = 0; k < BLOCK_SIZE; ++k)
        {
            Csub += As[ty][k] * Bs[k][tx];
        }

        // Synchronize to make sure that the preceding
        // computation is done before loading two new
        // sub-matrices of A and B in the next iteration
        __syncthreads();
    }

    // Write the block sub-matrix to device memory;
    // each thread writes one element
    int c = wB * BLOCK_SIZE * by + BLOCK_SIZE * bx;
    C[c + wB * ty + tx] = Csub;
}

void constantInit(float *data, int size, float val)
{
    for (int i = 0; i < size; ++i)
    {
        data[i] = val;
    }
}

/**
 * Run a simple test of matrix multiplication using CUDA
 */
int matrixMultiply(int argc, char **argv, int block_size, dim3 &dimsA, dim3 &dimsB)
{
    // Allocate host memory for matrices A and B
    unsigned int size_A = dimsA.x * dimsA.y;
    unsigned int mem_size_A = sizeof(float) * size_A;
    float *h_A = (float *)malloc(mem_size_A);
    unsigned int size_B = dimsB.x * dimsB.y;
    unsigned int mem_size_B = sizeof(float) * size_B;
    float *h_B = (float *)malloc(mem_size_B);

    // Initialize host memory
    const float valB = 0.01f;
    constantInit(h_A, size_A, 1.0f);
    constantInit(h_B, size_B, valB);

    // Allocate device memory
    float *d_A, *d_B, *d_C;

    // Allocate host matrix C
    dim3 dimsC(dimsB.x, dimsA.y, 1);
    unsigned int mem_size_C = dimsC.x * dimsC.y * sizeof(float);
    float *h_C = (float *) malloc(mem_size_C);

    if (h_C == NULL)
    {
        fprintf(stderr, "Failed to allocate host matrix C!\n");
        exit(EXIT_FAILURE);
    }

    cudaError_t error;

    error = cudaMalloc((void **) &d_A, mem_size_A);

    if (error != cudaSuccess)
    {
        printf("cudaMalloc d_A returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    error = cudaMalloc((void **) &d_B, mem_size_B);

    if (error != cudaSuccess)
    {
        printf("cudaMalloc d_B returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    error = cudaMalloc((void **) &d_C, mem_size_C);

    if (error != cudaSuccess)
    {
        printf("cudaMalloc d_C returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    // copy host memory to device
    error = cudaMemcpy(d_A, h_A, mem_size_A, cudaMemcpyHostToDevice);

    if (error != cudaSuccess)
    {
        printf("cudaMemcpy (d_A,h_A) returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    error = cudaMemcpy(d_B, h_B, mem_size_B, cudaMemcpyHostToDevice);

    if (error != cudaSuccess)
    {
        printf("cudaMemcpy (d_B,h_B) returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    // Setup execution parameters
    dim3 threads(block_size, block_size);
    dim3 grid(dimsB.x / threads.x, dimsA.y / threads.y);

    // Create and start timer
    printf("Computing result using CUDA Kernel...\n");

    // Performs warmup operation using matrixMul CUDA kernel
    if (block_size == 16)
    {
        matrixMulCUDA<16><<< grid, threads >>>(d_C, d_A, d_B, dimsA.x, dimsB.x);
    }
    else
    {
        matrixMulCUDA<32><<< grid, threads >>>(d_C, d_A, d_B, dimsA.x, dimsB.x);
    }

    printf("done\n");

    cudaDeviceSynchronize();

    // Allocate CUDA events that we'll use for timing
    cudaEvent_t start;
    error = cudaEventCreate(&start);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to create start event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    cudaEvent_t stop;
    error = cudaEventCreate(&stop);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to create stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    // Record the start event
    error = cudaEventRecord(start, NULL);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to record start event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    // Execute the kernel
    int nIter = 300;

    for (int j = 0; j < nIter; j++)
    {
        if (block_size == 16)
        {
            matrixMulCUDA<16><<< grid, threads >>>(d_C, d_A, d_B, dimsA.x, dimsB.x);
        }
        else
        {
            matrixMulCUDA<32><<< grid, threads >>>(d_C, d_A, d_B, dimsA.x, dimsB.x);
        }
    }

    // Record the stop event
    error = cudaEventRecord(stop, NULL);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to record stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    // Wait for the stop event to complete
    error = cudaEventSynchronize(stop);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to synchronize on the stop event (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    float msecTotal = 0.0f;
    error = cudaEventElapsedTime(&msecTotal, start, stop);

    if (error != cudaSuccess)
    {
        fprintf(stderr, "Failed to get time elapsed between events (error code %s)!\n", cudaGetErrorString(error));
        exit(EXIT_FAILURE);
    }

    // Compute and print the performance
    float msecPerMatrixMul = msecTotal / nIter;
    double flopsPerMatrixMul = 2.0 * (double)dimsA.x * (double)dimsA.y * (double)dimsB.x;
    double gigaFlops = (flopsPerMatrixMul * 1.0e-9f) / (msecPerMatrixMul / 1000.0f);
    printf(
        "Performance= %.2f GFlop/s, Time= %.3f msec, Size= %.0f Ops, WorkgroupSize= %u threads/block\n",
        gigaFlops,
        msecPerMatrixMul,
        flopsPerMatrixMul,
        threads.x * threads.y);

    // Copy result from device to host
    error = cudaMemcpy(h_C, d_C, mem_size_C, cudaMemcpyDeviceToHost);

    if (error != cudaSuccess)
    {
        printf("cudaMemcpy (h_C,d_C) returned error code %d, line(%d)\n", error, __LINE__);
        exit(EXIT_FAILURE);
    }

    printf("Checking computed result for correctness: ");
    bool correct = true;

    // test relative error by the formula
    //     |<x, y>_cpu - <x,y>_gpu|/<|x|, |y|>  < eps
    double eps = 1.e-6 ; // machine zero

    for (int i = 0; i < (int)(dimsC.x * dimsC.y); i++)
    {
        double abs_err = fabs(h_C[i] - (dimsA.x * valB));
        double dot_length = dimsA.x;
        double abs_val = fabs(h_C[i]);
        double rel_err = abs_err/abs_val/dot_length ;

        if (rel_err > eps)
        {
            printf("Error! Matrix[%05d]=%.8f, ref=%.8f error term is > %E\n", i, h_C[i], dimsA.x*valB, eps);
            correct = false;
        }
    }

    printf("%s\n", correct ? "Result = PASS" : "Result = FAIL");

    // Clean up memory
    free(h_A);
    free(h_B);
    free(h_C);
    cudaFree(d_A);
    cudaFree(d_B);
    cudaFree(d_C);

    printf("\nNote: For peak performance, please refer to the matrixMulCUBLAS example.\n");

    // cudaDeviceReset causes the driver to clean up all state. While
    // not mandatory in normal operation, it is good practice.  It is also
    // needed to ensure correct operation when the application is being
    // profiled. Calling cudaDeviceReset causes all profile data to be
    // flushed before the application exits
    cudaDeviceReset();

    if (correct)
    {
        return EXIT_SUCCESS;
    }
    else
    {
        return EXIT_FAILURE;
    }
}


/**
 * Program main
 */
int main_gradient(int argc, char** argv)
{
    printf("[Matrix Multiply Using CUDA] - Starting...\n");

    if (checkCmdLineFlag(argc, (const char **)argv, "help") ||
        checkCmdLineFlag(argc, (const char **)argv, "?"))
    {
        printf("Usage -device=n (n >= 0 for deviceID)\n");
        printf("      -wA=WidthA -hA=HeightA (Width x Height of Matrix A)\n");
        printf("      -wB=WidthB -hB=HeightB (Width x Height of Matrix B)\n");
        printf("  Note: Outer matrix dimensions of A & B matrices must be equal.\n");

        exit(EXIT_SUCCESS);
    }

    // By default, we use device 0, otherwise we override the device ID based on what is provided at the command line
    int devID = 0;

    if (checkCmdLineFlag(argc, (const char **)argv, "device"))
    {
        devID = getCmdLineArgumentInt(argc, (const char **)argv, "device");
        cudaSetDevice(devID);
    }

    cudaError_t error;
    cudaDeviceProp deviceProp;
    error = cudaGetDevice(&devID);

    if (error != cudaSuccess)
    {
        printf("cudaGetDevice returned error code %d, line(%d)\n", error, __LINE__);
    }

    error = cudaGetDeviceProperties(&deviceProp, devID);

    if (deviceProp.computeMode == cudaComputeModeProhibited)
    {
        fprintf(stderr, "Error: device is running in <Compute Mode Prohibited>, no threads can use ::cudaSetDevice().\n");
        exit(EXIT_SUCCESS);
    }

    if (error != cudaSuccess)
    {
        printf("cudaGetDeviceProperties returned error code %d, line(%d)\n", error, __LINE__);
    }
    else
    {
        printf("GPU Device %d: \"%s\" with compute capability %d.%d\n\n", devID, deviceProp.name, deviceProp.major, deviceProp.minor);
    }

    // Use a larger block size for Fermi and above
    int block_size = (deviceProp.major < 2) ? 16 : 32;

    dim3 dimsA(5*2*block_size, 5*2*block_size, 1);
    dim3 dimsB(5*4*block_size, 5*2*block_size, 1);

    // width of Matrix A
    if (checkCmdLineFlag(argc, (const char **)argv, "wA"))
    {
        dimsA.x = getCmdLineArgumentInt(argc, (const char **)argv, "wA");
    }

    // height of Matrix A
    if (checkCmdLineFlag(argc, (const char **)argv, "hA"))
    {
        dimsA.y = getCmdLineArgumentInt(argc, (const char **)argv, "hA");
    }

    // width of Matrix B
    if (checkCmdLineFlag(argc, (const char **)argv, "wB"))
    {
        dimsB.x = getCmdLineArgumentInt(argc, (const char **)argv, "wB");
    }

    // height of Matrix B
    if (checkCmdLineFlag(argc, (const char **)argv, "hB"))
    {
        dimsB.y = getCmdLineArgumentInt(argc, (const char **)argv, "hB");
    }

    if (dimsA.x != dimsB.y)
    {
        printf("Error: outer matrix dimensions must be equal. (%d != %d)\n",
               dimsA.x, dimsB.y);
        exit(EXIT_FAILURE);
    }

    printf("MatrixA(%d,%d), MatrixB(%d,%d)\n", dimsA.x, dimsA.y, dimsB.x, dimsB.y);

    gradient_search_launch();
    int matrix_result = matrixMultiply(argc, argv, block_size, dimsA, dimsB);

    exit(matrix_result);
}

