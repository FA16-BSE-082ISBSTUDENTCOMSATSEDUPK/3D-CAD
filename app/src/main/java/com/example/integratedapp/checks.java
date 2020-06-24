package com.example.integratedapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.lang.reflect.Array;

public class checks {

    public static boolean checkInputImage(Bitmap originalImage){

    if(originalImage == null)
            return false;
        else
            return true;
    }

    public static boolean checkDataIsNotEmpty(JSONObject imageData){
        if(imageData == null)
            return false;
        else
            return true;
    }



}


