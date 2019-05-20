package com.example.traveltools;

import android.app.Application;
import android.content.Context;




/**
 * Created by waynamigo on 18-5-27.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        MobSDK.init(context, "2609bb84cec79", "17a138fcb432787ac3a27bd153d30a96");

    }
}
