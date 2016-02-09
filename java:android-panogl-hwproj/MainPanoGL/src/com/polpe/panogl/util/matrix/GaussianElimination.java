package com.polpe.panogl.util.matrix;

public class GaussianElimination {
    private static final float EPSILON = 1e-10f;

    public static final float[] EYE4 = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };
    
    public static float[] lsolve(float[] A, float[] B, int N, int M) {

        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i + N * p]) > Math.abs(A[max + N * p])) {
                    max = i;
                }
            }

            for (int j = 0 ; j < N ; j++) {
                float temp = A[p + N * j];
                A[p + N * j] = A[max + N * j];
                A[max + N * j] = temp;
            }

            for (int j = 0; j < M; j++) {
                float   t    = B[p + N * j];
                B[p + N * j] = B[max + N * j];
                B[max + N * j] = t;
            }

            // singular or nearly singular
            if (Math.abs(A[p + N * p]) <= EPSILON) {
                throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot within A and B
            for (int i = p + 1; i < N; i++) {
                float alpha = A[i + N * p] / A[p + N * p];

                for (int j = 0; j < M; j++) {
                    B[i + N * j] -= alpha * B[p + N * j];
                }

                for (int j = p; j < N; j++) {
                    A[i + N * j] -= alpha * A[p + N * j];
                }
            }
        }

        // back substitution
        float[] x = new float[N * M];
        for (int i = N - 1; i >= 0; i--) {
            for (int k = 0; k < M; k++) {
                float sum = 0.0f;
                for (int j = i + 1; j < N; j++) {
                    sum += A[i + N * j] * x[j + N * k];
                }
                x[i + N * k] = (B[i + N * k] - sum) / A[i + N * i];
            }
        }

        return x;
    }

    public static float[] inverse (float [] A) {
        return lsolve(A, EYE4, 4, 4);
    }
    
}


