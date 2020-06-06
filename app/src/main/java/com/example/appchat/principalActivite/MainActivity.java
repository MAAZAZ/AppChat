package com.example.appchat.principalActivite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.compte.Login;
import com.example.appchat.fragment.AmisFragment;
import com.example.appchat.fragment.MessageFragment;
import com.example.appchat.fragment.ProfilFragment;
import com.example.appchat.modele.User;
import com.example.appchat.outils.Apropos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private DatabaseReference reference;
    private CircleImageView profil;
    private TextView username;
    private Toolbar toolbar;
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //désactiver le clavier
        //Pour résoudre le probleme de retourner de l'activité des messages vers l'activité de fragment des messages
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().getThemedContext();
        toolbar.setTitleTextColor(0xFFFFFFFF);

        menu = (BottomNavigationView) findViewById(R.id.menuprinc);
        profil = (CircleImageView) findViewById(R.id.profile_image);
        username = (TextView) findViewById(R.id.username);
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(authUser.getUid());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profilImages/" + authUser.getUid() + ".jpeg");
        // recuperer l'image de profil de l'utilisateur
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                Glide.with(MainActivity.this).load(downloadUrl.toString()).into(profil);
            }
            //si il y'a un probleme de connexion, alors afficher l'image par défault
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profil.setImageResource(R.drawable.defaultprofile);
            }
        });

        //recuperer le nom d'utilisatuer pour le titre dans le toolbar
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Garder le fragment des messages comme le fragment principal
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MessageFragment()).commit();
        }

        //menu des fragments
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fr = null;
                switch (item.getItemId()) {
                    case R.id.messages:
                        fr = new MessageFragment();
                        break;
                    case R.id.amis:
                        fr = new AmisFragment();
                        break;
                    case R.id.profil:
                        fr = new ProfilFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fr).commit();
                return true;
            }
        });
    }

    //menu de toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //le choix de menu de toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
            return true;
        }
        if (item.getItemId() == R.id.apropos) {
            Intent intent = new Intent(MainActivity.this, Apropos.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
            return true;
        }
        return false;
    }
}
