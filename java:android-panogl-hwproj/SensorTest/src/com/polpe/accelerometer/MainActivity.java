package com.polpe.accelerometer;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private static final int ORIENTATION_PORTRAIT_INV = 30;
    private static final int ORIENTATION_LANDSCAPE_INV = 31;

    private static final int INTEGRATOR_SIZE = 5;

    TextView val0, val1, val2, accu, accu2, orientation, phi, theta, gamma, angleX, angleY, angleZ;

    Integrator[] integrators;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private int ori = Configuration.ORIENTATION_LANDSCAPE;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        integrators = new Integrator[3];
        for (int i = 0; i < integrators.length; ++i) {
            integrators[i] = new Integrator(INTEGRATOR_SIZE);
        }

        val0 = (TextView) findViewById(R.id.val0);
        val1 = (TextView) findViewById(R.id.val1);
        val2 = (TextView) findViewById(R.id.val2);
        accu = (TextView) findViewById(R.id.accuracy);
        accu2 = (TextView) findViewById(R.id.accuracy_2);
        phi = (TextView) findViewById(R.id.angle_phi_val);
        theta = (TextView) findViewById(R.id.angle_theta_val);
        gamma = (TextView) findViewById(R.id.angl_gamma_val);
        angleX = (TextView) findViewById(R.id.angle_0_val);
        angleY = (TextView) findViewById(R.id.angle_1_val);
        angleZ = (TextView) findViewById(R.id.angle_2_val);
        orientation = (TextView) findViewById(R.id.orientation_val);
    }

    @Override
    protected void onResume () {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSensorChanged (SensorEvent event) {

        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                float sx = integrators[0].integrate(event.values[0]);
                float sy = integrators[1].integrate(event.values[1]);
                float sz = integrators[2].integrate(event.values[2]);
                float g = 9.8f;

                float szPg = Math.max(-1, Math.min(1, sz / g));

                val0.setText(String.valueOf(sx));
                val1.setText(String.valueOf(sy));
                val2.setText(String.valueOf(sz));

                double phi = Math.asin(szPg);

                double gCosPhi = g * Math.cos(phi);
                double sxPgCosPhi = Math.max(-1, Math.min(1, sx / gCosPhi));
                double syPgCosPhi = Math.max(-1, Math.min(1, sy / gCosPhi));

                double theta1 = Math.acos(sxPgCosPhi) * Math.signum(sy);
                double theta2 = Math.asin(syPgCosPhi);

                double theta = Math.abs(theta1) > 0.5 ? theta1 : theta2;

                phi = phi * 180.0 / Math.PI;
                theta = theta * 180.0 / Math.PI;

                calculateOrientation(theta);
                orientation.setText(getOrientationFromEnum(ori));
                // orientation.setText(getOrientationFromEnum(getResources().getConfiguration().orientation));

                this.phi.setText(String.valueOf(phi));
                this.theta.setText(String.valueOf(theta));
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                float[] grav = { integrators[0].getMean(), integrators[1].getMean(), integrators[2].getMean() };
                float[] R = new float[9],
                I = new float[9];
                SensorManager.getRotationMatrix(R, I, grav, event.values);

                // angleX.setText(String.valueOf(R[1]));
                // angleY.setText(String.valueOf(R[4]));
                // angleZ.setText(String.valueOf(R[7]));

                double gamma = 0.0;
                switch (ori) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        gamma = Math.acos(R[1]) * Math.signum(R[4]);
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:
                         gamma = Math.acos(R[0]) * Math.signum(R[3]);
                        break;
                    case ORIENTATION_PORTRAIT_INV:
                        gamma = Math.acos(R[0]) * Math.signum(R[3]) + Math.PI;
                        break;
                    case ORIENTATION_LANDSCAPE_INV:
                        gamma = Math.acos(R[1]) * Math.signum(R[4]) + Math.PI;
                        break;
                }
                this.gamma.setText(String.valueOf(gamma * 180.0 / Math.PI));

                float[] angles = new float[4];
                SensorManager.getOrientation(R, angles);

                angleX.setText(String.valueOf(angles[0]));
                angleY.setText(String.valueOf(angles[1]));
                angleZ.setText(String.valueOf(angles[2]));

                break;
        }

    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {
        switch (sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                accu.setText(getAccuracyFromEnum(accuracy));
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                accu2.setText(getAccuracyFromEnum(accuracy));

            default:
                break;
        }
    }

    private void calculateOrientation (double theta) {
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

    private String getOrientationFromEnum (int orientation) {
        switch (orientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                return "LANDSCAPE";

            case Configuration.ORIENTATION_PORTRAIT:
                return "PORTRAIT";

            case ORIENTATION_LANDSCAPE_INV:
                return "I_LANDSCAPE";

            case ORIENTATION_PORTRAIT_INV:
                return "I_PORTRAIT";

            default:
                return null;
        }
    }

    private String getAccuracyFromEnum (int accuracy) {
        switch (accuracy)
        {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                return "HIGH";

            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                return "MEDIUM";

            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                return "LOW";

            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                return "UNRELIABLE";

            default:
                return null;
        }
    }

}
