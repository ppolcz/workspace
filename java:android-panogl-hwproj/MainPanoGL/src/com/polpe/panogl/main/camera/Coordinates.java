package com.polpe.panogl.main.camera;

import junit.framework.Assert;

import org.openpanodroid.CubicPano.TextureFaces;

public class Coordinates {
    public static final float COORD = (float) Math.sin(Math.PI / 4.0);
    static final float DIVIDER = 0.99f;

    static final float[] cubeA = new float[] { -COORD, -COORD, COORD, 0.0f };
    static final float[] cubeB = new float[] { COORD, -COORD, COORD, 0.0f };
    static final float[] cubeC = new float[] { COORD, -COORD, -COORD, 0.0f };
    static final float[] cubeD = new float[] { -COORD, -COORD, -COORD, 0.0f };
    static final float[] cubeE = new float[] { -COORD, COORD, COORD, 0.0f };
    static final float[] cubeF = new float[] { COORD, COORD, COORD, 0.0f };
    static final float[] cubeG = new float[] { COORD, COORD, -COORD, 0.0f };
    static final float[] cubeH = new float[] { -COORD, COORD, -COORD, 0.0f };

    static final float[] screenA = new float[] { -1.0f, -1.0f, 1.0f, 1.0f };
    static final float[] screenB = new float[] { 1.0f, -1.0f, 1.0f, 1.0f };
    static final float[] screenC = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    static final float[] screenD = new float[] { -1.0f, 1.0f, 1.0f, 1.0f };

    static final float[][] screen = new float[][] { screenA, screenB, screenC, screenD };

    private float[] screenCoords = new float[16];

    private TextureFaces screenAFace;
    private TextureFaces screenBFace;
    private TextureFaces screenCFace;
    private TextureFaces screenDFace;

    private boolean isValid = true;

    public Coordinates() {}
    
    public Coordinates(float[] matrixInv) {
        for (int i = 0; i < 4; ++i) {
            float[] wv = PanoGLVortexRenderer.multMatrixVector(matrixInv, Coordinates.screen[i]);

            float[] in = new float[4];
            setScreenAFace(PanoGLVortexRenderer.intersection(wv, in));
            setScreenCoord(i, in);
        }
    }

    public TextureFaces getScreenAFace () {
        return screenAFace;
    }

    public TextureFaces getScreenBFace () {
        return screenBFace;
    }

    public TextureFaces getScreenCFace () {
        return screenCFace;
    }

    public TextureFaces getScreenDFace () {
        return screenDFace;
    }

    public void setScreenAFace (TextureFaces screenAFace) {
        this.screenAFace = screenAFace;
        if (screenAFace == null) isValid = false;
    }

    public void setScreenBFace (TextureFaces screenBFace) {
        this.screenBFace = screenBFace;
        if (screenBFace == null) isValid = false;
    }

    public void setScreenCFace (TextureFaces screenCFace) {
        this.screenCFace = screenCFace;
        if (screenCFace == null) isValid = false;
    }

    public void setScreenDFace (TextureFaces screenDFace) {
        this.screenDFace = screenDFace;
        if (screenDFace == null) isValid = false;
    }

    public float[] getScreenACoord () {
        return new float[] { screenCoords[0], screenCoords[1], screenCoords[2], screenCoords[3] };
    }

    public float[] getScreenBCoord () {
        return new float[] { screenCoords[4], screenCoords[5], screenCoords[6], screenCoords[7] };
    }

    public float[] getScreenCCoord () {
        return new float[] { screenCoords[8], screenCoords[9], screenCoords[10], screenCoords[11] };
    }

    public float[] getScreenDCoord () {
        return new float[] { screenCoords[12], screenCoords[13], screenCoords[14], screenCoords[15] };
    }

    public float[] getScreenACoord3 () {
        return new float[] { screenCoords[0] * DIVIDER, screenCoords[1] * DIVIDER, screenCoords[2] * DIVIDER};
    }

    public float[] getScreenBCoord3 () {
        return new float[] { screenCoords[4] * DIVIDER, screenCoords[5] * DIVIDER, screenCoords[6] * DIVIDER};
    }

    public float[] getScreenCCoord3 () {
        return new float[] { screenCoords[8] * DIVIDER, screenCoords[9] * DIVIDER, screenCoords[10] * DIVIDER};
    }

    public float[] getScreenDCoord3 () {
        return new float[] { screenCoords[12] * DIVIDER, screenCoords[13] * DIVIDER, screenCoords[14] * DIVIDER};
    }

    public float[] getScreenCoord (int i) {
        return new float[] { screenCoords[4 * i], screenCoords[4 * i + 1], screenCoords[4 * i + 2], screenCoords[4 * i + 3] };
    }
    
    public float[] getScreenCoords () {
        float[] coords = new float[16];
        coords = screenCoords.clone();
        return coords;
    }

    public boolean isValid () {
        return isValid;
    }
    
    public Coordinates setScreenCoord (int i, float[] a) {
        screenCoords[4 * i + 0] = a[0];
        screenCoords[4 * i + 1] = a[1];
        screenCoords[4 * i + 2] = a[2];
        screenCoords[4 * i + 3] = a[3];
        return this;
    }
    
    public Coordinates setScreenCoords (float[] coords) {
        Assert.assertTrue(coords.length == 16);
        screenCoords = coords.clone();
        return this;
    }
}
