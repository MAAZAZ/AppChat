package com.example.appchat.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchat.R;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.modele.Chat;
import com.example.appchat.modele.User;
import com.example.appchat.notification.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> users;
    private FirebaseUser fbuser;
    private DatabaseReference reference,reference2;
    private List<String> users_name;
    private UserAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycleview2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fbuser= FirebaseAuth.getInstance().getCurrentUser();
        users_name=new ArrayList<>();
        users=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        //les messages
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Chat message=d.getValue(Chat.class);
                    //tester si l'utilisateur actuel est l'un des deux : emitteur ou recepteur
                    //si oui, ajouter le nom de l'autre utilisateur à la liste (username)
                    if(!users_name.contains(message.getEmitteur()) && !users_name.contains(message.getRecepteur())) {
                        if (message.getEmitteur().equals(fbuser.getUid())) {
                            users_name.add(message.getRecepteur());
                        }
                        if (message.getRecepteur().equals(fbuser.getUid())) {
                            users_name.add(message.getEmitteur());
                        }
                    }
                }
                //Log.d("users",users_name.toString());
                //lire les messages
                LireMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //creer un token de l'utilisateur connecté
        // utiliser pour conncter à FCM à travers les notification envoyé et recu
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) getContext(),new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                updateToken(newToken);
            }
        });

        return view;
    }

    //creer un nouveau token correspond à l'utilisateur et ajouter à la base de données
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token tok = new Token(token);
        reference.child(fbuser.getUid()).setValue(tok);
    }

    //lire les messages
     private void LireMessages(){
        //si l'utilisateur envoye o recoit un message, la liste des utilisateurs doit faire un mise à jour
         //resoudre le problème d'avoir meme utilisateur sur la meme page
        users.clear();
        reference2=FirebaseDatabase.getInstance().getReference("utilisateurs");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    for (String id : users_name) {
                        // à travers la liste des noms de l'utilisateurs
                        // recupprer les utilisateurs dans la base de données
                        if (user.getId().equals(id)) {
                            users.add(user);
                        }
                    }
                }
                //for(User u : users)
                  //  Log.e("users_find",u.getUsername());

                //lier recyclerView avec adapter de l'utilisateur
                userAdapter=new UserAdapter(getContext(), users);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
