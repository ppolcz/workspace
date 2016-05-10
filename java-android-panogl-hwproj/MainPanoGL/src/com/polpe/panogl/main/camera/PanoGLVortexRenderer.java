/* 
 * Copyright 2012 Frank Dürr
 * 
 * This file is part of OpenPanodroid.
 *
 * OpenPanodroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenPanodroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenPanodroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.polpe.panogl.main.camera;

import static com.polpe.panogl.main.camera.Coordinates.COORD;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;

import org.openpanodroid.CubicPano;
import org.openpanodroid.CubicPano.TextureFaces;
import org.openpanodroid.panoutils.android.CubicPanoNative;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;
import com.polpe.panogl.util.matrix.Matrix;
import com.polpe.panogl.util.matrix.MatrixMathematics;

public class PanoGLVortexRenderer implements GLSurfaceView.Renderer {
    private static final String LOG_TAG = PanoGLVortexRenderer.class.getSimpleName();
    // private static final float EPSILON = 1e-5f;
    private final static float ROTATION_ACCELERATION = 20; // [deg/s^2]

    private CubicPanoNative cubicPano;

    // new textures
    private Vector<int[]> newTextureIds = new Vector<int[]>();
    private Vector<FloatBuffer> newVertexCoordinates = new Vector<FloatBuffer>();
    private ShortBuffer indicesIdentity;
    private FloatBuffer textureCoordinates;

    // according to the new textures
    private boolean pictureTaken = false;
    private Bitmap bmp = null;

    private int[] textureIds = new int[6];
    private FloatBuffer cubeVertexBuffer;
    private ShortBuffer[] faceVertexIndices = new ShortBuffer[6];
    private FloatBuffer[] faceTextureCoordinates = new FloatBuffer[6];

    private float fovDeg; // diagonal field of view

    // callibration data - IMPORTANT
    private int surfaceWidth, surfaceHeight;
    private float zRotationDeg = 0;
    // Rotation around x axis in degrees (to a certain latitude circle).
    private float rotationLatitudeDeg = 0.0f;
    // Rotation around y axis in degrees (to a certain longitude circle).
    private float rotationLongitudeDeg = 0.0f;
    private Coordinates screenCoordinates;

    // While accessing these matrices, the PanoGLVortexRenderer object has to be locked.
    private float[] rotationMatrix = new float[16];
    private float[] zrotationMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] matrix = new float[16];
    private float[] matrixInv = new float[16];

    private boolean isKineticRotationActive = false;
    private float rotationSpeedLongitude0, rotationSpeedLatitude0; // [deg/s]
    private float longitude0;
    private float latitude0;
    private long t0; // [ms]

    private boolean shootMode = true;
    private boolean autoRotate = true;
    private LinkedList<String> imagesList = null;

    public PanoGLVortexRenderer(PanoGLSurfaceView view, CubicPanoNative cubicPano) {
        super();

        // this.view = view;
        this.cubicPano = cubicPano;
        this.fovDeg = GlobalConstants.DEFAULT_FOV_DEG;

        setRotation(0.0f, 0.0f);
    }

    public PanoGLVortexRenderer(PanoGLSurfaceView view) {
        super();

        this.fovDeg = GlobalConstants.DEFAULT_FOV_DEG;
        setRotation(0.0f, 0.0f);

        shootMode = false;
        autoRotate = false;
        imagesList = new LinkedList<String>();
    }

    public synchronized void startKineticRotation (float rotationSpeedLatitude, float rotationSpeedLongitude) {
        isKineticRotationActive = true;
        rotationSpeedLongitude0 = rotationSpeedLongitude;
        rotationSpeedLatitude0 = rotationSpeedLatitude;
        longitude0 = rotationLongitudeDeg;
        if (!autoRotate) {
            latitude0 = rotationLatitudeDeg;
        }
        t0 = System.currentTimeMillis();
    }

    public synchronized void stopKineticRotation () {
        isKineticRotationActive = false;
    }

    private synchronized void doKineticRotation () {
        if (!isKineticRotationActive) { return; }

        long currentTime = System.currentTimeMillis();
        float t = (currentTime - t0) / 1000.0f;
        if (t == 0.0f) { return; }

        float rLat = t * ROTATION_ACCELERATION;
        float rLon = t * ROTATION_ACCELERATION;

        if (Math.abs(rotationSpeedLatitude0) < rLat && Math.abs(rotationSpeedLongitude0) < rLon) {
            stopKineticRotation();
        }

        float deltaLat = 0, deltaLon;

        if (!autoRotate) {
            if (Math.abs(rotationSpeedLatitude0) >= rLat) {
                float rotSpeedLat_t = rotationSpeedLatitude0 - Math.signum(rotationSpeedLatitude0) * rLat;
                deltaLat = (rotationSpeedLatitude0 + rotSpeedLat_t) / 2.0f * t;
            } else {
                float tMax = Math.abs(rotationSpeedLatitude0) / ROTATION_ACCELERATION;
                deltaLat = 0.5f * tMax * rotationSpeedLatitude0;
            }
        }

        if (Math.abs(rotationSpeedLongitude0) >= rLon) {
            float rotSpeedLon_t = rotationSpeedLongitude0 - Math.signum(rotationSpeedLongitude0) * rLon;
            deltaLon = (rotationSpeedLongitude0 + rotSpeedLon_t) / 2.0f * t;
        } else {
            float tMax = Math.abs(rotationSpeedLongitude0) / ROTATION_ACCELERATION;
            deltaLon = 0.5f * tMax * rotationSpeedLongitude0;
        }

        if (autoRotate) {
            setRotation(getRotationLatitudeDeg(), longitude0 + deltaLon);
        } else {
            setRotation(latitude0 + deltaLat, longitude0 + deltaLon);
        }
    }

    private synchronized void calculateRotationMatrix () {
        // Rotation matrix in column mode.
        double rotationLongitude = Math.toRadians(rotationLongitudeDeg);
        double rotationLatitude = Math.toRadians(rotationLatitudeDeg);
        double zRotation = Math.toRadians(zRotationDeg);

        float C = (float) Math.cos(zRotation);
        float S = (float) Math.sin(zRotation);

        zrotationMatrix[0] = C;
        zrotationMatrix[1] = S;
        zrotationMatrix[2] = 0.0f;
        zrotationMatrix[3] = 0.0f;

        zrotationMatrix[4] = -S;
        zrotationMatrix[5] = C;
        zrotationMatrix[6] = 0.0f;
        zrotationMatrix[7] = 0.0f;

        zrotationMatrix[8] = 0.0f;
        zrotationMatrix[9] = 0.0f;
        zrotationMatrix[10] = 1.0f;
        zrotationMatrix[11] = 0.0f;

        zrotationMatrix[12] = 0.0f;
        zrotationMatrix[13] = 0.0f;
        zrotationMatrix[14] = 0.0f;
        zrotationMatrix[15] = 1.0f;

        rotationMatrix[0] = (float) Math.cos(rotationLongitude);
        rotationMatrix[1] = (float) (Math.sin(rotationLatitude) * Math.sin(rotationLongitude));
        rotationMatrix[2] = (float) (-1.0 * Math.cos(rotationLatitude) * Math.sin(rotationLongitude));
        rotationMatrix[3] = 0.0f;

        rotationMatrix[4] = 0.0f;
        rotationMatrix[5] = (float) Math.cos(rotationLatitude);
        rotationMatrix[6] = (float) Math.sin(rotationLatitude);
        rotationMatrix[7] = 0.0f;

        rotationMatrix[8] = (float) Math.sin(rotationLongitude);
        rotationMatrix[9] = (float) (-1.0 * Math.sin(rotationLatitude) * Math.cos(rotationLongitude));
        rotationMatrix[10] = (float) (Math.cos(rotationLongitude) * Math.cos(rotationLatitude));
        rotationMatrix[11] = 0.0f;

        rotationMatrix[12] = 0.0f;
        rotationMatrix[13] = 0.0f;
        rotationMatrix[14] = 0.0f;
        rotationMatrix[15] = 1.0f;

        matrix = multMatrix(projectionMatrix, multMatrix(zrotationMatrix, rotationMatrix));
        // float[] RInv = GaussianElimination.inverse(R);
        matrixInv = new float[16];

        matrixInv = MatrixMathematics.inverse(new Matrix(matrix, 4)).toVec();
    }

    private void normalizeRotation () {
        rotationLongitudeDeg %= 360.0f;
        if (rotationLongitudeDeg < -180.0f) {
            rotationLongitudeDeg = 360.0f + rotationLongitudeDeg;
        } else if (rotationLongitudeDeg > 180.0f) {
            rotationLongitudeDeg = -360.0f + rotationLongitudeDeg;
        }

        if (rotationLatitudeDeg < -90.0f) {
            rotationLatitudeDeg = -90.0f;
        } else if (rotationLatitudeDeg > 90.0f) {
            rotationLatitudeDeg = 90.0f;
        }

        Assert.assertTrue(rotationLongitudeDeg >= -180.0f && rotationLongitudeDeg <= 180.0f);
        Assert.assertTrue(rotationLatitudeDeg >= -90.0f && rotationLatitudeDeg <= 90.0f);
    }

    void rotate () {
        float coords[] = {
                COORD, COORD, COORD, // left, top, front
                -COORD, COORD, COORD, // right, top, front
                -COORD, COORD, -COORD, // right, top, back
                COORD, COORD, -COORD, // left, top, back
                COORD, -COORD, COORD, // left, bottom, front
                -COORD, -COORD, COORD, // right, bottom, front
                -COORD, -COORD, -COORD, // right, bottom, back
                COORD, -COORD, -COORD // left, bottom, back
        };

        double rotationLongitute = Math.toRadians(rotationLongitudeDeg);

        for (int i = 0; i < coords.length / 3; i++) {
            double x = coords[i * 3];
            double y = coords[i * 3 + 1];
            double z = coords[i * 3 + 2];

            double x2 = Math.cos(rotationLongitute) * x + Math.sin(rotationLongitute) * z;
            double y2 = y;
            double z2 = -Math.sin(rotationLongitute) * x + Math.cos(rotationLongitute) * z;

            coords[i * 3] = (float) x2;
            coords[i * 3 + 1] = (float) y2;
            coords[i * 3 + 2] = (float) z2;
        }

        cubeVertexBuffer.put(coords);
        cubeVertexBuffer.position(0);
    }

    public void setZRotation (float zRotation) {
        if (Math.abs(rotationLatitudeDeg) < 75 && shootMode) {
            this.zRotationDeg = zRotation;
        }
    }

    public void setRotation (float rotationLatitudeDeg, float rotationLongitudeDeg) {
        this.rotationLongitudeDeg = rotationLongitudeDeg;
        this.rotationLatitudeDeg = rotationLatitudeDeg;

        normalizeRotation();

        calculateRotationMatrix();
    }

    public float getRotationLatitudeDeg () {
        return rotationLatitudeDeg;
    }

    public float getRotationLongitudeDeg () {
        return rotationLongitudeDeg;
    }

    public float getRotationZDeg () {
        return zRotationDeg;
    }

    public int getSurfaceWidth () {
        return surfaceWidth;
    }

    public int getSurfaceHeight () {
        return surfaceHeight;
    }

    public float getFov () {
        return fovDeg;
    }

    public float getHFovDeg () {
        float viewDiagonal = (float) Math.sqrt(surfaceHeight * surfaceHeight + surfaceWidth * surfaceWidth);

        float hFovDeg = (float) surfaceWidth / viewDiagonal * fovDeg;

        return hFovDeg;
    }

    public float getVFovDeg () {
        float viewDiagonal = (float) Math.sqrt(surfaceHeight * surfaceHeight + surfaceWidth * surfaceWidth);

        float vFovDeg = (float) surfaceHeight / viewDiagonal * fovDeg;

        return vFovDeg;
    }

    public float[] getScreenCoordinates () {
        return screenCoordinates.getScreenCoords();
    }

    private void initCube (GL10 gl) {
        calcCubeTriangles();
        initNewBuffers();
    }

    private void calcCubeTriangles () {
        // Define the 8 vertices of the cube.
        // Using 3 dimensions (x, y, z).
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(8 * 3 * Float.SIZE / 8);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        cubeVertexBuffer = vertexByteBuffer.asFloatBuffer();

        float coords[] = {
                // nekem az x valamiert pontosan forditva van, mint ahogy itt kommenteli
                COORD, COORD, COORD, // left, top, front
                -COORD, COORD, COORD, // right, top, front
                -COORD, COORD, -COORD, // right, top, back
                COORD, COORD, -COORD, // left, top, back
                COORD, -COORD, COORD, // left, bottom, front
                -COORD, -COORD, COORD, // right, bottom, front
                -COORD, -COORD, -COORD, // right, bottom, back
                COORD, -COORD, -COORD // left, bottom, back
        };

        cubeVertexBuffer.put(coords);
        cubeVertexBuffer.position(0);

        for (CubicPano.TextureFaces face : CubicPano.TextureFaces.values()) {
            // Define which vertices make up the faces of the cube.
            // For each of the 6 faces, 4 index entries are required
            // (one triangle strip of 2 triangles with 4 vertices per face).
            int faceNo = face.ordinal();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * Short.SIZE / 8);
            byteBuffer.order(ByteOrder.nativeOrder());
            faceVertexIndices[faceNo] = byteBuffer.asShortBuffer();

            // We define triangles such that we are looking onto the inner
            // side of the cube faces (sitting in the middle of the cube). That is,
            // vertex order has to be clockwise when looking onto the inner side
            // of the face. For each face we use a triangle strip (4 vertices).
            short indices[][] = {
                    { 0, 1, 3, 2 }, // top
                    { 6, 5, 7, 4 }, // bottom
                    { 0, 4, 1, 5 }, // front
                    { 2, 6, 3, 7 }, // back
                    { 1, 5, 2, 6 }, // right
                    { 3, 7, 0, 4 } // left
            };

            switch (face)
            {
                case top:
                    faceVertexIndices[faceNo].put(indices[0]);
                    break;
                case bottom:
                    faceVertexIndices[faceNo].put(indices[1]);
                    break;
                case front:
                    faceVertexIndices[faceNo].put(indices[2]);
                    break;
                case back:
                    faceVertexIndices[faceNo].put(indices[3]);
                    break;
                case right:
                    faceVertexIndices[faceNo].put(indices[4]);
                    break;
                case left:
                    faceVertexIndices[faceNo].put(indices[5]);
                    break;
            }

            faceVertexIndices[faceNo].position(0);
        }
    }

    private void initNewBuffers () {
        indicesIdentity = ByteBuffer.allocateDirect(4 * Short.SIZE / 8).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesIdentity.put(new short[] { 0, 1, 2, 3 });
        indicesIdentity.position(0);

        textureCoordinates = ByteBuffer.allocateDirect(2 * 4 * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinates.put(new float[] {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        });
        textureCoordinates.position(0);

    }

    private void setProjection (GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float aspect = (float) surfaceWidth / (float) surfaceHeight;
        float zNear = 0.1f;
        float zFar = 2.0f;
        float fovYDeg = getVFovDeg();

        float f = (float) (1.0 / Math.tan(Math.toRadians(fovYDeg) / 2.0));

        projectionMatrix[0] = f / aspect;
        projectionMatrix[1] = 0.0f;
        projectionMatrix[2] = 0.0f;
        projectionMatrix[3] = 0.0f;

        projectionMatrix[4] = 0.0f;
        projectionMatrix[5] = f;
        projectionMatrix[6] = 0.0f;
        projectionMatrix[7] = 0.0f;

        projectionMatrix[8] = 0.0f;
        projectionMatrix[9] = 0.0f;
        projectionMatrix[10] = (zNear + zFar) / (zNear - zFar);
        projectionMatrix[11] = -1.0f;

        projectionMatrix[12] = 0.0f;
        projectionMatrix[13] = 0.0f;
        projectionMatrix[14] = 2 * zFar * zNear / (zNear - zFar);
        projectionMatrix[15] = 0.0f;

        // GLU.gluPerspective(gl, fovYDeg, aspect, zNear, zFar);
        gl.glMultMatrixf(projectionMatrix, 0);
    }

    private void setupNewTexture (GL10 gl) {
        Util.debug();

        newTextureIds.add(new int[1]);

        FloatBuffer vfb = ByteBuffer.allocateDirect(3 * 4 * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vfb.put(screenCoordinates.getScreenACoord3());
        vfb.put(screenCoordinates.getScreenDCoord3());
        vfb.put(screenCoordinates.getScreenBCoord3());
        vfb.put(screenCoordinates.getScreenCCoord3());
        vfb.position(0);

        newVertexCoordinates.add(vfb);

        int[] ids = newTextureIds.lastElement();

        gl.glMatrixMode(GL10.GL_TEXTURE);
        gl.glLoadIdentity();

        gl.glGenTextures(1, ids, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, ids[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        // gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        // gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);

        pictureTaken = false;
        bmp.recycle();
        bmp = null;

        if (imagesList != null && !imagesList.isEmpty()) {
            addNewImage(imagesList.pollFirst());
        }
    }

    private void setupTextures (GL10 gl) {
        gl.glMatrixMode(GL10.GL_TEXTURE);
        gl.glLoadIdentity();

        gl.glGenTextures(6, textureIds, 0);

        for (CubicPanoNative.TextureFaces face : CubicPanoNative.TextureFaces.values()) {
            int faceNo = face.ordinal();

            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[faceNo]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            // gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

            // POLPE - lehet erre szukseg lenne:
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            // gl.glTexEnvf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
            // gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

            Bitmap bm = null;

            Assert.assertTrue(cubicPano != null);
            bm = cubicPano.getFace(face);

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);

            // Enable alpha blend when rendering. These two lines are responsible for the transparent texturing
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_BLEND);

            // If we save the state of the activity, we should not delete the
            // original bitmaps. Otherwise, we have to reload them from the network.
            // bm.recycle();

            // For each face, we have to define 8 coordinates although only 4 are used
            // at a time -- glDrawElements() uses the same indices as for the vertex array
            // to select texture coordinates. Coordinates that are not used are marked
            // as dummy entries.
            float coordinates[][] = {
                    {
                            // top (vertices 0, 1, 3, 2)
                            1.0f, 1.0f,
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 0.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                    }, {
                            // bottom (vertices 6, 5, 7, 4)
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            1.0f, 1.0f,
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 0.0f,
                    }, {
                            // front (vertices 0, 4, 1, 5)
                            0.0f, 1.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f,
                            1.0f, 0.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f // dummy
                    }, {
                            // back (vertices 2, 6, 3, 7)
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 1.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f,
                            1.0f, 0.0f
                    }, {
                            // right (vertices 1, 5, 2, 6)
                            0.0f, 0.0f, // dummy
                            0.0f, 1.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f,
                            1.0f, 0.0f,
                            0.0f, 0.0f // dummy
                    }, {
                            // left (vertices 3, 7, 0, 4)
                            1.0f, 1.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 1.0f,
                            1.0f, 0.0f,
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f, // dummy
                            0.0f, 0.0f
                    }
            };

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 2 * Float.SIZE / 8);
            byteBuffer.order(ByteOrder.nativeOrder());
            faceTextureCoordinates[faceNo] = byteBuffer.asFloatBuffer();

            switch (face)
            {
                case top:
                    faceTextureCoordinates[faceNo].put(coordinates[0]);
                    break;
                case bottom:
                    faceTextureCoordinates[faceNo].put(coordinates[1]);
                    break;
                case front:
                    faceTextureCoordinates[faceNo].put(coordinates[2]);
                    break;
                case back:
                    faceTextureCoordinates[faceNo].put(coordinates[3]);
                    break;
                case right:
                    faceTextureCoordinates[faceNo].put(coordinates[4]);
                    break;
                case left:
                    faceTextureCoordinates[faceNo].put(coordinates[5]);
                    break;
            }

            faceTextureCoordinates[faceNo].position(0);
        }

    }

    public void setAutoRotate (boolean autoRotate) {
        this.autoRotate = autoRotate;
        Util.debug(this.autoRotate + "");
    }
    
    public boolean getAutoRotate () {
        return autoRotate;
    }
    
    public void onPictureTaken (final String filename) {
        new Thread(new Runnable() {

            @Override
            public void run () {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp = BitmapFactory.decodeFile(filename, options);
                Assert.assertTrue(bmp != null);

                int width = bmp.getWidth();
                int height = bmp.getHeight();
                int[] pixels = new int[width * height];

                bmp.getPixels(pixels, 0, width, 0, 0, width, height);
                bmp.recycle();

                for (int i = 0; i < width * height; ++i) {
                    pixels[i] = (0x99 << 24) | (0x00FFFFFF & pixels[i]);
                }

                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bmp.setPixels(pixels, 0, width, 0, 0, width, height);

                pictureTaken = true;
            }
        }).start();
    }

    @Override
    public void onDrawFrame (GL10 gl) {
        if (pictureTaken && bmp != null) setupNewTexture(gl);

        // POLPE - itt belenyultam
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        doKineticRotation();
        synchronized (this) {
            gl.glMultMatrixf(multMatrix(zrotationMatrix, rotationMatrix), 0);
        }

        if (shootMode) {
            // calculating the projection of the screen's corners onto the cube's faces
            Coordinates coords = new Coordinates(matrixInv);
            if (coords.isValid()) {
                screenCoordinates = coords;
            }
        }

        // [GL_BEGIN]
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        {
            if (shootMode) {
                // drawing face textures
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBuffer);
                for (CubicPano.TextureFaces face : CubicPano.TextureFaces.values()) {
                    int faceNo = face.ordinal();

                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[faceNo]);
                    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, faceTextureCoordinates[faceNo]);

                    // For each face, we have to draw 4 vertices (triangle strip with two triangles).
                    gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, faceVertexIndices[faceNo]);
                }
            }

            // drawing new photos' textures
            for (int i = 0; i < newTextureIds.size(); ++i) {
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, newVertexCoordinates.get(i));
                gl.glBindTexture(GL10.GL_TEXTURE_2D, newTextureIds.get(i)[0]);
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinates);
                gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, indicesIdentity);
            }
        }
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // [GL_END]
    }

    @Override
    public void onSurfaceChanged (GL10 gl, int width, int height) {
        Log.i(LOG_TAG, "Surface changed: width = " + Integer.toString(width) + "; height = " + Integer.toString(height));

        surfaceWidth = width;
        surfaceHeight = height;

        setProjection(gl);

        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceCreated (GL10 gl, EGLConfig config) {

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Don't draw back sides.
        // gl.glEnable(GL10.GL_CULL_FACE);
        // gl.glFrontFace(GL10.GL_CCW);
        // gl.glCullFace(GL10.GL_BACK);

        // Don't need depth tests since we only have one object.
        // gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do

        // gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // POLPE
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_BLEND);
        gl.glShadeModel(GL10.GL_SMOOTH);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // POLPE
        gl.glDisable(GL10.GL_DITHER); // POLPE

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        initCube(gl);

        if (shootMode) setupTextures(gl);

        gl.glClearColor(0, 0, 0, 0);
    }

    static TextureFaces intersection (float[] v, float[] in) {
        // TextureFaces tf;

        float x = v[0];
        float y = v[1];
        float z = v[2];

        // intersection with circle C(center = origin, R = 0.7)
        // this method results in better view, hence I will use THIS
        // instead of intersections with the cube
        float r = (float) Math.sqrt(x * x + y * y + z * z);
        in[0] = x / r * 0.7f;
        in[1] = y / r * 0.7f;
        in[2] = z / r * 0.7f;
        return TextureFaces.back;
    }

    static float[] multMatrixVector (float[] a, float[] v) {
        float[] w = new float[4];

        w[0] = a[0] * v[0] + a[4] * v[1] + a[8] * v[2] + a[12] * v[3];
        w[1] = a[1] * v[0] + a[5] * v[1] + a[9] * v[2] + a[13] * v[3];
        w[2] = a[2] * v[0] + a[6] * v[1] + a[10] * v[2] + a[14] * v[3];
        w[3] = a[3] * v[0] + a[7] * v[1] + a[11] * v[2] + a[15] * v[3];

        return w;
    }

    static float[] multMatrix (float[] a, float[] b) {
        float[] c = new float[16];

        c[0] = a[0] * b[0] + a[4] * b[1] + a[8] * b[2] + a[12] * b[3];
        c[4] = a[0] * b[4] + a[4] * b[5] + a[8] * b[6] + a[12] * b[7];
        c[8] = a[0] * b[8] + a[4] * b[9] + a[8] * b[10] + a[12] * b[11];
        c[12] = a[0] * b[12] + a[4] * b[13] + a[8] * b[14] + a[12] * b[15];

        c[1] = a[1] * b[0] + a[5] * b[1] + a[9] * b[2] + a[13] * b[3];
        c[5] = a[1] * b[4] + a[5] * b[5] + a[9] * b[6] + a[13] * b[7];
        c[9] = a[1] * b[8] + a[5] * b[9] + a[9] * b[10] + a[13] * b[11];
        c[13] = a[1] * b[12] + a[5] * b[13] + a[9] * b[14] + a[13] * b[15];

        c[2] = a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3];
        c[6] = a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7];
        c[10] = a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11];
        c[14] = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15];

        c[3] = a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3];
        c[7] = a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7];
        c[11] = a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11];
        c[15] = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15];

        return c;
    }

    public void addImage (String filename) {
        if (!pictureTaken && bmp == null) addNewImage(filename);
        else imagesList.add(filename);
    }

    private void addNewImage (String filename) {
        Util.debug();
        screenCoordinates = readCoordinatesFromFile(filename);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmp = BitmapFactory.decodeFile(filename, options);

        pictureTaken = true;
    }

    public Coordinates readCoordinatesFromFile (String filename) {
        float coordinates[] = new float[16];
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filename.replace(GlobalConstants.FILE_IMAGE_EXTENSION, GlobalConstants.FILE_TEXT_EXTENSION)));
            String text = null;

            int i = 0;
            while ((text = reader.readLine()) != null) {
                coordinates[i++] = Float.parseFloat(text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {}
        }
        return new Coordinates().setScreenCoords(coordinates);
    }

    // public void onPictureTaken (Bitmap bmp) {
    // pictureTaken = true;
    // this.bmp = bmp;
    // }

    // // intersection with the cube
    //
    // if (Math.abs(x) > EPSILON) {
    // in[0] = Math.signum(x) * COORD;
    // float divider = in[0] / x;
    // in[1] = y * divider;
    // in[2] = z * divider;
    //
    // if (Math.abs(in[1]) < COORD && Math.abs(in[2]) < COORD) {
    //
    // if (in[0] < 0) tf = TextureFaces.left;
    // else tf = TextureFaces.right;
    //
    // in = new float[] { in[0], in[1], in[2] };
    // return tf;
    // }
    // }
    // if (Math.abs(y) > EPSILON) {
    // in[1] = Math.signum(y) * COORD;
    // float divider = in[1] / y;
    // in[0] = x * divider;
    // in[2] = z * divider;
    //
    // if (Math.abs(in[0]) < COORD && Math.abs(in[2]) < COORD) {
    //
    // if (in[1] < 0) tf = TextureFaces.bottom;
    // else tf = TextureFaces.top;
    //
    // in = new float[] { in[0], in[1], in[2] };
    // return tf;
    // }
    // }
    // if (Math.abs(z) > EPSILON) {
    // in[2] = Math.signum(z) * COORD;
    // float divider = in[2] / z;
    // in[1] = y * divider;
    // in[0] = x * divider;
    //
    // if (Math.abs(in[1]) < COORD && Math.abs(in[0]) < COORD) {
    //
    // if (in[2] < 0) tf = TextureFaces.back;
    // else tf = TextureFaces.front;
    //
    // in = new float[] { in[0], in[1], in[2] };
    // return tf;
    // }
    // }
    //
    // return null;

    // private float[] multMatrixVector3 (float[] a, float[] v) {
    // float[] w = new float[4];
    //
    // w[0] = a[0] * v[0] + a[3] * v[1] + a[6] * v[2];
    // w[1] = a[1] * v[0] + a[4] * v[1] + a[7] * v[2];
    // w[2] = a[2] * v[0] + a[5] * v[1] + a[8] * v[2];
    // w[3] = 1.0f;
    //
    // return w;
    // }
    //
    // private float[] homogenize (float[] a) {
    // a[0] = a[0] / a[3];
    // a[1] = a[1] / a[3];
    // a[2] = a[2] / a[3];
    // a[3] = 1.0f;
    // return a;
    // }
    //
    // private float[] add3 (float[] a, float[] b) {
    // float[] c = new float[4];
    // c[0] = a[0] + b[0];
    // c[1] = a[1] + b[1];
    // c[2] = a[2] + b[2];
    // c[3] = 1.0f;
    // return c;
    // }
    //
    // private float[] sub3 (float[] a, float[] b) {
    // float[] c = new float[4];
    // c[0] = a[0] - b[0];
    // c[1] = a[1] - b[1];
    // c[2] = a[2] - b[2];
    // c[3] = 1.0f;
    // return c;
    // }
    //
    // private float[] mul3 (float[] a, float b) {
    // float[] c = new float[4];
    // c[0] = a[0] * b;
    // c[1] = a[1] * b;
    // c[2] = a[2] * b;
    // c[3] = 0.0f;
    // return c;
    // }
    //
    // private float[] linePlaneIntersection (float[] p1, float p2[], float[] a, float[] b) {
    // // p0 - assumed to be the origin
    // // b = l1
    //
    // float[] A = new float[] {
    // a[0] - b[0], a[1] - b[1], a[2] - b[2],
    // p1[0], p1[1], p1[2],
    // p2[0], p2[1], p2[2],
    // };
    //
    // // TODO - ehelyett inkabb GaussianElimination - az gyorsabb
    // float[] AInv = MatrixMathematics.inverse(new Matrix(A, 3)).toVec();
    // float t = multMatrixVector3(AInv, a)[0];
    // return add3(a, mul3(sub3(b, a), t));
    // }
}
