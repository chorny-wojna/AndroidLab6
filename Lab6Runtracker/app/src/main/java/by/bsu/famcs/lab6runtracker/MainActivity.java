package by.bsu.famcs.lab6runtracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvStart;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAltitude;
    private TextView tvElapsedTime;
    private Button btnStart;
    private Button btnStop;

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStart = (TextView) findViewById(R.id.tvStart);
        tvLatitude = (TextView) findViewById(R.id.tvLat);
        tvLongitude = (TextView) findViewById(R.id.tvLong);
        tvAltitude = (TextView) findViewById(R.id.tvAlt);
        tvElapsedTime = (TextView) findViewById(R.id.tvElapsed);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnStop.setEnabled(false);

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, intent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                pendingIntent);

        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        tvLatitude.setText(String.valueOf(loc.getLatitude()));
        tvLongitude.setText(String.valueOf(loc.getLongitude()));
        tvAltitude.setText(String.valueOf(loc.getAltitude()));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
                startTime = System.currentTimeMillis();
                tvStart.setText(sdf.format(startTime));
                tvElapsedTime.setText("");
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                long endTime = System.currentTimeMillis();
                tvElapsedTime.setText(Long.toString(endTime - startTime));
                break;
            default:

                break;
        }
    }

    public class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            Location loc = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);

            tvLatitude.setText(String.valueOf(loc.getLatitude()));
            tvLongitude.setText(String.valueOf(loc.getLongitude()));
            tvAltitude.setText(String.valueOf(loc.getAltitude()));
        }
    }
}
