package com.mbientlab.metawear.starter;

/**
 * Created by milosberka on 30.8.2016.
 */

import android.os.AsyncTask;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by milosberka on 29.8.2016.
 */

public class MapFragmentActivity extends Fragment implements OnMapReadyCallback {

    private MapView gMapView;
    private GoogleMap gMap = null;
    private static FragmentManager fm;
    private static final String belowTemp = "belowTemp.text";
    private FileObserver fObs;

    public static MapFragmentActivity newInstance(FragmentManager fragm) {
        MapFragmentActivity fragment = new MapFragmentActivity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fm = fragm;
        return fragment;
    }

    public MapFragmentActivity() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.map_fragment, container, false);
        gMapView = (MapView) layout.findViewById(R.id.mapView);
        gMapView.onCreate(savedInstanceState);

        gMapView.onResume();

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        gMapView.getMapAsync(this);
        return layout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        setMarkers();

        fObs = new FileObserver(belowTemp) {
            @Override
            public void onEvent(int event, String belowTemp) {
                setMarkers();
            }
        };
        fObs.startWatching();
    }

    public void setMarkers () {
        try {
            //gMap.clear();
            FileInputStream fISx = getActivity().openFileInput(belowTemp);
            InputStreamReader iSRx = new InputStreamReader(fISx);
            BufferedReader bRx = new BufferedReader(iSRx);
            String line;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            do {
                line = bRx.readLine();
                String[] markerData = line.split(":");
                String temp = markerData[0];
                Double lat = Double.parseDouble(markerData[1]);
                Double lng = Double.parseDouble(markerData[2]);
                LatLng location = new LatLng(lat, lng);
                gMap.addMarker(new MarkerOptions().position(location).title("Temperature: " + temp));
                builder.include(location);

            } while (line != null);
            LatLngBounds bounds = builder.build();
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            bRx.close();
        } catch (Exception e) {
            Log.d("Write to file", e.toString());
        }
    }
}

