package com.example.appchat.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.adapter.MessageAdapter;
import com.example.appchat.modele.Chat;
import com.example.appchat.modele.User;
import com.example.appchat.notification.APIService;
import com.example.appchat.notification.Client;
import com.example.appchat.notification.Data;
import com.example.appchat.notification.Response;
import com.example.appchat.notification.Sender;
import com.example.appchat.notification.Token;
import com.example.appchat.principalActivite.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Messages extends AppCompatActivity {

    private CircleImageView profil_img;
    private TextView username;
    private FirebaseUser FBuser;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private Button send;
    private TextView message;
    private MessageAdapter messageAdapter;
    private List<Chat> messages;
    private RecyclerView recyclerView;
    private APIService apiService;
    private String userid;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //possibilité de retourner vers l'acvitité principale (fragment des messages)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Messages.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(Messages.this, "fadein-to-fadeout");
            }
        });

        //pour connecter au serveur de FCM (Firebase Cloud Messaging)
        //utiliser pour les notifications
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        //Creer un vertical layout avec LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        profil_img = (CircleImageView) findViewById(R.id.profile_image1);
        username = (TextView) findViewById(R.id.username1);

        send = (Button) findViewById(R.id.send);
        message = (TextView) findViewById(R.id.message);

        //recevoir id de l'utilisateur choisi
        Intent intent = getIntent();
        final String userid = intent.getStringExtra("id");

        FBuser = FirebaseAuth.getInstance().getCurrentUser();

        //l'autre utilisateur
        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //ajouter son nom dans le toolbar
                username.setText(user.getUsername());
                //ajouter son image dans le toolbar
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = storageRef.child("profilImages/" + user.getId() + ".jpeg");
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Glide.with(Messages.this).load(downloadUrl.toString()).into(profil_img);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profil_img.setImageResource(R.drawable.defaultprofile);
                    }
                });

                //lire les messages
                LireMessages(FBuser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Envoyé un nouveau message
    public void sendMessage(View view) {

        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        //String usernam = intent.getStringExtra("name");

        FBuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        if (!message.getText().toString().equals("")) {
            notify=true;
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("emitteur", FBuser.getUid());
            hashMap.put("recepteur", userid);
            hashMap.put("message", message.getText().toString());
            reference.child("Chats").push().setValue(hashMap);

            final String msg = message.getText().toString();

            reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(FBuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotifiaction(userid, user.getUsername(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            message.setText("");
        }
    }

    // Envoyé un nouveau message permet d'envoyé une nouvelle notification vers l'utilisateur destinataire
    private void sendNotifiaction(String receiver, final String username, final String message){
        //en utilisant les jettons
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(FBuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "Nouveau message", userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(Messages.this, "Problème de connexion!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // lire les messages
    private void LireMessages(final String id, final String userid, final String ImgUrl) {
        messages = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //si l'utilisateur à recevoir ou envoyer un nouveau messages
                //alors supprimer les messages avant et les lire à nouveau
                messages.clear();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Chat message = dataSnap.getValue(Chat.class);
                    if (message.getRecepteur().equals(id) && message.getEmitteur().equals(userid) || message.getRecepteur().equals(userid) && message.getEmitteur().equals(id)
                    ) {
                        messages.add(message);
                    }
                    messageAdapter = new MessageAdapter(Messages.this, messages, ImgUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
