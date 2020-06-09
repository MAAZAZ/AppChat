package com.example.appchat.modele;

//classe de l'utilisateur
public class User {
    private String id;
    private String Username;
    //utiliser seulement pour les images de profil d'autre utilisateurs
    private String imageUrl;

    public User(){}

    public User(String id, String username, String imageUrl) {
        this.id = id;
        this.Username = username;
        this.imageUrl= imageUrl;
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
    
    public String getImageUrl(){ 
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl){ 
        this.imageUrl=imageUrl;
    }
}
