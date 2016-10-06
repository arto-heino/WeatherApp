package com.mbientlab.metawear.starter;

/**
 * Created by milosberka on 30.8.2016.
 */

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by milosberka on 29.8.2016.
 */

public class MapFragmentActivity extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "txt";
    private static final String ARG_PARAM2 = "img";
    private String dTxt;
    private int dImg;
    private TextView textView;

    MapView gMapView;
    GoogleMap gMap = null;
    private static FragmentManager fm;

    public static MapFragmentActivity newInstance(FragmentManager fragm, String dogImg) {
        MapFragmentActivity fragment = new MapFragmentActivity();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, dogTxt);
        args.putString(ARG_PARAM2, dogImg);
        fragment.setArguments(args);
        fm = fragm;
        return fragment;
    }

    public MapFragmentActivity () {

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
        LatLng sydney = new LatLng(-34, 151);
        gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


}