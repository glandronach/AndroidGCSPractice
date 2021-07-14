package com.viasofts.mygcs;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    MyDrone mMyDrone;
    NaverMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;

    LocationOverlay mLocationOverlay;
    private Boolean isReadyMap = false;

    Button mBtnConnect;
    TextView mTextViewBattery, mTextViewAltitude;
    Spinner mModeSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyDrone = new MyDrone(this);

        MyDrone.Callback callback = new MyDrone.Callback() {
            @Override
            public void setBatteryValue(double batteryValue) {
                mTextViewBattery.setText(String.valueOf(Math.round(batteryValue*100)/100.0) + 'V');
            }

            @Override
            public void setAltitude(double altitude) {
                mTextViewAltitude.setText(String.valueOf(Math.round(altitude*100)/100.0) + 'm');
            }

            @Override
            public void setVehicleMode(List<VehicleMode> vehicleModes) {
                ArrayAdapter<VehicleMode> vehicleModeArrayAdapter = new ArrayAdapter<VehicleMode>(MainActivity.this, android.R.layout.simple_spinner_item, vehicleModes);
                vehicleModeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mModeSelector.setAdapter(vehicleModeArrayAdapter);
            }
        };

        mMyDrone.setCallback(callback);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMyDrone.startDrone();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMyDrone.stopDrone();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mMap = naverMap;
        mMap.setMapType(NaverMap.MapType.Satellite);
        mLocationOverlay = naverMap.getLocationOverlay();
        mLocationOverlay.setVisible(true);
        mMap.setLocationSource(mLocationSource);
        mMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
        isReadyMap = true;

        mMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                if (isReadyMap) {
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(mLocationOverlay.getPosition());
                    mMap.moveCamera(cameraUpdate);
                    isReadyMap = false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated()) { // 권한 거부됨
                mMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mBtnConnect = findViewById(R.id.btnConnect);
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyDrone.isConnected()) {
                    mMyDrone.disconnect();
                } else {
                    ConnectionParameter connectionParams = ConnectionParameter.newUdpConnection(null);
                    mMyDrone.connect(connectionParams);
                }
            }
        });

        mTextViewBattery = findViewById(R.id.textViewBattery);
        mTextViewAltitude = findViewById(R.id.textViewAltitude);
        mModeSelector = findViewById(R.id.modeSelect);
    }
}