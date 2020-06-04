package com.example.appchat.notification;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    // il s'agit d'un client HTTP de type sécurisé pour Android et Java
    // il est facile de demander des services Web de REST avec GET, POST, PUT et bien plus encore.
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
