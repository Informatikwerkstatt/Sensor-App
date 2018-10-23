package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

/**
 * Interface für einen GPS-Positions Listener
 */
public interface ILocationListener extends ISensorListener {

    /**
     * liefert die aktuelle Position mit weiteren Meta-Daten
     *
     * @param p_latitude  Breitengrad im Gradmaß
     * @param p_longitude Längengrad im Gradmaß
     * @param p_altitude  Höhe in Meter, sofern es im Gerät gemessen wird, ansonsten 0
     * @param p_speed     Geschwindigkeit in m/s, sofern es im Gerät gemessen wird, ansonsten 0
     * @param p_distance  Distanz zur vergangenen Position
     */
    void location(final Number p_latitude, final Number p_longitude, final Number p_altitude, final Number p_speed, final Number p_distance);

}
