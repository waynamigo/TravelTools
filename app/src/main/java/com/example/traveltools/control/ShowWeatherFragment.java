package com.example.traveltools.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.amap.api.services.weather.LocalWeatherLive;

public class ShowWeatherFragment extends DialogFragment{
    public static final String KEY_MSG = "weather msg";
    private LocalWeatherLive weatherLive;
    public ShowWeatherFragment() {
        // Required empty public constructor
    }

    public static ShowWeatherFragment newInstance(LocalWeatherLive weatherLive) {
        ShowWeatherFragment fragment = new ShowWeatherFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MSG,weatherLive);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            weatherLive =getArguments().getParcelable(KEY_MSG);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("济南天气");
        builder.setMessage(weatherLive.getCity()+"\n"
                +weatherLive.getWeather()+"\n"
                +weatherLive.getTemperature()+"\n"

        );
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
}