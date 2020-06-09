package com.example.appchat.modele;

//classe de l'utilisateur
public class User {
    private String id;
    private String Username;

    public User(){}

    public User(String id, String username) {
        this.id = id;
        this.Username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
