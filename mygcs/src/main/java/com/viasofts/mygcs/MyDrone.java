package com.viasofts.mygcs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.LinkListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.companion.solo.SoloAttributes;
import com.o3dr.services.android.lib.drone.companion.solo.SoloState;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Attitude;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.GuidedState;
import com.o3dr.services.android.lib.drone.property.Home;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.gcs.link.LinkConnectionStatus;
import com.o3dr.services.android.lib.model.AbstractCommandListener;

import java.util.List;

public class MyDrone implements DroneListener, TowerListener, LinkListener {
    private Drone mDrone;
    private int mDroneType = Type.TYPE_UNKNOWN;
    private ControlTower mControlTower;
    private final Handler mHandler = new Handler();
    private MainActivity mMainActivity;
    private String TAG = "MyLog";
    private boolean mCondition;
    private Callback mCallback;

    interface Callback {
        void setBatteryValue(double batteryValue);
        void setAltitude(double altitude);
        void setVehicleMode(List<VehicleMode> vehicleModes);
        void editVehicleMode(VehicleMode vehicleMode);
    }

    public void connectTower() {
        mControlTower.connect(this);

    }

    public MyDrone(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mControlTower = new ControlTower(mMainActivity);
        mDrone = new Drone(mMainActivity);

        mCondition = false;
        mCallback = null;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
        mCondition = true;
    }

    public Boolean isConnected() {
        return mDrone.isConnected();
    }

    public void disconnect() {
        mDrone.disconnect();
    }

    public void connect(ConnectionParameter connectionParameter) {
        mDrone.connect(connectionParameter);
    }

    public void startDrone() {
        mControlTower.connect(this);
    }

    public void stopDrone() {
        if (mDrone.isConnected()) {
            mDrone.disconnect();
        }

        mControlTower.unregisterDrone(mDrone);
        mControlTower.disconnect();
    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                alertUser("Drone Connected");
                checkSoloState();
                break;

            case AttributeEvent.ATTITUDE_UPDATED:
                Attitude attitude = mDrone.getAttribute(AttributeType.ATTITUDE);
                attitude.getYaw();

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone Disconnected");
                break;

            case AttributeEvent.STATE_UPDATED:
                break;

            case AttributeEvent.STATE_ARMING:
                break;

            case AttributeEvent.TYPE_UPDATED:
                Type newDroneType = mDrone.getAttribute(AttributeType.TYPE);
                if (newDroneType.getDroneType() != mDroneType) {
                    mDroneType = newDroneType.getDroneType();
                    updateVehicleModesForType(mDroneType);
                }
                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                State vehicleState = mDrone.getAttribute(AttributeType.STATE);
                VehicleMode vehicleMode = vehicleState.getVehicleMode();
                mCallback.editVehicleMode(vehicleMode);
                break;

            case AttributeEvent.SPEED_UPDATED:
                break;

            case AttributeEvent.ALTITUDE_UPDATED:
                if(mCondition && (mCallback != null)) {
                    Altitude altitude = mDrone.getAttribute(AttributeType.ALTITUDE);
                    mCallback.setAltitude(altitude.getAltitude());
                }
                break;

            case AttributeEvent.HOME_UPDATED:
                Home home = mDrone.getAttribute((AttributeType.HOME));
                LatLongAlt latLongAlt = home.getCoordinate();

                break;

            case AttributeEvent.BATTERY_UPDATED:
                if(mCondition && (mCallback != null)) {
                    Battery droneBattery = mDrone.getAttribute(AttributeType.BATTERY);
                    mCallback.setBatteryValue(droneBattery.getBatteryVoltage());
                }
                break;

            default:
                // Log.i("DRONE_EVENT", event); //Uncomment to see events from the drone
                break;
        }
    }

    private void checkSoloState() {
        final SoloState soloState = mDrone.getAttribute(SoloAttributes.SOLO_STATE);
        if (soloState == null){
            alertUser("Unable to retrieve the solo state.");
        }
        else {
            alertUser("Solo state is up to date.");
        }
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {

    }

    @Override
    public void onLinkStateUpdated(@NonNull LinkConnectionStatus connectionStatus) {

    }

    @Override
    public void onTowerConnected() {
        alertUser("DroneKit-Android Connected");
        mControlTower.registerDrone(mDrone, mHandler);
        mDrone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {
        alertUser("DroneKit-Android Interrupted");
    }

    protected void alertUser(String message) {
        Toast.makeText(mMainActivity, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }

    protected void updateVehicleModesForType(int droneType) {
        List<VehicleMode> vehicleModes = VehicleMode.getVehicleModePerDroneType(droneType);
        mCallback.setVehicleMode(vehicleModes);
    }

    public void onFlightModeSelected(Object vehicleMode) {
        VehicleApi.getApi(mDrone).setVehicleMode((VehicleMode) vehicleMode, new AbstractCommandListener() {
            @Override
            public void onSuccess() {
                alertUser("Vehicle mode change successful.");
            }

            @Override
            public void onError(int executionError) {
                alertUser("Vehicle mode change failed: " + executionError);
            }

            @Override
            public void onTimeout() {
                alertUser("Vehicle mode change timed out.");
            }
        });
    }
}
