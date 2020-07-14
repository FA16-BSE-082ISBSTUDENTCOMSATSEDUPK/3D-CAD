package com.example.integratedapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

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
import static java.lang.Math.sqrt;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private Button openModelBtn, loadModelBtn, captureImgBtn;

    //-----------------------//
    ImageView imageView;
    Uri imageUri;
    Bitmap resultBitmap, initialImageBitmap;

    JSONObject imageData;
    //-----------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        openModelBtn = findViewById(R.id.open_model_btn);
        openModelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checks.checkDataIsNotEmpty(imageData)){
                    Toast.makeText(MainActivity.this, "Data is not present", Toast.LENGTH_LONG).show();
                    return;
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm 2D Design !");
                builder.setMessage("Are you sure to create 3D Model ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, OpenModelActivity.class);
                        startActivity(intent);                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();



            }
        });



        loadModelBtn = findViewById(R.id.load_model_btn);
        loadModelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoadModelActivity.class);
                startActivity(intent);
            }
        });

        captureImgBtn = findViewById(R.id.capture_img_btn);
        captureImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });



        //--------------------------------------------------Tahir's Code
        imageView = (ImageView) findViewById(R.id.imageView2);

        OpenCVLoader.initDebug();
        //--------------------------------------------------//

    }


    //Capture Functionality start

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


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

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            initialImageBitmap = photo;
            imageView.setImageBitmap(photo);
        }
    }

    public void openGallery(View v) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    public void getFeatures(View v) throws JSONException {



        Mat originalImage = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        if(!checks.checkInputImage(initialImageBitmap)){

            Toast.makeText(this, "Image not selected", Toast.LENGTH_LONG).show();

            return;
        }

        Utils.bitmapToMat(initialImageBitmap, originalImage);



        imageData = new JSONObject();
        JSONArray wallArray = Walls.wallDetection(originalImage);
        JSONArray doorArray = Doors.doorDetection(originalImage);
        JSONArray windowArray = Windows.windowDetection(originalImage);

        imageData.put("Walls", wallArray);
        imageData.put("Doors", doorArray);
        imageData.put("Windows", windowArray);

        if(doorArray.length()==0 || windowArray.length()==0 || wallArray.length()==0){

            JSONArray linesArray = Lines.lineDetection(originalImage);
            imageData.remove("Walls");
            imageData.remove("Doors");
            imageData.remove("Windows");

            imageData.put("Walls", linesArray);
        }





        String jsonString = imageData.toString();
        UnityCallBack.getInstance().setJsonString(jsonString);//Sending the json string to unitycallback
        Log.d("STATE", jsonString);


        int height = initialImageBitmap.getWidth();
        int witdh = initialImageBitmap.getHeight();

        resultBitmap = Bitmap.createBitmap(height, witdh, Bitmap.Config.RGB_565);

        Utils.matToBitmap(originalImage, resultBitmap);

        imageView.setImageBitmap(resultBitmap);
    }

    //-----------------------//

}