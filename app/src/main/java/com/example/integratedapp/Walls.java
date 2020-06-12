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

public class Walls {

    public static JSONArray wallDetection(Mat originalImage) throws JSONException {

        Mat greyMat = new Mat();
        Mat edgeMat = new Mat();

        Mat roiTmp = originalImage.clone();
        Log.e("bitmapWidth", String.valueOf(originalImage.width()));
        final Mat hsvMat = new Mat();
        originalImage.copyTo(hsvMat);

        // convert mat to HSV format for Core.inRange()
        Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_RGB2HSV);



        Scalar lowerTestingColor = new Scalar(0, 0, 0); //Grey
        Scalar upperTestingColor = new Scalar(180, 255, 130);



        Core.inRange(hsvMat, lowerTestingColor, upperTestingColor, roiTmp);   // select only blue pixels

        Mat cropped = new Mat();
        originalImage.copyTo( cropped, roiTmp );

        //Logic Start Here
        Imgproc.cvtColor(cropped, greyMat, Imgproc.COLOR_BGR2GRAY); // Color Inversion
        Imgproc.Canny(greyMat, edgeMat, 80, 100);// Edge detection
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edgeMat, linesP, 1, Math.PI / 180, 50, 10, 10);

        double[] data;
        Point pt1 = new Point();
        Point pt2 = new Point();

//        JSONObject imageData = new JSONObject();
        JSONArray wallArray = new JSONArray();

        for (int i = 0; i < linesP.rows(); i++) {

            JSONObject wall = new JSONObject();
            data = linesP.get(i, 0);
            pt1.x = data[0];
            pt1.y = data[1];
            pt2.x = data[2];
            pt2.y = data[3];
            Imgproc.line(originalImage, pt1, pt2, new Scalar(255, 0, 0), 3);
            float angle = (float) atan2(pt1.y - pt2.y, pt1.x - pt2.x);

            double wallLength = GetingMeasurements.getLinesLength(pt1, pt2);


            wall.put("StartingPoint", pt1.x + "," + pt1.y);
            wall.put("EndingPoint", pt2.x + "," + pt2.y);
            wall.put("AngleInRadian", angle);
            wall.put("Length", wallLength);




            wallArray.put(wall);
        }

//        imageData.put("Walls", wallArray);

//        String jsonString = imageData.toString();
//        UnityCallBack.getInstance().setJsonString(jsonString);//Sending the json string to unitycallback
//        Log.d("STATE", jsonString);
        Log.d("Walls detected:   ", wallArray.toString());
        return wallArray;
    }

}
