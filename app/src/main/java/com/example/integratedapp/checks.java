package com.example.integratedapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Point;

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

    public static boolean similarPoints(Point P1, Point P2){

        double xDifference = P1.x - P2.x;
        double yDifference = P1.y - P2.y;

        double sqrX = Math.pow(xDifference, 2);
        double sqrY = Math.pow(yDifference, 2);

        double X = Math.sqrt(sqrX);
        double Y = Math.sqrt(sqrY);

        if(X < 7 && Y < 7)
            return true;
        else
            return false;


    }

    public static boolean similarAngles(float A1, float A2){

        float difference = A1 - A2;
        float sqrDiff = (float) Math.pow(difference, 2);
        float Diff = (float) Math.sqrt(sqrDiff);


        if(Diff < 7)
            return true;
        else
            return false;
    }

    public static boolean similarLength(double L1, double L2){

        double difference = L1 - L2;
        double sqrDiff = Math.pow(difference, 2);
        double Diff = Math.sqrt(sqrDiff);

        if(Diff < 7)
            return true;
        else
            return false;
    }

}


