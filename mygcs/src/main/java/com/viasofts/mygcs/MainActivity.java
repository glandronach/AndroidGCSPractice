package com.viasofts.mygcs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.viasofts.mygcs.activities.helpers.BluetoothDevicesActivity;
import com.viasofts.mygcs.utils.TLogUtils;
import com.viasofts.mygcs.utils.prefs.DroidPlannerPrefs;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    MyDrone mMyDrone;
    NaverMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;

    LocationOverlay mLocationOverlay;
    private Boolean isReadyMap = false;

    Button mBtnConnect, mBtnSetConnectionType;
    TextView mTextViewBattery, mTextViewAltitude;
    Spinner mModeSelector;

    private DroidPlannerPrefs mPrefs;
    private static final long EVENTS_DISPATCHING_PERIOD = 200L; //MS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(getApplicationContext().MODE_PRIVATE);
        SharedPrefManager.getInstance().init(sharedPref);
        mPrefs = DroidPlannerPrefs.getInstance(getApplicationContext());


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

            @Override
            public void editVehicleMode(VehicleMode vehicleMode) {
                ArrayAdapter arrayAdapter = (ArrayAdapter) mModeSelector.getAdapter();
                mModeSelector.setSelection(arrayAdapter.getPosition(vehicleMode));
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
                    //mMyDrone.connect(connectionParams);
                    mMyDrone.connect(retrieveConnectionParameters());
                }
            }
        });

        mTextViewBattery = findViewById(R.id.textViewBattery);
        mTextViewAltitude = findViewById(R.id.textViewAltitude);

        mModeSelector = findViewById(R.id.modeSelect);
        mModeSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMyDrone.onFlightModeSelected(mModeSelector.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mBtnSetConnectionType = findViewById(R.id.button_set_conn_type);
        final String[] types = getResources().getStringArray(R.array.connection_type);
        mBtnSetConnectionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setTitle("SET CONNECTION TYPE")
                        .setItems(R.array.connection_type, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String value = types[id];
                                SharedPrefManager.save(SharedPrefManager.KEY_CONNECTION, value);
                                mBtnSetConnectionType.setText(value);
                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        String connectionType = SharedPrefManager.read(SharedPrefManager.KEY_CONNECTION);
        if (connectionType.equals("")) {
            connectionType = types[0];
        }

        mBtnSetConnectionType.setText(connectionType);
    }

    private ConnectionParameter retrieveConnectionParameters() {
        final @ConnectionType.Type int connectionType = mPrefs.getConnectionParameterType();

        // Generate the uri for logging the tlog data for this session.
        Uri tlogLoggingUri = TLogUtils.getTLogLoggingUri(getApplicationContext(),
                connectionType, System.currentTimeMillis());

        int idx = getConnectionTypeIdx();

        ConnectionParameter connParams;
        //switch (connectionType) {
        //switch (ConnectionType.TYPE_BLUETOOTH) {
        switch (idx) {
            case ConnectionType.TYPE_USB:
                connParams = ConnectionParameter.newUsbConnection(mPrefs.getUsbBaudRate(),
                        tlogLoggingUri, EVENTS_DISPATCHING_PERIOD);
                break;

            case ConnectionType.TYPE_UDP:
                if (mPrefs.isUdpPingEnabled()) {
                    connParams = ConnectionParameter.newUdpWithPingConnection(
                            mPrefs.getUdpServerPort(),
                            mPrefs.getUdpPingReceiverIp(),
                            mPrefs.getUdpPingReceiverPort(),
                            "Hello".getBytes(),
                            ConnectionType.DEFAULT_UDP_PING_PERIOD,
                            tlogLoggingUri,
                            EVENTS_DISPATCHING_PERIOD);
                } else {
                    connParams = ConnectionParameter.newUdpConnection(mPrefs.getUdpServerPort(),
                            tlogLoggingUri, EVENTS_DISPATCHING_PERIOD);
                }
                break;

            case ConnectionType.TYPE_TCP:
                connParams = ConnectionParameter.newTcpConnection(mPrefs.getTcpServerIp(),
                        mPrefs.getTcpServerPort(), tlogLoggingUri, EVENTS_DISPATCHING_PERIOD);
                break;

            case ConnectionType.TYPE_BLUETOOTH:
                String btAddress = mPrefs.getBluetoothDeviceAddress();

                if (TextUtils.isEmpty(btAddress)) {
                    connParams = null;
                    startActivity(new Intent(getApplicationContext(), BluetoothDevicesActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                } else {
                    connParams = ConnectionParameter.newBluetoothConnection(btAddress,
                            tlogLoggingUri, EVENTS_DISPATCHING_PERIOD);
                }
                break;

            default:
                Log.e("myLog", "Unrecognized connection type: " + connectionType);
                connParams = null;
                break;
        }

        return connParams;
    }

    public int getConnectionTypeIdx() {
        final String[] types = getResources().getStringArray(R.array.connection_type);
        String strConn = mBtnSetConnectionType.getText().toString();

        int idx = 3;
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(strConn)) {
                return i;
            }
        }

        return idx;
    }
}