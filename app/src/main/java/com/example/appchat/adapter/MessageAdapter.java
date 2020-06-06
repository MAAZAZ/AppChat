package com.example.appchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.appchat.R;
import com.example.appchat.modele.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Chat> messages;
    private String image_profil;

    //si le message envoyé par l'utilisateur
    public static int nb_message_right=1;
    //si le message recu par l'utilisateur
    public static int nb_message_left=0;
    private FirebaseUser fbrusr;

    public MessageAdapter(Context context, List<Chat> messages, String image_profil){
        this.context=context;
        this.messages=messages;
        this.image_profil=image_profil;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //si le message envoyé par l'utilisateur => faire l'appel à l'activité chat_item_right
        if(viewType==nb_message_right) {
             view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        }
        //si le message recu par l'utilisateur => faire l'appel à l'activité chat_item_left
        else{
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Chat chat=messages.get(position);
        ((ViewHolder)holder).getMessage().setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView message;
        private ImageView profil_img;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message1);
            profil_img = (ImageView) itemView.findViewById(R.id.profile_image);
        }

        public TextView getMessage(){
            return message;
        }
        public ImageView getProfil_img(){
            return profil_img;
        }

    }

    @Override
    public int getItemViewType(int position) {
        fbrusr=FirebaseAuth.getInstance().getCurrentUser();
        //position du message
        if(messages.get(position).getEmitteur().equals(fbrusr.getUid()))
            return nb_message_right;
        else
        return nb_message_left;
    }
}
