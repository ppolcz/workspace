package com.polpe.panogl.demo.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;

import org.openpanodroid.CubicPano;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;

/**
 *  OpenGL Custom renderer used with GLSurfaceView
 */
@SuppressWarnings("unused")
// TODO - warningokat kiszedni
public class GLPanoRenderer implements GLSurfaceView.Renderer {

    private GLTextureCube phcube;

    Context context;

    public GLPanoRenderer(Context context) {
        phcube = new GLTextureCube(context);
        this.context = context;

        this.fovDeg = GlobalConstants.DEFAULT_FOV_DEG;

        setRotation(0.0f, 0.0f);
    }

    // Call back when the surface is first created or re-created
    @Override
    public void onSurfaceCreated (GL10 gl, EGLConfig config) {
        gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER); // Disable dithering for better performance

        // Setup Texture, each time the surface is created (Photocube)
        phcube.loadTexture(gl); // Load images into textures (Photocube)
        gl.glEnable(GL10.GL_TEXTURE_2D); // Enable texture (Photocube)

        gl.glClearColor(0, 0, 0, 0);
    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged (GL10 gl, int width, int height) {
        if (height == 0) height = 1; // To prevent divide by zero
        float aspect = (float) width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // // Setup perspective projection, with aspect ratio matches viewport
        // gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        // gl.glLoadIdentity(); // Reset projection matrix
        // // Use perspective projection
        // GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
        gl.glLoadIdentity(); // Reset

        surfaceWidth = width;
        surfaceHeight = height;

        setProjection(gl);
        gl.glViewport(0, 0, width, height);
    }

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame (GL10 gl) {

        // Clear color and depth buffers using clear-value set earlier
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // ----- Render the Color Cube -----
        // gl.glLoadIdentity(); // Reset the model-view matrix
        gl.glTranslatef(0.0f, 0.0f, 0.0f); // Translate right and into the screen
        gl.glScalef(1.2f, 1.2f, 1.2f); // Scale down (3D shapes)

        doKineticRotation();

        // synchronized (this) {
        // gl.glLoadMatrixf(rotationMatrix, 0);
        // }

        gl.glRotatef((float) (zRotationDeg), 0, 0, 1);

        synchronized (this) {
            gl.glMultMatrixf(rotationMatrix, 0);
        }

        // Util.debug("------------------------------------------------------------------------");
        // Util.debug("%s %s %s %s", rotationMatrix[0], rotationMatrix[1], rotationMatrix[2], rotationMatrix[3]);
        // Util.debug("%s %s %s %s", rotationMatrix[4], rotationMatrix[5], rotationMatrix[6], rotationMatrix[7]);
        // Util.debug("%s %s %s %s", rotationMatrix[8], rotationMatrix[9], rotationMatrix[10], rotationMatrix[11]);
        // Util.debug("%s %s %s %s", rotationMatrix[12], rotationMatrix[13], rotationMatrix[14], rotationMatrix[15]);

        // gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
        // gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);

        // gl.glMatrixMode(GL10.GL_MODELVIEW);
        // gl.glLoadIdentity();
        //
        // gl.glRotatef(30.75f, 0.0f, 0.0f, 1.0f);

        phcube.draw(gl); // Draw the cube (Texture Cube)
    }

    // =====================================================================================

    private final static float COORD = (float) Math.sin(Math.PI / 4.0);

    private final static float ROTATION_ACCELERATION = 20; // [deg/s^2]

    private FloatBuffer cubeVertexBuffer;
    private ShortBuffer[] faceVertexIndices = new ShortBuffer[6];

    private float fovDeg; // diagonal field of view

    // Rotation around x axis in degrees (to a certain latitude circle).
    private float rotationLatitudeDeg = 0.0f;
    // Rotation around y axis in degrees (to a certain longitude circle).
    private float rotationLongitudeDeg = 0.0f;
    // While accessing this matrix, the PanodroidVortexRenderer object has to be locked.
    private float[] rotationMatrix = new float[16];

    private int surfaceWidth, surfaceHeight;

    private boolean isKineticRotationActive = false;
    private float rotationSpeedLongitude0, rotationSpeedLatitude0; // [deg/s]
    private float zRotationDeg;

    private float latitude0, longitude0;
    private long t0; // [ms]

    public synchronized void startKineticRotation (float rotationSpeedLatitude, float rotationSpeedLongitude) {
        isKineticRotationActive = true;
        rotationSpeedLongitude0 = rotationSpeedLongitude;
        rotationSpeedLatitude0 = rotationSpeedLatitude;
        latitude0 = rotationLatitudeDeg;
        longitude0 = rotationLongitudeDeg;

        // Util.debug("lat  = " + rotationLatitudeDeg);
        // Util.debug("long = " + rotationLatitudeDeg);

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

        // Util.debug("rLat  = " + rLat);
        // Util.debug("rLong = " + rLon);

        if (Math.abs(rotationSpeedLatitude0) < rLat && Math.abs(rotationSpeedLongitude0) < rLon) {
            stopKineticRotation();
        }

        float deltaLat, deltaLon;

        if (Math.abs(rotationSpeedLatitude0) >= rLat) {
            float rotSpeedLat_t = rotationSpeedLatitude0 - Math.signum(rotationSpeedLatitude0) * rLat;
            deltaLat = (rotationSpeedLatitude0 + rotSpeedLat_t) / 2.0f * t;
        } else {
            float tMax = Math.abs(rotationSpeedLatitude0) / ROTATION_ACCELERATION;
            deltaLat = 0.5f * tMax * rotationSpeedLatitude0;
        }

        if (Math.abs(rotationSpeedLongitude0) >= rLon) {
            float rotSpeedLon_t = rotationSpeedLongitude0 - Math.signum(rotationSpeedLongitude0) * rLon;
            deltaLon = (rotationSpeedLongitude0 + rotSpeedLon_t) / 2.0f * t;
        } else {
            float tMax = Math.abs(rotationSpeedLongitude0) / ROTATION_ACCELERATION;
            deltaLon = 0.5f * tMax * rotationSpeedLongitude0;
        }

        // Util.debug("deltaLat  = " + deltaLat);
        // Util.debug("deltaLong = " + deltaLon);

        // TODO - only azimuth direction
        setRotation(getRotationLatitudeDeg(), longitude0 + deltaLon);
    }

    private synchronized void calculateRotationMatrix () {
        // Rotation matrix in column mode.

        double rotationLongitude = Math.toRadians(rotationLongitudeDeg);
        double rotationLatitude = Math.toRadians(rotationLatitudeDeg);

        // rotationMatrix[0] = (float) (Math.cos(rotationLatitude) * Math.cos(rotationLongitude));
        // rotationMatrix[1] = (float) (-Math.sin(rotationLatitude) * Math.cos(rotationLongitude));
        // rotationMatrix[2] = (float) (-Math.cos(rotationLatitude) * Math.sin(rotationLongitude));
        // rotationMatrix[3] = 0.0f;
        //
        // rotationMatrix[4] = (float) (Math.sin(rotationLatitude) * Math.cos(rotationLongitude));
        // rotationMatrix[5] = (float) (Math.cos(rotationLatitude) * Math.cos(rotationLongitude));
        // rotationMatrix[6] = (float) (-Math.sin(rotationLatitude) * Math.sin(rotationLongitude));
        // rotationMatrix[7] = 0.0f;
        //
        // rotationMatrix[8] = (float) Math.sin(rotationLongitude);
        // rotationMatrix[9] = (float) Math.sin(rotationLongitude);
        // rotationMatrix[10] = (float) Math.cos(rotationLongitude);
        // rotationMatrix[11] = 0.0f;
        //
        // rotationMatrix[12] = 0.0f;
        // rotationMatrix[13] = 0.0f;
        // rotationMatrix[14] = 0.0f;
        // rotationMatrix[15] = 1.0f;

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

    public void setZRotation (float zRotation) {
        if (Math.abs(rotationLatitudeDeg) < 75) {
            this.zRotationDeg = zRotation;
        }
    }

    public void setRotation (float rotationLatitudeDeg, float rotationLongitudeDeg) {
        this.rotationLatitudeDeg = rotationLatitudeDeg;
        this.rotationLongitudeDeg = rotationLongitudeDeg;

        // Util.debug(rotationLatitudeDeg + ", " + rotationLongitudeDeg);

        normalizeRotation();
        calculateRotationMatrix();
    }

    public float getRotationLatitudeDeg () {
        return rotationLatitudeDeg;
    }

    public float getRotationLongitudeDeg () {
        return rotationLongitudeDeg;
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

    private void initCube (GL10 gl) {
        calcCubeTriangles();
    }

    private void calcCubeTriangles () {
        // Define the 8 vertices of the cube.
        // Using 3 dimensions (x, y, z).
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(8 * 3 * Float.SIZE / 8);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        cubeVertexBuffer = vertexByteBuffer.asFloatBuffer();

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

    // TODO EZT IS FEL KELLENE HASZNALNI
    private void setProjection (GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float aspect = (float) surfaceWidth / (float) surfaceHeight;
        float dNear = 0.1f;
        float dFar = 2.0f;
        float fovYDeg = getVFovDeg();

        // TODO
        GLU.gluPerspective(gl, fovYDeg, aspect, dNear, dFar);
    }
}
