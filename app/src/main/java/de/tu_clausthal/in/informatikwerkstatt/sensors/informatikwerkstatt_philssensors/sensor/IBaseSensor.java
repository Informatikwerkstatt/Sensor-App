package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static android.content.Context.SENSOR_SERVICE;

/**
 * abstrakte Klasse für die Basisfunktionalität
 *
 * @tparam T Listener type
 * @see https://developer.android.com/guide/topics/manifest/uses-feature-element#hw-features
 * @see https://developer.android.com/guide/topics/sensors/sensors_motion
 */
public abstract class IBaseSensor<T extends ISensorListener> implements ISensor<T> {
    /**
     * Multiplikator für Nano-Sekunden in Sekunden
     */
    static final float NS2S = 1.0f / 1000000000.0f;
    /**
     * Standard Sampling Rate für den Sensor
     */
    static final int SAMPLINGDEFAULT = SensorManager.SENSOR_DELAY_GAME;
    /**
     * Sensormanager
     */
    final SensorManager m_manager;
    /**
     * Sensorarray
     */
    final Sensor[] m_sensors;
    /**
     * Listener
     */
    Set<T> m_listener = new CopyOnWriteArraySet<>();

    /**
     * Ctor
     *
     * @param p_parent  aufrufende Activity
     * @param p_sensors Sensor-Typ
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    IBaseSensor(final Activity p_parent, final Integer... p_sensors) {
        m_manager = (SensorManager) p_parent.getSystemService(SENSOR_SERVICE);
        m_sensors = Arrays.stream(p_sensors).map(i -> Objects.requireNonNull(m_manager).getDefaultSensor(i)).toArray(Sensor[]::new);
    }

    /**
     * prüft, ob ein Sensor existiert
     *
     * @param p_parent aufrufende Activity
     * @param p_sensor Sensor-Typ
     * @return Existenz-Flag
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    static boolean has(final Activity p_parent, final int p_sensor) {
        final SensorManager l_manager = (SensorManager) p_parent.getSystemService(SENSOR_SERVICE);
        return Objects.nonNull(Objects.requireNonNull(l_manager).getDefaultSensor(p_sensor));
    }

    /**
     * liefert eine Liste der Sensoren
     *
     * @param p_parent aufrufende Activity
     * @return Liste mit Sensornamen
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> sensors(final Activity p_parent) {
        final SensorManager l_manager = (SensorManager) p_parent.getSystemService(SENSOR_SERVICE);
        return Objects.requireNonNull(l_manager)
                .getSensorList(Sensor.TYPE_ALL)
                .stream()
                .map(Sensor::getName)
                .collect(Collectors.toList());
    }

    /**
     * Floating-Point Equals
     *
     * @param p_value     Wert
     * @param p_compareto zu überprüfender Wert
     * @param p_epsilon   Genauigkeit
     * @return Equality Flag
     */
    static boolean isequal(final Number p_value, final Number p_compareto, final Number p_epsilon) {
        return Math.abs(p_value.doubleValue() - p_compareto.doubleValue()) < p_epsilon.doubleValue();
    }

    /**
     * erzeugt anhand der Genauigkeit den definierten Wert
     *
     * @param p_value     Wert
     * @param p_compareto zu überprüfender Wert
     * @param p_epsilon   Genauigkeit
     * @return Wert oder überprüfter Wert
     */
    static Number filterequal(final Number p_value, final Number p_compareto, final Number p_epsilon) {
        return isequal(p_value, p_compareto, p_epsilon) ? p_compareto : p_value;
    }

    /**
     * setzt in einem Float-Array alle Werte, die Genauigkeit erreichen
     *
     * @param p_array     Array
     * @param p_compareto zu vergleichender Wert
     * @param p_epsilon   Genauigkeit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    static void setvalue(final float[] p_array, final Number p_compareto, final Number p_epsilon) {
        IntStream.range(0, p_array.length)
                .forEach(i -> p_array[i] = isequal(p_array[i], 0, p_epsilon) ? 0f : p_array[i]);

    }

    @Override
    public final ISensor<T> register(final T p_listener) {
        m_listener.add(p_listener);
        return this;
    }

    @Override
    public final ISensor<T> unregister(final T p_listener) {
        m_listener.remove(p_listener);
        return this;
    }
}
