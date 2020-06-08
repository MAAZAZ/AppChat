package com.example.appchat.compte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import maes.tech.intentanim.CustomIntent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.appchat.principalActivite.MainActivity;
import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class Inscription extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText email, username, password;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        //Toolbar
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inscription");
        //Renvoie un contexte avec un thème approprié pour créer des vues qui apparaîtront dans la barre d'action.
        getSupportActionBar().getThemedContext();
        //colore de texte "inscription"
        toolbar.setTitleTextColor(0xFFFFFFFF);
        //retourner au la page de login
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //materialEditText
        email =  (MaterialEditText) findViewById(R.id.email);
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);
        //initialiser l'instance Firebase Authentification => Connectez l'application à Firebase
        auth = FirebaseAuth.getInstance();
    }

    //button d'inscription
    public void inscription(View view) {
        //email non nulle & username non nulle & password non nulle
        //password doit etre supérieur à 5 caractères
        if (!email.getText().toString().isEmpty() && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && password.getText().toString().length()>5) {
            //creer un compte avec un email & un mot de passe
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Task est une API qui représente les appels de méthode asynchrones
                    if (task.isSuccessful()) {
                        //si le compte est crée, alors récupper l'id de cet utilisateur
                        String usernameId = auth.getCurrentUser().getUid();
                        //getInstance() : Gets the default FirebaseDatabase instance.
                        //getReference() : Gets a DatabaseReference for the database root node.
                        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(usernameId);
                        //HashMap list
                        HashMap<String,String> hashMap = new HashMap<>();
                        //id
                        hashMap.put("id", usernameId);
                        //nom d'utilisateur = username
                        hashMap.put("username", username.getText().toString());
                        //image par default, il suffit de le recuperer à l'application et l'enregister dans le Firebase Storage avec id de l'utilisateur
                        //Drawable est une abstraction générale pour "quelque chose qui peut être dessiné".
                        Drawable myDrawable= getResources().getDrawable(R.drawable.defaultprofile);
                        //transférer sous forme de Bitmap qui rerésente un simple  rectangle de pixels
                        Bitmap image= ((BitmapDrawable) myDrawable).getBitmap();
                        //fait l'appel de la méthode handleUpload qui permet le stockage dans Firebase Storage
                        handleUpload(image);
                        //si le compte est bien enregistrer dans la base de données de Firebase
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //transéfer vers la page d'acceuil
                                    Intent intent = new Intent(Inscription.this, MainActivity.class);
                                    //FLAG_ACTIVITY_NEW_TASK && FLAG_ACTIVITY_CLEAR_TASK : ces indicateurs entraînera la suppression
                                    //de toute tâche existante qui serait associée à l'activité avant le démarrage de l'activité.
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    //Afficher le message de création de compte
                                    Toast.makeText(Inscription.this, "Votre compte a été crée!", Toast.LENGTH_SHORT).show();
                                    //transférer vers l'activité principale
                                    startActivity(intent);
                                    //l'animation utilisée
                                    CustomIntent.customType(Inscription.this, "fadein-to-fadeout");
                                    //Lors de l'appel finish()d'une activité, la méthode onDestroy() est exécutée.
                                    finish();
                                }
                            }
                        });
                    } else {
                        //probleme de connexion au serveur Firebase
                        Toast.makeText(Inscription.this, "Erreur de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //probleme de ne pas respecter les régles
            Toast.makeText(Inscription.this, "Veuillez remplir tout les champs (le mot de passe doit être supérieur à 6 caractères)", Toast.LENGTH_SHORT).show();
        }
    }

    //upload l'image
    private void handleUpload(Bitmap bitmap) {
        //l'utilisateur actuel
        FirebaseUser firebaseUser = auth.getCurrentUser();
        //un flux de sortie dans lequel les données sont écrites dans un tableau d'octets.
        ByteArrayOutputStream Baos = new ByteArrayOutputStream();
        //les paramétres de l'image (format, qualité, le flux utilisé)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Baos);
        //l'id de l'utilisateur
        final String uid = firebaseUser.getUid();
        //creer un image avec id comme titre
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profilImages").child(uid + ".jpeg");
        //UploadTask : Une tâche contrôlable qui télécharge et déclenche des événements pour le succès, la progression et l'échec.
        //Il permet également une pause et une reprise pour contrôler l'opération de téléchargement.
        storageReference.putBytes(Baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(Inscription.this, "l'image a été ajouté!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(Inscription.this, "Problème du connexion!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
