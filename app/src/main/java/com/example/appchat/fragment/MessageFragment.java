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
import java.util.concurrent.Executor;


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
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Chat message=d.getValue(Chat.class);
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
                LireMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) getContext(),new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                updateToken(newToken);
            }
        });

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token tok = new Token(token);
        reference.child(fbuser.getUid()).setValue(tok);
    }

     private void LireMessages(){
        users.clear();
        reference2=FirebaseDatabase.getInstance().getReference("utilisateurs");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    for (String id : users_name) {
                        if (user.getId().equals(id)) {
                            users.add(user);
                        }
                    }
                }
                //for(User u : users)
                  //  Log.e("users_find",u.getUsername());

                userAdapter=new UserAdapter(getContext(), users);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
