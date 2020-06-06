package com.example.appchat.outils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.appchat.R;

public class Apropos extends AppCompatActivity {

    private Toolbar toolbar;
    //Projet d'android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_propos);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("À propos");
        getSupportActionBar().getThemedContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
    }
}
