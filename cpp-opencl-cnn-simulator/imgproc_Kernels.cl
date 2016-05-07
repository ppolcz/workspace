/* 
 * File:   imgproc_Kernels.cl
 * Author: Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on June 14, 2014, 12:04 AM
 */


__constant sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;
__constant const float4 phi_lower = {-1, -1, -1, -1};
__constant const float4 phi_upper = {1, 1, 1, 1};

__inline float4 phi (float4 x) {
    return max(phi_lower, min(phi_upper, x));
}

/**
 * Convert from unsigned char to float [-1, 1]
 */
__kernel void to_float4 (
        __read_only image2d_t in,
        __write_only image2d_t out
    ) {
    const int2 pos = {get_global_id(0), get_global_id(1)};
    write_imagef(out, pos, (convert_float4(read_imageui(in, sampler, pos)) - 127.5f) / 127.5f);
}

/**
 * Convert from float [-1, 1] to unsigned char
 */
__kernel void to_uchar4 (
        __read_only image2d_t in,
        __write_only image2d_t out
    ) {
    const int2 pos = {get_global_id(0), get_global_id(1)};
    write_imageui(out, pos, convert_uint4_rtz(read_imagef(in, sampler, pos) * 127.5f + 127.5f));
}

__kernel void cnn (
        __read_only image2d_t u,
        __read_only image2d_t x0,
        __constant float * A,
        __constant float * B, 
        __private float z,
        __private float dt,
        __write_only image2d_t x
    ) {
    
    const int2 pos = { get_global_id(0), get_global_id(1) };
    
    int mask_size = 1; // TODO - this should be a kernel argument
    int mask_width = mask_size * 2 + 1;

    float4 a_tag = 0.0f;
    float4 b_tag = 0.0f;

    for(int a = -mask_size; a < mask_size + 1; a++) {
        for(int b = -mask_size; b < mask_size + 1; b++) {
            int2 new_pos = pos + (int2)(a,b);
            a_tag += A[a + mask_size + (b + mask_size) * mask_width] /*float*/ * phi(read_imagef(x0, sampler, new_pos)) /*float4*/; 
            b_tag += B[a + mask_size + (b + mask_size) * (mask_size * 2 + 1)] /*float*/* read_imagef(u, sampler, new_pos) /*float4*/; 
        }
    }

    float4 p = read_imagef(x0, sampler, pos);
    float4 o = (p * (1.0f - dt) + (z + a_tag + b_tag) * dt);
    write_imagef(x, pos, o);
}

__kernel void gaussian_blur(
        __read_only image2d_t image,
        __constant float * mask,
        __write_only image2d_t image_out,
        __private int maskSize
    ) {

    const int2 pos = {get_global_id(0), get_global_id(1)};

    float4 sum = 0.0f;
    for(int a = -maskSize; a < maskSize+1; a++) {
        for(int b = -maskSize; b < maskSize+1; b++) {
            sum += mask[a + maskSize + (b + maskSize) * (maskSize * 2 + 1)] * convert_float4(read_imageui(image, sampler, pos + (int2)(a,b))); 
        }
    }

    write_imageui(image_out, pos, convert_uint4_rtz(sum));
}