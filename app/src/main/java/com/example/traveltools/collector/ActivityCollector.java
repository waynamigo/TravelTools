package com.example.traveltools.collector;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by waynamigo on 18-8-3.
 */

public class ActivityCollector {
    public static ArrayList<Activity> activities = new ArrayList<>();
    public static void addActivty(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishALL(){
        for(Activity activity:activities){
            activity.finish();
        }
    }
}
