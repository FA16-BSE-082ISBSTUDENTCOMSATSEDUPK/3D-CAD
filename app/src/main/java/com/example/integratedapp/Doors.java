package com.example.integratedapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.atan2;

public class Doors {

    public static JSONArray doorDetection(Mat originalImage) throws JSONException {

        //Fields to reduce redundency
        Point oldStartingPoint = new Point(0,0);
        Point oldEndingPoint = new Point(0, 0);
        float oldAngle = (float) 0.00;
        double oldLength = 0.0;

        Mat greyMat = new Mat();
        Mat edgeMat = new Mat();

        Mat roiTmp = originalImage.clone();
        Log.e("bitmapWidth", String.valueOf(originalImage.width()));
        final Mat hsvMat = new Mat();
        originalImage.copyTo(hsvMat);

        // convert mat to HSV format for Core.inRange()
        Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_RGB2HSV);



        Scalar lowerTestingColor = new Scalar(160, 150, 20); //Dark Red
        Scalar upperTestingColor = new Scalar(180, 255, 255);



        Core.inRange(hsvMat, lowerTestingColor, upperTestingColor, roiTmp);   // select only blue pixels

        Mat cropped = new Mat();
        originalImage.copyTo( cropped, roiTmp );

        //Logic Start Here
        Imgproc.cvtColor(cropped, greyMat, Imgproc.COLOR_BGR2GRAY); // Color Inversion
        Imgproc.Canny(greyMat, edgeMat, 80, 100);// Edge detection
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edgeMat, linesP, 1, Math.PI / 180, 30, 10, 10);

        double[] data;
        Point pt1 = new Point();
        Point pt2 = new Point();

//        JSONObject imageData = new JSONObject();
        JSONArray doorArray = new JSONArray();

        for (int i = 0; i < linesP.rows(); i++) {

            JSONObject door = new JSONObject();
            data = linesP.get(i, 0);
            pt1.x = data[0];
            pt1.y = data[1];
            pt2.x = data[2];
            pt2.y = data[3];
            Imgproc.line(originalImage, pt1, pt2, new Scalar(0, 255, 0), 3);
            float angle = (float) atan2(pt1.y - pt2.y, pt1.x - pt2.x);

            double doorLength = GetingMeasurements.getLinesLength(pt1, pt2);


            //Check and remove Redundency start

            if(checks.similarPoints(oldStartingPoint, pt1)){
                if(checks.similarPoints(oldEndingPoint, pt2)){
                    if(checks.similarAngles(oldAngle, angle)){
                        if(checks.similarLength(oldLength, doorLength)){
                            //Do whatever if walls are similar

                            Log.d("Testing Redundent:   ", "True" );
                            //Update Old values to New Values
                            oldStartingPoint = pt1;
                            oldEndingPoint = pt2;
                            oldAngle = angle;
                            oldLength = doorLength;

//                            i++;
//                            continue;


                        }
                    }
                }
            };

            //Update Old values to New Values
            oldStartingPoint = pt1;
            oldEndingPoint = pt2;
            oldAngle = angle;
            oldLength = doorLength;


            door.put("StartingPoint", pt1.x + "," + pt1.y);
            door.put("EndingPoint", pt2.x + "," + pt2.y);
            door.put("AngleInRadian", angle);
            door.put("Length", doorLength);



            doorArray.put(door);
        }


        Log.d("Doors detected:   ", doorArray.toString());
        return doorArray;
    }

}
