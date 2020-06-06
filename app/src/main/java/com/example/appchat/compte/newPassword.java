package com.example.appchat.compte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import maes.tech.intentanim.CustomIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class newPassword extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView email;
    private Button btn_env;
    private FirebaseAuth fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nouveau mot de passe");
        getSupportActionBar().getThemedContext();
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email=(TextView) findViewById(R.id.send_pass);
        btn_env=(Button) findViewById(R.id.send);
        fb=FirebaseAuth.getInstance();
        //si l'utilisateur clique sur le buttom d'envoyé
        btn_env.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //la barre de l'email est vide
                if(email.getText().toString().equals("")){
                    Toast.makeText(newPassword.this,"Vous n'avez pas saisi votre email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //la méthode ci-dessous permet d'envoyer le mot de passe d'un email à travers le service de Firebase
                    fb.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(newPassword.this,"Vous avez recu un message sur votre email pour recuperer le mot de passe",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(newPassword.this, Login.class);
                                startActivity(intent);
                                CustomIntent.customType(newPassword.this, "fadein-to-fadeout");
                            }
                            else
                            {
                                //sinon afficher l'erreur lier avec l'exception du Firebase
                                String error=task.getException().getMessage();
                                Toast.makeText(newPassword.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
