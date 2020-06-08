package com.example.appchat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ProfilFragment extends Fragment {

    private CircleImageView img_profil;
    private TextView username;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private static int image_req = 1;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        img_profil = (CircleImageView) view.findViewById(R.id.profil_image);
        username = (TextView) view.findViewById(R.id.profil_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        username.setText(firebaseUser.getEmail());

        //service de firebase storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("profilImages/" + firebaseUser.getUid() + ".jpeg");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                Glide.with(ProfilFragment.this).load(downloadUrl.toString()).into(img_profil);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                img_profil.setImageResource(R.drawable.defaultprofile);
            }
        });

        // si l'utilisateur est cliqué sur l'image de profil
        img_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, image_req);
                    CustomIntent.customType(getContext(), "rotateout-to-rotatein");
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_req) {
            //demande l'autorisation à accéder à la camera
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    img_profil.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    //upload l'image
    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream Baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Baos);
        String uid = firebaseUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("profilImages").child(uid + ".jpeg");
        storageReference.putBytes(Baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Votre image de profil est bien modifié!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Problème du connexion!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
