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

import java.util.Stack;

import junit.framework.Assert;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.polpe.panogl.sensorreadout.OnRotationChangedListener;
import com.polpe.panogl.sensorreadout.PolpeSensorListener;
import com.polpe.panogl.util.GlobalConstants;

public class PanoGLSurfaceView extends GLSurfaceView implements OnRotationChangedListener {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = PanoGLSurfaceView.class.getSimpleName();

    private static final long KINETIC_INTERVAL = 200; // [ms]
    private static final int REST_THRESHOLD = 5; // [pixel]

    private PanoGLVortexRenderer renderer;
    private PolpeSensorListener sensorListener;

    private Stack<EventInfo> motionEvents;
    private float zRotationRad = 0.0f;

    class EventInfo {
        public float x;
        public float y;
        public long time;

        public EventInfo(float x, float y, long time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }

    @Override
    public boolean onTouchEvent (final MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                stopKineticRotation();

                motionEvents = new Stack<EventInfo>();
                motionEvents.push(new EventInfo(event.getX(), event.getY(), event.getEventTime()));

                return true;
            case MotionEvent.ACTION_MOVE:
                Assert.assertTrue(motionEvents != null);

                if (motionEvents.size() > 0) {
                    EventInfo lastEvent = motionEvents.lastElement();

                    float distX = event.getX() - lastEvent.x;
                    float distY = event.getY() - lastEvent.y;

                    rotate(distX, distY);
                }

                motionEvents.push(new EventInfo(event.getX(), event.getY(), event.getEventTime()));

                return true;
            case MotionEvent.ACTION_UP:
                Assert.assertTrue(motionEvents != null);

                motionEvents.push(new EventInfo(event.getX(), event.getY(), event.getEventTime()));

                startKineticRotation();

                return true;
            case MotionEvent.ACTION_CANCEL:
                motionEvents = null;
                return true;
        }

        return false;
    }

    private void stopKineticRotation () {
        renderer.stopKineticRotation();
    }

    private void startKineticRotation () {
        Assert.assertTrue(motionEvents != null);

        if (motionEvents.size() < 2) { return; }

        // last event
        EventInfo event2 = motionEvents.pop();

        // event before the last
        EventInfo event1 = motionEvents.pop();

        long tEnd = event2.time;
        float directionX = 0.0f;
        float directionY = 0.0f;
        long tStart = tEnd;

        while (event1 != null && tEnd - event1.time < KINETIC_INTERVAL) {
            tStart = event1.time;
            directionX += event2.x - event1.x;
            directionY += event2.y - event1.y;

            event2 = event1;
            if (motionEvents.size() > 0) {
                event1 = motionEvents.pop();
            } else {
                event1 = null;
            }
        }

        // POLPE - transform the direction to the actual zRotation angle
        float dx = (float) (directionX * Math.cos(zRotationRad) - directionY * Math.sin(zRotationRad));
        float dy = (float) (directionX * Math.sin(zRotationRad) + directionY * Math.cos(zRotationRad));

        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist <= REST_THRESHOLD) { return; }

        // The pointer was moved by more than REST_THRESHOLD pixels in the last
        // KINETIC_INTERVAL seconds (or less). --> We have a kinetic scroll event.

        float deltaT = (tEnd - tStart) / 1000.0f;
        if (deltaT == 0.0f) { return; }

        int surfaceWidth = renderer.getSurfaceWidth();
        int surfaceHeight = renderer.getSurfaceHeight();

        float hFovDeg = renderer.getHFovDeg();
        float vFovDeg = renderer.getVFovDeg();

        float deltaLongitute = dx / surfaceWidth * hFovDeg;
        float rotationSpeedLongitude = deltaLongitute / deltaT;

        float deltaLatitude = dy / surfaceHeight * vFovDeg;
        float rotationSpeedLatitude = deltaLatitude / deltaT;

        if (rotationSpeedLongitude == 0.0f && rotationSpeedLatitude == 0.0f) { return; }

        renderer.startKineticRotation(-1.0f * rotationSpeedLatitude, -1.0f * rotationSpeedLongitude);
    }

    public void rotate (float deltaX, float deltaY) {

        // TODO - ezek az elojelek nem tokeletesek igy
        float dx = (float) (deltaX * Math.cos(zRotationRad) - deltaY * Math.sin(zRotationRad));
        float dy = (float) (deltaX * Math.sin(zRotationRad) + deltaY * Math.cos(zRotationRad));

        int surfaceWidth = renderer.getSurfaceWidth();
        int surfaceHeight = renderer.getSurfaceHeight();

        float aspect = (float) surfaceWidth / (float) surfaceHeight;
        float rotationLatitudeDeg = renderer.getRotationLatitudeDeg();
        float rotationLongitudeDeg = renderer.getRotationLongitudeDeg();
        float hFovDeg = renderer.getHFovDeg();

        float deltaLongitute = dx / surfaceWidth * hFovDeg;
        rotationLongitudeDeg -= deltaLongitute;

        float fovYDeg = hFovDeg / aspect;
        float deltaLatitude = dy / surfaceHeight * fovYDeg;
        rotationLatitudeDeg -= deltaLatitude;

        renderer.setRotation(rotationLatitudeDeg, rotationLongitudeDeg);
    }

    @Override
    // OnRotationChangedListener.~
    public void onXRotationChanged (float zRotation) {}

    @Override
    // OnRotationChangedListener.~
    public void onYRotationChanged (float zRotation) {
        renderer.setRotation(zRotation, renderer.getRotationLongitudeDeg());
    }

    @Override
    // OnRotationChangedListener.~
    public void onZRotationChanged (float zRotation) {
        this.zRotationRad = zRotation * GlobalConstants.TO_RADF;
        renderer.setZRotation(zRotation);
    }

    @Override
    public void setRenderer (Renderer renderer) {
        this.renderer = (PanoGLVortexRenderer) renderer;
        super.setRenderer(renderer);
    }

    public PanoGLSurfaceView(Activity activity, boolean enableSensorListener) {
        super(activity);
        if (enableSensorListener) sensorListener = new PolpeSensorListener(activity, this);
    }
    
    public void sensorNeeded (boolean needed) {
        if (needed) sensorListener.register();
        else sensorListener.unregister();
    }
}
