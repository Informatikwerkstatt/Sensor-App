package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Interface für einen Gyroscope-Sensor-Listener
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public interface IGyroscopeListener extends ISensorListener {

    /**
     * Listener Aufruf
     *
     * @param p_xaxis relative Rotation in X-Richtung
     * @param p_yaxis relative Rotation in Y-Richtung
     * @param p_zaxis relative Rotation in Z-Richtung
     */
    void gyroscope(final Number p_xaxis, final Number p_yaxis, final Number p_zaxis);

}
