package com.example.integratedapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.ciit.modelconversion.UnityPlayerActivity;


public class UnityCallMiddleMan extends AppCompatActivity{

    private Button unity_start, back_main, dummy_model, load_scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity_call_middle_man);
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

        unity_start = findViewById(R.id.unity_start);
        unity_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityCallBack.getInstance().setUseDummyValues(false);
                UnityCallBack.getInstance().setModelLoadScene(false);
                Intent intent = new Intent(UnityCallMiddleMan.this, UnityPlayerActivity.class);
                startActivity(intent);

            }
        });

        dummy_model = findViewById(R.id.dummy_model);
        dummy_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityCallBack.getInstance().setUseDummyValues(true);
                UnityCallBack.getInstance().setModelLoadScene(false);
                Intent intent = new Intent(UnityCallMiddleMan.this, UnityPlayerActivity.class);
                startActivity(intent);

            }
        });

        load_scene = findViewById(R.id.load_scene);
        load_scene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityCallBack.getInstance().setModelLoadScene(true);
                Intent intent = new Intent(UnityCallMiddleMan.this, UnityPlayerActivity.class);
                startActivity(intent);
            }
        });

        back_main = findViewById(R.id.back_main);
        back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnityCallMiddleMan.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



}
