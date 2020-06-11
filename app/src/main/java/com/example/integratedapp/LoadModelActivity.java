package com.example.integratedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ciit.modelconversion.UnityPlayerActivity;

public class LoadModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_model);


        UnityCallBack.getInstance().setModelLoadScene(true);
        Intent intent = new Intent(LoadModelActivity.this, UnityPlayerActivity.class);
        startActivity(intent);
    }
}
