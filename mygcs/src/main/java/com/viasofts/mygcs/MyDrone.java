package com.viasofts.mygcs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.LinkListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.companion.solo.SoloAttributes;
import com.o3dr.services.android.lib.drone.companion.solo.SoloState;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.gcs.link.LinkConnectionStatus;

public class MyDrone implements DroneListener, TowerListener, LinkListener {
    private Drone mDrone;
    private int mDroneType = Type.TYPE_UNKNOWN;
    private ControlTower mControlTower;
    private final Handler mHandler = new Handler();
    private MainActivity mMainActivity;
    private String TAG = "MyLog";

    private static final int DEFAULT_UDP_PORT = 14550;
    private static final int DEFAULT_USB_BAUD_RATE = 57600;

    public MyDrone(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mControlTower = new ControlTower(mMainActivity);
        mDrone = new Drone(mMainActivity);
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

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone Disconnected");
                break;

            case AttributeEvent.STATE_UPDATED:
            case AttributeEvent.STATE_ARMING:
                break;

            case AttributeEvent.TYPE_UPDATED:
                Type newDroneType = mDrone.getAttribute(AttributeType.TYPE);
                if (newDroneType.getDroneType() != this.mDroneType) {
                    mDroneType = newDroneType.getDroneType();
                }
                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                break;

            case AttributeEvent.SPEED_UPDATED:
                break;

            case AttributeEvent.ALTITUDE_UPDATED:
                break;

            case AttributeEvent.HOME_UPDATED:
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
        this.mControlTower.registerDrone(mDrone, mHandler);
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
}
