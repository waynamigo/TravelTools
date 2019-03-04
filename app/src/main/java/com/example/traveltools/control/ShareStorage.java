package com.example.traveltools.control;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareStorage {
    public static boolean setShareInt(Context context, String tag, int value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putInt(tag,value);
        editor.apply();
        return true;
    }
    public static boolean setShareFloat(Context context,String tag, float value){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putFloat(tag,value);
        editor.apply();
        return true;
    }
    public static boolean setShareString(Context context,String tag1, String value1,String tag2,String value2){
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString(tag1,value1);
        editor.putString(tag2,value2);
        editor.apply();
        return true;
    }
    public static int getShareInt(Context context,String tag){
        SharedPreferences pref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return  pref.getInt(tag,0);
    }
    public static float getShareFloat(Context context,String tag){
        SharedPreferences pref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return  pref.getFloat(tag,0.0f);
    }
    public static String getShareString(Context context,String tag){
        SharedPreferences pref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return  pref.getString(tag,"");
    }
    public static String[] getShareString(Context context,String tag1,String tag2){
        SharedPreferences pref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        String [] value = new String[2];
        value[0] = pref.getString(tag1,"");
        value[1] = pref.getString(tag2,"");
        return  value;
    }
}
