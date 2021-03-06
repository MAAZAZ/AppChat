package com.example.appchat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.appchat.R;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.modele.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AmisFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    //liste des utilisateurs inscris
    private List<User> users;
    //faire un recherche par le nom d'utilisateur
    private EditText recherche;
    private FirebaseUser fbr;
    private ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        users=new ArrayList<>();
        //partie de navigation fragment
        View view= inflater.inflate(R.layout.fragment_amis, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recherche=(EditText) view.findViewById(R.id.recherche);
        image=(ImageView)view.findViewById(R.id.image);
        //recupperer tous les utilisateurs
        AllUsers();
        fbr=FirebaseAuth.getInstance().getCurrentUser();
        //faire une recherche à travers le nom de l'utilisateur
        recherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    //faire un requete en fonction de nom de l'utilisateur
                    //doc : https://firebase.google.com/docs/database/rest/retrieve-data
                    //Paginate data with query cursors
                    //doc : https://firebase.google.com/docs/firestore/query-data/query-cursors
                    //uf8ff : est un point de code très élevé dans la plage Unicode.
                    Query query = FirebaseDatabase.getInstance().getReference("utilisateurs").orderByChild("username").startAt(s.toString()).endAt(s + "\uf8ff");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //supprimer la liste des utilisateurs
                            users.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (!user.getId().equals(fbr.getUid())) {
                                    //si le nom entré par l'utilisateur est correspond, alos il faut l'ajouter à la liste
                                    users.add(user);
                                }
                            }
                            // lier recyleview avec l'adapter des utilisateurs
                            userAdapter = new UserAdapter(getContext(), users);
                            recyclerView.setAdapter(userAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    // si la chaine est vide, alors faire l'appel à la méthode allUsers()
                   AllUsers();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    // recuprer tous les utilisateurs
    private void AllUsers(){
        users.clear();
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("utilisateurs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    if (user != null) {
                        String userId = user.getId();
                        String Uid = firebaseUser.getUid();
                        if (!userId.equals(Uid)) {
                            users.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),users);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
