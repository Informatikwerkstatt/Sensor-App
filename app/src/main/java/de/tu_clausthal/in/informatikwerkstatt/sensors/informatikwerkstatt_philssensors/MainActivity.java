package de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors;

import android.annotation.SuppressLint;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.MessageFormat;

import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.CAccelerometer;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.CGyroscope;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.CLocation;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.IAccelerometerListener;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.IBaseSensor;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.IGyroscopeListener;
import de.tu_clausthal.in.informatikwerkstatt.sensors.informatikwerkstatt_philssensors.sensor.ILocationListener;

public class MainActivity extends AppCompatActivity implements ILocationListener, IGyroscopeListener, IAccelerometerListener {



    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // https://developer.android.com/guide/topics/ui/layout/listview
        final ListView l_sensors = findViewById(R.id.sensors);
        l_sensors.setAdapter(new ArrayAdapter<>(this, R.layout.sensorlist, IBaseSensor.sensors( this )) );

        ((Switch)findViewById(R.id.exist_gyroscope)).setChecked(CGyroscope.has(this));
        if (CGyroscope.has(this))
            new CGyroscope(this).register(this);

        ((Switch)findViewById(R.id.exist_accelerometer)).setChecked(CAccelerometer.has(this));
        if (CAccelerometer.has(this))
            new CAccelerometer(this, SensorManager.SENSOR_DELAY_UI).register(this);

        ((Switch)findViewById(R.id.exist_gps)).setChecked(CLocation.has(this));
        if (CLocation.has(this))
            try {
                new CLocation(this).register(this);
            } catch ( final IllegalAccessException ignored) { }

    }

    @Override
    public void gyroscope(final Number p_xaxis, final Number p_yaxis, final Number p_zaxis) {
        ((TextView)findViewById(R.id.text_gyroscop_xvalue)).setText( p_xaxis.toString() );
        ((TextView)findViewById(R.id.text_gyroscop_yvalue)).setText( p_yaxis.toString() );
        ((TextView)findViewById(R.id.text_gyroscop_zvalue)).setText( p_zaxis.toString() );
    }

    @Override
    public void accelerometer(final Number p_xaxis, final Number p_yaxis, final Number p_zaxis) {
        ((TextView)findViewById(R.id.text_accelerometer_xvalue)).setText( p_xaxis.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_xaxis ) );
        ((TextView)findViewById(R.id.text_accelerometer_yvalue)).setText( p_yaxis.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_yaxis ) );
        ((TextView)findViewById(R.id.text_accelerometer_zvalue)).setText( p_zaxis.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_zaxis ) );
    }

    @Override
    public void location(final Number p_latitude, final Number p_longitude, final Number p_altitude, final Number p_speed, final Number p_distance) {
        ((TextView)findViewById(R.id.text_gps_longitudevalue)).setText( p_longitude.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_longitude ) );
        ((TextView)findViewById(R.id.text_gps_latitudevalue)).setText( p_latitude.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_latitude ) );

        ((TextView)findViewById(R.id.text_gps_altitudevalue)).setText( p_altitude.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_altitude ) );
        ((TextView)findViewById(R.id.text_gps_speedvalue)).setText( p_speed.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_speed ) );
        ((TextView)findViewById(R.id.text_gps_distancevalue)).setText( p_distance.intValue() == 0 ? this.getString( R.string.nullnumber ) : MessageFormat.format( this.getString( R.string.numberformat ), p_distance ) );
    }
}
