package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

/**
 * Interface für einen Beschleunigung-Sensor-Listener
 */
public interface IAccelerometerListener extends ISensorListener {

    /**
     * Listener Aufruf
     *
     * @param p_xaxis relative Beschleunigung in X-Richtung
     * @param p_yaxis relative Beschleunigung in Y-Richtung
     * @param p_zaxis relative Beschleunigung in Z-Richtung
     */
    void accelerometer(final Number p_xaxis, final Number p_yaxis, final Number p_zaxis);

}
