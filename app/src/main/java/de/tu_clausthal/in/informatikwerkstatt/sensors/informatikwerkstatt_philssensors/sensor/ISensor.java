package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

/**
 * Interface für einen Sensor
 *
 * @tparam T Sensor Listener Typ
 */
public interface ISensor<T extends ISensorListener> {

    /**
     * registert einen Listener
     *
     * @param p_listener listener
     * @return Selbst-Referenz
     */
    ISensor<T> register(final T p_listener);

    /**
     * unregistriert einen Listener
     *
     * @param p_listener Listener
     * @return Selbst-Referenz
     */
    ISensor<T> unregister(final T p_listener);

    /**
     * stoppt den Sensor wenn möglich
     *
     * @return Selbst-Referenz
     */
    ISensor<T> disable();
}
