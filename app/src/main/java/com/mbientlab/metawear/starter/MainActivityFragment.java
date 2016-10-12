/*
 * Copyright 2015 MbientLab Inc. All rights reserved.
 *
 * IMPORTANT: Your use of this Software is limited to those specific rights
 * granted under the terms of a software license agreement between the user who
 * downloaded the software, his/her employer (which must be your employer) and
 * MbientLab Inc, (the "License").  You may not use this Software unless you
 * agree to abide by the terms of the License which can be found at
 * www.mbientlab.com/terms . The License limits your use, and you acknowledge,
 * that the  Software may not be modified, copied or distributed and can be used
 * solely and exclusively in conjunction with a MbientLab Inc, product.  Other
 * than for the foregoing purpose, you may not use, reproduce, copy, prepare
 * derivative works of, modify, distribute, perform, display or sell this
 * Software and/or its documentation for any purpose.
 *
 * YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
 * PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
 * MBIENTLAB OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT, NEGLIGENCE,
 * STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE
 * THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED
 * TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST
 * PROFITS OR LOST DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY,
 * SERVICES, OR ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY
 * DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 *
 * Should you have any questions regarding your right to use this Software,
 * contact MbientLab Inc, at www.mbientlab.com.
 */

package com.mbientlab.metawear.starter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.MultiChannelTemperature;
import com.mbientlab.metawear.module.MultiChannelTemperature.*;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements ServiceConnection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public interface FragmentSettings {
        BluetoothDevice getBtDevice();
    }

    private MetaWearBoard mwBoard = null;
    private FragmentSettings settings;
    private MultiChannelTemperature mcTempModule;
    private List<MultiChannelTemperature.Source> tempSources;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Timer timer;
    private GetTemperature getTemp;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitudeText;
    private String mLongitudeText;
    private Float temperature;
    private Integer timeLimit, tempLimit;
    private Boolean alert, vibrate;
    protected static String sTemp;
    private boolean hasStarted = false;
    private ComponentName name;
    private IBinder service;

    private static final String allTemp = "allTemp.text";
    private static final String belowTemp = "belowTemp.text";
    private static final String separator = System.getProperty("line.separator");

    private ImageView statusIcon;



    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity owner = getActivity();
        if (!(owner instanceof FragmentSettings)) {
            throw new ClassCastException("Owning activity must implement the FragmentSettings interface");
        }

        settings = (FragmentSettings) owner;
        owner.getApplicationContext().bindService(new Intent(owner, MetaWearBleService.class), this, Context.BIND_AUTO_CREATE);

        timer = new Timer();
        getTemp = new GetTemperature();
        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getActivity().getApplicationContext().unbindService(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusIcon = (ImageView) getActivity().findViewById(R.id.imageView);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        showUserSettings();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                showUserSettings();
                getTemp.cancel();
                getTemp = new GetTemperature();
                timer.schedule(getTemp, 1000, timeLimit);
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener);


    }


    @Override
    public void onServiceConnected(ComponentName cname, IBinder iservice) {
        name = cname;
        service = iservice;
        mwBoard = ((MetaWearBleService.LocalBinder) service).getMetaWearBoard(settings.getBtDevice());
        ready();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    public void onConnectionSuspended(int cause) {

    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    /**
     * Called when the app has reconnected to the board
     */
    public void reconnected() {
    }

    /**
     * Called when the mwBoard field is ready to be used
     */
    public void ready() {
        try {
            mcTempModule = mwBoard.getModule(MultiChannelTemperature.class);
            tempSources = mcTempModule.getSources();
            timer.schedule(getTemp, 1000, timeLimit);

        } catch (UnsupportedModuleException e) {
            Snackbar.make(getActivity().findViewById(R.id.device_setup_fragment), e.getMessage(),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showUserSettings() {

        tempLimit = sharedPrefs.getInt("tempAlert", 30) - 30;
        timeLimit = (sharedPrefs.getInt("tempFreq", 1) + 1) * 1000;
        vibrate = sharedPrefs.getBoolean("prefAlertVibrate", false);
        alert = sharedPrefs.getBoolean("prefAlertSound", false);
    }

    public void temperatureBelow() {
        TempBelow tB = new TempBelow();
        tB.execute();
    }

    public void setTempView() {
        ((DeviceSetupActivity)getActivity()).setTempView();
    }

    public void setStatusIcon(int status) {
        ((DeviceSetupActivity)getActivity()).setStatusIcon(status);
    }

    public void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            }
        } catch (SecurityException e){
            Log.getStackTraceString(e);
        }
    }

    private class GetTemperature extends TimerTask {
        private String temp;
        @Override
        public void run() {
            hasStarted = true;
            mcTempModule.routeData()
                    .fromSource(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE)).stream("temp_nrf_stream")
                    .commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {

                public void success(RouteManager result) {
                    result.subscribe("temp_nrf_stream", new RouteManager.MessageHandler() {
                        @Override
                        public void process(Message msg) {
                            temp = String.format("%.1f°C",
                                    msg.getData(Float.class));
                            sTemp = temp;
                            setTempView();
                            temperature = msg.getData(Float.class);

                            TempMeasured tempMeasured = new TempMeasured();
                            tempMeasured.execute();

                            if(temperature < tempLimit){
                                setStatusIcon(1);

                                temperatureBelow();
                                if(vibrate){
                                    Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                    vib.vibrate(900);
                                }
                                if(alert){
                                    try {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                                        r.play();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else {
                                setStatusIcon(0);
                            }
                        }
                    });

                    // Read temperature from the NRF soc chip
                    mcTempModule.readTemperature(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE));
                }
            });
        }
    }

    private class TempMeasured extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... params) {
            try {
                FileOutputStream fOs = getActivity().openFileOutput(allTemp, Context.MODE_APPEND);
                OutputStreamWriter oSw = new OutputStreamWriter(fOs);
                BufferedWriter bw = new BufferedWriter(oSw);
                bw.write(String.format("%.1f°C", temperature));
                bw.write(separator);
                bw.flush();
                bw.close();
                oSw.close();
                fOs.close();
            }
            catch (Exception e) {
                Log.d("Write to file", e.toString());
            }
            return null;
        }
    }

    private class TempBelow extends AsyncTask<Void, Void, Void> {

        public Void doInBackground(Void... params) {
            getLocation();
            try {
                FileOutputStream fOs = getActivity().openFileOutput(belowTemp, Context.MODE_APPEND);
                OutputStreamWriter oSw = new OutputStreamWriter(fOs);
                BufferedWriter bw = new BufferedWriter(oSw);
                bw.write(String.format("%.1f°C", temperature)+":"+mLatitudeText+":"+mLongitudeText);
                bw.write(separator);
                bw.flush();
                bw.close();
                oSw.close();
                fOs.close();
            }
            catch (Exception e) {
                Log.d("Write to file", e.toString());
            }
            return null;
        }
    }
}
