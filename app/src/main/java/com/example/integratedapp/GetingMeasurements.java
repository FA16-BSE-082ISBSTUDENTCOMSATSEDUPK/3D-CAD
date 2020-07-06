package com.example.integratedapp;

import android.widget.Toast;

import org.opencv.core.Point;

import static java.lang.Math.sqrt;

public class GetingMeasurements {

    public static double getLinesLength(Point P1, Point P2){

        double xDifference = P1.x - P2.x;
        double yDifference = P1.y - P2.y;

        double sqrX = Math.pow(xDifference, 2);
        double sqrY = Math.pow(yDifference, 2);

        double preResult = sqrX + sqrY;

        double result = sqrt(preResult);

        return result;

    }



}
