package com.polpe.accelerometer;

public class Integrator {
    float[] buffer;
    int ptr;
    int size;
    float dsize;
    float sum;

    public Integrator(int size) {
        this.size = size;
        this.dsize = (float) size;
        this.ptr = 0;
        this.sum = 0.0f;
        buffer = new float[size];
    }

    public void add (float element) {
        // subtract the last element which should be removed from the buffer
        sum -= buffer[ptr];
        // put the new element in place of the last element in the buffer
        buffer[ptr] = element;
        // add the new element to the sum
        sum += element;
        // increment ptr
        ptr = (ptr + 1) % size;
    }

    public float getMean () {
        return sum / dsize;
    }

    public float integrate (float element) {
        add(element);
        return getMean();
    }
}
