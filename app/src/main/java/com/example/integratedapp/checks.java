package com.example.integratedapp;

import android.graphics.Bitmap;

import org.json.JSONObject;
import org.opencv.core.Mat;

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

//    public static boolean checkDataIsNotValid(JSONObject imageData){
//        if(imageData == null)
//            return false;
//        else
//            return true;
//    }

}


