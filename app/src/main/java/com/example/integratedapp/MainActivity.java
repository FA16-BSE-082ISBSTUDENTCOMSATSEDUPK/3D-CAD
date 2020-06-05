package com.example.integratedapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;


public class MainActivity extends AppCompatActivity {

    private Button middle_activity;

    //-----------------------//
    ImageView imageView;
    Uri imageUri;
    Bitmap resultBitmap, initialImageBitmap;
    //-----------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        middle_activity = findViewById(R.id.middle_activity);
        middle_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UnityCallMiddleMan.class);
                startActivity(intent);
            }
        });

        //--------------------------------------------------Tahir's Code
        imageView = (ImageView) findViewById(R.id.imageView2);

        OpenCVLoader.initDebug();
        //--------------------------------------------------//

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //-----------------------//Tahir's Code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                initialImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(initialImageBitmap);

        }
    }

    public void openGallery(View v) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    public void convertToGrey(View v) throws JSONException {

        Mat rgpa = new Mat();
        Mat greyMat = new Mat();
        Mat edgeMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        Utils.bitmapToMat(initialImageBitmap, rgpa);


        //Logic Start Here
        Imgproc.cvtColor(rgpa, greyMat, Imgproc.COLOR_BGR2GRAY); // Color Inversion
        Imgproc.Canny(greyMat, edgeMat, 80, 100);// Edge detection
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edgeMat, linesP, 1, Math.PI / 180, 30, 10, 10);

        double[] data;
        Point pt1 = new Point();
        Point pt2 = new Point();

        JSONObject imageData = new JSONObject();
        JSONArray wallArray = new JSONArray();

        for (int i = 0; i < linesP.rows(); i++) {

            JSONObject wall = new JSONObject();
            data = linesP.get(i, 0);
            pt1.x = data[0];
            pt1.y = data[1];
            pt2.x = data[2];
            pt2.y = data[3];
            Imgproc.line(rgpa, pt1, pt2, new Scalar(0, 0, 200), 3);
            float angle = (float) atan2(pt1.y - pt2.y, pt1.x - pt2.x);

            wall.put("StartingPoint", pt1.x + "," + pt1.y);
            wall.put("EndingPoint", pt2.x + "," + pt2.y);
            wall.put("AngleInRadian", angle);

            wallArray.put(wall);
        }

        imageData.put("Walls", wallArray);

        String jsonString = imageData.toString();
        UnityCallBack.getInstance().setJsonString(jsonString);//Sending the json string to unitycallback
        Log.d("STATE", jsonString);

        //Logic End Here

        int height = initialImageBitmap.getWidth();
        int witdh = initialImageBitmap.getHeight();

        resultBitmap = Bitmap.createBitmap(height, witdh, Bitmap.Config.RGB_565);

        Utils.matToBitmap(rgpa, resultBitmap);

        imageView.setImageBitmap(resultBitmap);
    }

    //-----------------------//

    public void TextDetection(View v) throws JSONException {

        Mat image_mat = new Mat();
        Mat template_mat = new Mat();
        Mat result_mat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        Utils.bitmapToMat(initialImageBitmap, image_mat);

        Bitmap template_initial_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.characters_template);


        Utils.bitmapToMat(template_initial_bitmap, template_mat);

        Imgproc.matchTemplate(image_mat, template_mat, result_mat, Imgproc.TM_SQDIFF);

        Core.normalize(result_mat, result_mat, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result_mat);

        matchLoc = mmr.maxLoc;

        Log.d("Points:   ",matchLoc.toString());


//        //Drawing code start
//        Imgproc.rectangle(image_mat, matchLoc, new Point(matchLoc.x + template_mat.cols(), matchLoc.y + template_mat.rows()),
//                new Scalar(200, 0, 0), 10, 8, 0);
//
//        Imgproc.line(image_mat, matchLoc, new Point(matchLoc.x + template_mat.cols(), matchLoc.y + template_mat.rows()), new Scalar(0, 0, 200), 3);
//
//        //Drawing code end

        Mat mask = new Mat(image_mat.rows(), image_mat.cols(), CvType.CV_8U, Scalar.all(0));

//        Imgproc.ellipse( mask,
//                new Point( image_mat.rows() / 2, image_mat.cols() / 2 ),
//                new Size( image_mat.rows() / 3, image_mat.cols() / 5 ),
//                70.0,
//                0.0,
//                360.0,
//                new Scalar( 255, 255, 255 ),
//                -1,
//                8,
//                0 );

        Rect rect = new Rect(((int) matchLoc.x), ((int) matchLoc.y),template_mat.cols(),template_mat.rows());
        Imgproc.rectangle(mask, rect.tl(), rect.br(), new Scalar(255,255,255), -1, 8, 0);

        Mat cropped = new Mat();
        image_mat.copyTo( cropped, mask );

//        Mat trunctaed = image_mat.submat(new Rect(0, 0, 30, 30));

//        trunctaed.copyTo(image_mat);

        Log.d("Matchloc:   ",matchLoc.toString());


        Mat greyMat = new Mat();
        Mat edgeMat = new Mat();


        //Logic Start Here
        Imgproc.cvtColor(cropped, greyMat, Imgproc.COLOR_BGR2GRAY); // Color Inversion
        Imgproc.Canny(greyMat, edgeMat, 80, 100);// Edge detection
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edgeMat, linesP, 1, Math.PI / 180, 50, 10, 50);

        double[] data;
        Point pt1 = new Point();
        Point pt2 = new Point();

        JSONObject imageData = new JSONObject();
        JSONArray wallArray = new JSONArray();

        for (int i = 0; i < linesP.rows(); i++) {

            JSONObject wall = new JSONObject();
            data = linesP.get(i, 0);
            pt1.x = data[0];
            pt1.y = data[1];
            pt2.x = data[2];
            pt2.y = data[3];
            Imgproc.line(cropped, pt1, pt2, new Scalar(0, 0, 200), 3);
            float angle = (float) atan2(pt1.y - pt2.y, pt1.x - pt2.x);

            wall.put("StartingPoint", pt1.x + "," + pt1.y);
            wall.put("EndingPoint", pt2.x + "," + pt2.y);
            wall.put("AngleInRadian", angle);

            wallArray.put(wall);
        }

        imageData.put("Walls", wallArray);

        String jsonString = imageData.toString();
        UnityCallBack.getInstance().setJsonString(jsonString);//Sending the json string to unitycallback
        Log.d("STATE", jsonString);

        //Logic End Here

        int height = initialImageBitmap.getWidth();
        int witdh = initialImageBitmap.getHeight();

        resultBitmap = Bitmap.createBitmap(height, witdh, Bitmap.Config.RGB_565);

        Utils.matToBitmap(cropped, resultBitmap);

        imageView.setImageBitmap(resultBitmap);

//        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
//                new Scalar(0, 0, 0), 2, 8, 0);

//        imageView.setImageBitmap(bitmap);
    }
}