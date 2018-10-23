package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Klasse für den Gyroskopsensor
 *
 * @see https://developer.android.com/guide/topics/sensors/sensors_motion
 * @see https://stackoverflow.com/questions/14310347/gyroscope-issues-with-device-orientation
 * @see http://plaw.info/articles/sensorfusion/
 */
public final class CGyroscope extends IBaseSensor<IGyroscopeListener> implements SensorEventListener {
    /**
     * Genauigkeit
     */
    private final float m_epsilon;
    /**
     * Rotationsmatrix
     */
    private final float[] m_rotationmatrix = new float[9];
    /**
     * Rotationsvektor
     */
    private final float[] m_rotationvector = new float[4];
    /**
     * letzter Timestep
     */
    private float m_timestamp;

    /**
     * Ctor
     *
     * @param p_parent aufrufende Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CGyroscope(final Activity p_parent) {
        this(p_parent, SAMPLINGDEFAULT, 0.001f);
    }

    /**
     * Ctor
     *
     * @param p_parent   aufrufende Activity
     * @param p_sampling Sampling Genauigkeit
     * @param p_epsilon  Werte-Genauigkeit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CGyroscope(final Activity p_parent, final int p_sampling, final float p_epsilon) {
        super(p_parent, Sensor.TYPE_GYROSCOPE);
        m_epsilon = p_epsilon;
        m_manager.registerListener(this, m_sensors[0], p_sampling);
    }

    /**
     * Überprüfung ob Sensor vorhanden ist
     *
     * @param p_parent aufrufende Activtiy
     * @return Existenz-Flag
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean has(final Activity p_parent) {
        return has(p_parent, Sensor.TYPE_GYROSCOPE);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSensorChanged(final SensorEvent p_event) {
        if (m_listener.isEmpty())
            return;

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (m_timestamp != 0) {
            final float l_dt = (p_event.timestamp - m_timestamp) * NS2S;

            // Axis of the rotation sample, not normalized yet.
            float l_xaxis = p_event.values[0];
            float l_yaxis = p_event.values[1];
            float l_zaxis = p_event.values[2];

            // Calculate the angular speed of the sample
            final float l_omegamagnitude = (float) Math.sqrt(l_xaxis * l_xaxis + l_yaxis * l_yaxis + l_zaxis * l_zaxis);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, m_epsilon should represent your maximum allowable margin of error)
            if (l_omegamagnitude > m_epsilon) {
                l_xaxis /= l_omegamagnitude;
                l_yaxis /= l_omegamagnitude;
                l_zaxis /= l_omegamagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            final float l_thetaovertwo = l_omegamagnitude * l_dt / 2.0f;
            final float l_sinthetaovertwo = (float) Math.sin(l_thetaovertwo);

            m_rotationvector[0] = l_sinthetaovertwo * l_xaxis;
            m_rotationvector[1] = l_sinthetaovertwo * l_yaxis;
            m_rotationvector[2] = l_sinthetaovertwo * l_zaxis;
            m_rotationvector[3] = (float) Math.cos(l_thetaovertwo);
        }
        m_timestamp = p_event.timestamp;

        SensorManager.getRotationMatrixFromVector(m_rotationmatrix, m_rotationvector);
        final float[] l_currentrotation = SensorManager.getOrientation(m_rotationmatrix, p_event.values);
        setvalue(l_currentrotation, 0, m_epsilon);

        m_listener.parallelStream()
                .forEach(i -> i.gyroscope(l_currentrotation[0], l_currentrotation[1], l_currentrotation[2]));
    }

    @Override
    public void onAccuracyChanged(final Sensor p_sensor, final int p_accuracy) {

    }

    @Override
    public ISensor<IGyroscopeListener> disable() {
        m_manager.unregisterListener(this);
        return this;
    }
}
