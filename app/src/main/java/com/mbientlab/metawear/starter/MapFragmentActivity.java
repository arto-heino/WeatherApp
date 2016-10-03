package com.mbientlab.metawear.starter;

/**
 * Created by milosberka on 30.8.2016.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by milosberka on 29.8.2016.
 */

public class MapFragmentActivity extends Fragment {
    private static final String ARG_PARAM1 = "txt";
    private static final String ARG_PARAM2 = "img";
    private String dTxt;
    private int dImg;
    private TextView textView;

    public static MapFragmentActivity newInstance(String dogTxt, String dogImg) {
        MapFragmentActivity fragment = new MapFragmentActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, dogTxt);
        args.putString(ARG_PARAM2, dogImg);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragmentActivity () {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.map_fragment, container, false);
        //textView = (TextView) layout.findViewById(R.id.textView);
        //textView.setText("vidu");
        return layout;
    }
}