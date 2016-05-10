package com.polpe.panogl.sensorreadout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PolpeSensorListener implements SensorEventListener {

    private static final int ORIENTATION_PORTRAIT_INV = 30;
    private static final int ORIENTATION_LANDSCAPE_INV = 31;

    private static final int INTEGRATOR_SIZE = 5;

    float gx, gy, gz;
    float phi, theta, gamma;
    float angleX, angleY, angleZ;
    int orientation, accu, accu2;

    Integrator[] integrators;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private int ori = Configuration.ORIENTATION_LANDSCAPE;

    private OnRotationChangedListener rotationListener;

    private boolean registered = false;
    
    public PolpeSensorListener(Context context, OnRotationChangedListener rotationListener) {

        sensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        integrators = new Integrator[3];
        for (int i = 0; i < integrators.length; ++i) {
            integrators[i] = new Integrator(INTEGRATOR_SIZE);
        }

        register();

        if (rotationListener == null) throw new NullPointerException();
        this.rotationListener = rotationListener;
    }

    public void unregister () {
        if (!registered) return;
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
        registered = false;
    }

    public void register () {
        if (registered) return;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        registered = true;
    }

    @Override
    public void onSensorChanged (SensorEvent event) {

        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                gx = integrators[0].integrate(event.values[0]);
                gy = integrators[1].integrate(event.values[1]);
                gz = integrators[2].integrate(event.values[2]);
                float g = 9.8f;

                float szPg = Math.max(-1, Math.min(1, gz / g));

                phi = (float) Math.asin(szPg);

                float gCosPhi = (float) (g * Math.cos(phi));
                float sxPgCosPhi = Math.max(-1, Math.min(1, gx / gCosPhi));
                float syPgCosPhi = Math.max(-1, Math.min(1, gy / gCosPhi));

                float theta1 = (float) (Math.acos(sxPgCosPhi) * Math.signum(gy));
                float theta2 = (float) Math.asin(syPgCosPhi);

                theta = (float) (Math.abs(theta1) > 0.5 ? theta1 : theta2);

                phi = (float) (phi * 180.0 / Math.PI);
                theta = (float) (theta * 180.0 / Math.PI);

                calculateOrientation(theta);
                rotationListener.onZRotationChanged(theta);
                rotationListener.onYRotationChanged(phi);

                orientation = ori;

                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                float[] grav = { integrators[0].getMean(), integrators[1].getMean(), integrators[2].getMean() };
                float[] R = new float[9],
                I = new float[9];
                SensorManager.getRotationMatrix(R, I, grav, event.values);

                // angleX = R[1];
                // angleY = R[4];
                // angleZ = R[7];

                float gamma = 0.0f;
                switch (ori)
                {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        gamma = (float) (Math.acos(R[1]) * Math.signum(R[4]));
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:
                        gamma = (float) (Math.acos(R[0]) * Math.signum(R[3]));
                        break;
                    case ORIENTATION_PORTRAIT_INV:
                        gamma = (float) (Math.acos(R[0]) * Math.signum(R[3]) + Math.PI);
                        break;
                    case ORIENTATION_LANDSCAPE_INV:
                        gamma = (float) (Math.acos(R[1]) * Math.signum(R[4]) + Math.PI);
                        break;
                }
                this.gamma = (float) (gamma * 180.0 / Math.PI);

                float[] angles = new float[4];
                SensorManager.getOrientation(R, angles);

                angleX = angles[0];
                angleY = angles[1];
                angleZ = angles[2];

                break;
        }

    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {
        switch (sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                accu = accuracy;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                accu2 = accuracy;

            default:
                break;
        }
    }

    private void calculateOrientation (float theta) {
        if (ori != Configuration.ORIENTATION_PORTRAIT) {
            if (theta > 60.0 && theta < 120) {
                ori = Configuration.ORIENTATION_PORTRAIT;
                return;
            }
        }
        if (ori != Configuration.ORIENTATION_LANDSCAPE) {
            if (Math.abs(theta) < 30) {
                ori = Configuration.ORIENTATION_LANDSCAPE;
                return;
            }
        }
        if (ori != ORIENTATION_PORTRAIT_INV) {
            if (-120 < theta && theta < -60) {
                ori = ORIENTATION_PORTRAIT_INV;
                return;
            }
        }
        if (ori != ORIENTATION_LANDSCAPE_INV) {
            if (Math.abs(theta) > 150) {
                ori = ORIENTATION_LANDSCAPE_INV;
            }
        }
    }
}
