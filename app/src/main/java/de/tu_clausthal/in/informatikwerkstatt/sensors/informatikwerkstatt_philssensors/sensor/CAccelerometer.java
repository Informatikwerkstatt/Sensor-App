package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Beschleunigungssensor
 *
 * @see https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */
public final class CAccelerometer extends IBaseSensor<IAccelerometerListener> implements SensorEventListener {
    /**
     * Genauigkeit
     */
    private final float m_epsilon;
    /**
     * letzter Beschleunigungsvektor
     */
    private final float[] m_last = new float[3];

    /**
     * Ctor
     *
     * @param p_parent aufrufende Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CAccelerometer(final Activity p_parent) {
        this(p_parent, SAMPLINGDEFAULT, 0.1f);
    }

    /**
     * Ctor
     *
     * @param p_parent aufrufende Activity
     * @param p_sampling Sampling Genauigkeit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CAccelerometer(final Activity p_parent, final int p_sampling) {
        this(p_parent, p_sampling, 0.1f);
    }

    /**
     * Ctor
     *
     * @param p_parent   aufrufende Activity
     * @param p_sampling Sampling Genauigkeit
     * @param p_epsilon  Werte-Genauigkeit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CAccelerometer(final Activity p_parent, final int p_sampling, final float p_epsilon) {
        super(p_parent, Sensor.TYPE_ACCELEROMETER);
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
        return has(p_parent, Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onSensorChanged(final SensorEvent p_event) {
        if (m_listener.isEmpty())
            return;

        final Number l_xaxis = filterequal(p_event.values[0] - m_last[0], 0, m_epsilon);
        final Number l_yaxis = filterequal(p_event.values[1] - m_last[1], 0, m_epsilon);
        final Number l_zaxis = filterequal(p_event.values[2] - m_last[2], 0, m_epsilon);

        System.arraycopy(p_event.values, 0, m_last, 0, p_event.values.length);

        m_listener.parallelStream()
                .forEach(i -> i.accelerometer(l_xaxis, l_yaxis, l_zaxis));
    }

    @Override
    public void onAccuracyChanged(final Sensor p_sensor, final int p_accuracy) {

    }

    @Override
    public ISensor<IAccelerometerListener> disable() {
        m_manager.unregisterListener(this);
        return this;
    }
}
