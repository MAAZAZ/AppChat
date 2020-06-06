package com.example.appchat.outils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.appchat.R;
import com.example.appchat.compte.Login;

public class Start extends AppCompatActivity {

    private static int time=4000;

    // afficher le logo de l'application pendant 4 secondes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Start.this, Login.class);
                startActivity(intent);
                finish();
            }
        },time);
    }
}
