package com.example.appchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appchat.message.Messages;
import com.example.appchat.R;
import com.example.appchat.modele.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import maes.tech.intentanim.CustomIntent;

public class UserAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final User user = users.get(position);
        ((ViewHolder) holder).getUsername().setText(user.getUsername());
        //recuperer l'image de profil de l'utilisateur
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profilImages/" + user.getId() + ".jpeg");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                Glide.with(context).load(downloadUrl.toString()).into(((ViewHolder) holder).getProfil_img());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //cas de problème de connexion
                ((ViewHolder) holder).getProfil_img().setImageResource(R.drawable.defaultprofile);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Messages.class);
                intent.putExtra("id", user.getId());
                //intent.putExtra("name", user.getUsername());
                context.startActivity(intent);
                CustomIntent.customType(context, "fadein-to-fadeout");
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView profil_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            profil_img = (ImageView) itemView.findViewById(R.id.profile_image);
        }

        public TextView getUsername() {
            return username;
        }

        public ImageView getProfil_img() {
            return profil_img;
        }

    }
}
