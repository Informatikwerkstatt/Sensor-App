package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GPS Sensor
 *
 * @see https://developer.android.com/reference/android/os/Handler
 * @see https://developer.android.com/training/permissions/requesting
 */
public final class CLocation implements ISensor<ILocationListener>, LocationListener {
    /**
     * GPS Provider
     */
    private static final String PROVIDER = LocationManager.GPS_PROVIDER;
    /**
     * notwendige Permissions
     */
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    /**
     * Listener
     */
    private final Set<ILocationListener> m_listener = new CopyOnWriteArraySet<>();
    /**
     * Boolean Flag um Timer- zu beenden
     */
    private final AtomicBoolean m_isrunning = new AtomicBoolean(true);
    /**
     * letzte Position
     */
    private Location m_lastlocation;


    // https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android
    // https://wangjingke.com/2016/09/23/Multiple-ways-to-schedule-repeated-tasks-in-android

    // https://developer.android.com/reference/android/os/Handler
    // https://developer.android.com/reference/java/util/Timer
    // https://stackoverflow.com/questions/41511698/handler-postdelayed-print-every-second-something/41550971

    /**
     * ctor
     *
     * @param p_parent   Activity
     * @throws IllegalAccessException wird bei Initisalisiierungsfehler geworfen
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CLocation(final Activity p_parent) throws IllegalAccessException {
        this(p_parent,1000);
    }

    /**
     * ctor
     *
     * @param p_parent Activity
     * @param p_time Minimum Zeitintervall in Milisekunden zwischen Location-Updates
     * @throws IllegalAccessException wird bei Initisalisiierungsfehler geworfen
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CLocation(final Activity p_parent, final long p_time) throws IllegalAccessException {
        if (!permission(p_parent)) {
            Log.e(this.getClass().getCanonicalName(), "Berechtigung für GPS nicht vorhanden");
            throw new IllegalAccessException("Berechtigung für GPS nicht vorhanden");
        }

        final LocationManager l_locationmanager = (LocationManager) p_parent.getSystemService(Context.LOCATION_SERVICE);

        final Handler l_handler = new Handler();
        l_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Location l_location = l_locationmanager.getLastKnownLocation(PROVIDER);

                m_listener.parallelStream()
                        .forEach(i -> i.location(
                                l_location.getLatitude(),
                                l_location.getLongitude(),
                                l_location.hasAltitude() ? l_location.getAltitude() : 0,
                                l_location.hasSpeed() ? l_location.getSpeed() : 0,
                                Objects.nonNull(m_lastlocation) ? l_location.distanceTo(m_lastlocation) : 0
                        ));

                m_lastlocation = l_location;
                if ( m_isrunning.get() )
                    l_handler.postDelayed( this, p_time );

            }
        }, p_time);
    }

    /**
     * überprüft, ob ein GPS vorhanden ist
     *
     * @param p_parent Activity
     * @return Existenz
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean has(final Activity p_parent ) {
        if (!permission(p_parent))
            return false;

        final LocationManager l_locationmanager = (LocationManager) p_parent.getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(l_locationmanager).isProviderEnabled(PROVIDER);
    }

    /**
     * erfragt vom Benutzer die entsprechende Permission
     *
     * @param p_parent Activity
     * @return Berechtigung garantiert
     * @warning das Erzeugen des Zugriffs muss blockierend funktionieren
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static boolean permission(final Activity p_parent) {
        return Arrays.stream(PERMISSIONS).allMatch( i -> checkpermission(p_parent, i) );
    }

    /**
     * check einzelne Permission
     *
     * @param p_parent p_Activity
     * @param p_permission zu überprüfende Permission
     * @return Berechtigung garantiert
     */
    private static boolean checkpermission(final Activity p_parent, final String p_permission)
    {
        if (p_parent.checkSelfPermission(p_permission) == PackageManager.PERMISSION_GRANTED)
            return true;

        if (!p_parent.shouldShowRequestPermissionRationale(p_permission)) {
            p_parent.requestPermissions(new String[]{p_permission}, 22);

            while (p_parent.checkSelfPermission(p_permission) != PackageManager.PERMISSION_GRANTED) {
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException l_exception) {
                    Log.e("permission", l_exception.toString());
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ISensor<ILocationListener> register(final ILocationListener p_listener) {
        m_listener.add(p_listener);
        return this;
    }

    @Override
    public ISensor<ILocationListener> unregister(final ILocationListener p_listener) {
        m_listener.remove(p_listener);
        return this;
    }

    @Override
    public ISensor<ILocationListener> disable() {
        m_isrunning.set(false);
        return this;
    }

    @Override
    public void onLocationChanged(final Location p_location) {
    }

    @Override
    public void onStatusChanged(final String p_provider, final int p_status, final Bundle p_extras) {

    }

    @Override
    public void onProviderEnabled(final String p_provider) {

    }

    @Override
    public void onProviderDisabled(final String p_provider) {

    }

}
