package io.github.kahenteikou.speedmetergps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private GnssStatus.Callback gnssCallback;
    private TextView txView;
    private TextView GPSStatusLabelView;
    private ProgressBar barkun;
    private TextView AccView;
    private float speedkun=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        txView=(TextView) findViewById(R.id.SpeedTextView);
        AccView=(TextView)findViewById(R.id.ACCURACYVIEW);
        barkun=(ProgressBar) findViewById(R.id.progressBar);
        GPSStatusLabelView=(TextView) findViewById(R.id.GPSStatusLabel);
        gnssCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                int usedInFixCount = 0;
                for (int i = 0; i < status.getSatelliteCount(); i++) {
                    if (status.usedInFix(i)) {
                        usedInFixCount++;
                    }
                }
                GPSStatusLabelView.setText(String.format("GPS:%02d/%02d",usedInFixCount,status.getSatelliteCount()));
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        locationManager.unregisterGnssStatusCallback(gnssCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        locationManager.registerGnssStatusCallback(gnssCallback);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location.hasSpeed()) {
            speedkun = location.getSpeed() * 3.6f;
        }else{
                speedkun=0.0f;
        }
        txView.setText(String.format("%5.1f",speedkun).replace(" ","!"));
        barkun.setProgress((int)(speedkun*100.0f));
        if(location.hasAccuracy()) {
            AccView.setText(String.format("ACCURACY:%03dM", (int)location.getAccuracy()));
        }else{
            AccView.setText("ACCURACY:NONE");
        }

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}