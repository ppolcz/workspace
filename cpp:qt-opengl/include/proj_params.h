/* 
 * File:   proj_params.h
 * Author: polpe
 *
 * Created on December 27, 2012, 2:38 PM
 */

#ifndef PROJ_PARAMS_H
#define	PROJ_PARAMS_H

#include <pcl/point_types.h>

struct ProjParams {
    float dx, dy, dz;

    uchar thresh;
    int offset;
    double spread;

    int cloud_size;

    unsigned int width_ima, height_ima;
    float width_proj, height_proj;

    pcl::PointXYZRGBA min;

    // error correction
    static const unsigned int width_error_correction = 600;
    
    /**
     * Constructor with outside parameters
     * @param min
     * @param max
     * @param cloud_size
     */
    ProjParams(const pcl::PointXYZRGBA & _min, const pcl::PointXYZRGBA & _max, int _cloud_size) {
        dx = _max.x - _min.x;
        dy = _max.y - _min.y;
        dz = _max.z - _min.z;

        // thresh, offset, spread --> unknown yet

        cloud_size = _cloud_size;

        // width_ima, height_ima --> unknown yet
        // width_proj, height_proj --> unkown yet

        min = _min;
    }

    /**
     * Setter with inside parameters
     * @param _thresh
     * @param _offset
     * @param _spread
     */
    void set_inside_params(uchar _thresh, int _offset, double _spread) {
        // dx, dy, dz --> already known

        thresh = _thresh;
        offset = _offset;
        spread = _spread;

        // cloud_size --> already known

        float fact = dx / dy;
        float prod = cloud_size * spread;
        width_ima = (uint) std::ceil(std::pow(fact * prod, 0.5)) + 2 * offset;
        height_ima = (uint) std::ceil(std::pow(prod / fact, 0.5)) + 2 * offset;
        width_proj = (float) std::pow(fact * prod, 0.5);
        height_proj = (float) std::pow(prod / fact, 0.5);

        // min already known
    }

    /**
     * Constructor with inside parameters
     * @param _thresh
     * @param _offset
     * @param _spread
     */
    ProjParams(uchar _thresh, int _offset, double _spread) {
        // dx, dy, dz --> unknown yet

        thresh = _thresh;
        offset = _offset;
        spread = _spread;

        // cloud_size --> unknown yet

        // width_ima, height_ima --> unknown yet
        // width_proj, height_proj --> unkown yet

        // min unknown yet
    }

    /**
     * Setter with outside parameters
     * @param min
     * @param max
     * @param cloud_size
     */
    void set_outside_params(const pcl::PointXYZRGBA & _min, const pcl::PointXYZRGBA & _max, int _cloud_size) {
        dx = _max.x - _min.x;
        dy = _max.y - _min.y;
        dz = _max.z - _min.z;

        // thresh, offset, spread --> already known

        cloud_size = _cloud_size;

        float fact = dx / dy;
        float prod = cloud_size * spread;
        width_ima = (uint) std::pow(fact * prod, 0.5) + 2 * offset + 1;
        height_ima = (uint) std::pow(prod / fact, 0.5) + 2 * offset + 1;
        width_proj = (float) std::pow(fact * prod, 0.5);
        height_proj = (float) std::pow(prod / fact, 0.5);

        min = _min;
    }

    /**
     * COPY CONSTRUCTOR
     * @param params
     */
    ProjParams(const ProjParams & params) :
    dx(params.dx), dy(params.dy), dz(params.dz),
    thresh(params.thresh), offset(params.offset), spread(params.spread),
    cloud_size(params.cloud_size),
    width_ima(params.width_ima), height_ima(params.height_ima),
    width_proj(params.width_proj), height_proj(params.height_proj),
    min(params.min) {
    }
};

struct TransformParams {
    pcl::PointXYZRGBA min, max;
    float div;
};

#endif	/* PROJ_PARAMS_H */

