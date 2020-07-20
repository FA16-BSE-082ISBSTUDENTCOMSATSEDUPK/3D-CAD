package com.example.integratedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;


public class OpenModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_model);



        UnityCallBack.getInstance().setModelLoadScene(false);
        Intent intent = new Intent(OpenModelActivity.this, UnityPlayerActivity.class);
        startActivity(intent);

    }
}
