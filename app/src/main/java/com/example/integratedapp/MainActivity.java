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
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import static java.lang.Math.atan2;


public class MainActivity extends AppCompatActivity {

    private Button middle_activity;

    //-----------------------//
    ImageView imageView;
    Uri imageUri;
    Bitmap resultBitmap,initialImageBitmap;
    //-----------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        if(requestCode ==100 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();

            try {
                initialImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(initialImageBitmap);

        }
    }

    public void openGallery(View v){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery,100);
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
        Imgproc.cvtColor(rgpa,greyMat,Imgproc.COLOR_BGR2GRAY); // Color Inversion
        Imgproc.Canny(greyMat, edgeMat, 80, 100);// Edge detection
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(edgeMat, linesP, 1, Math.PI/180, 50, 100 ,50);

        double[] data;
        Point pt1 = new Point();
        Point pt2 = new Point();

        JSONObject imageData = new JSONObject();
        JSONArray wallArray = new JSONArray();

        for (int i = 0; i < linesP.rows(); i++){

            JSONObject wall = new JSONObject();
            data = linesP.get( i,0);
            pt1.x = data[0];
            pt1.y = data[1];
            pt2.x = data[2];
            pt2.y = data[3];
            Imgproc.line(rgpa, pt1, pt2, new Scalar(0, 0, 200), 3);
            float angle = (float) atan2(pt1.y - pt2.y, pt1.x - pt2.x);

            wall.put("StartingPoint",pt1.x+","+pt1.y);
            wall.put("EndingPoint",pt2.x+","+pt2.y);
            wall.put("AngleInRadian",angle);

            wallArray.put(wall);
        }

        imageData.put("Walls",wallArray);

        String jsonString = imageData.toString();
        UnityCallBack.getInstance().setJsonString(jsonString);//Sending the json string to unitycallback
        Log.d("STATE",jsonString);

        //Logic End Here

        int height = initialImageBitmap.getWidth();
        int witdh = initialImageBitmap.getHeight();

        resultBitmap = Bitmap.createBitmap(height,witdh,Bitmap.Config.RGB_565);

        Utils.matToBitmap(rgpa,resultBitmap);

        imageView.setImageBitmap(resultBitmap);
    }

    //-----------------------//

}
