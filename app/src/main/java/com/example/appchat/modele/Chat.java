package com.example.appchat.modele;

//classe des messages dans le chat
public class Chat {
    private String emitteur;
    private String recepteur;
    private String message;

    public Chat(){}

    public Chat(String emitteur, String recepteur, String message) {
        this.emitteur = emitteur;
        this.recepteur = recepteur;
        this.message = message;
    }

    public String getEmitteur() {
        return emitteur;
    }

    public void setEmitteur(String emitteur) {
        this.emitteur = emitteur;
    }

    public String getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(String recepteur) {
        this.recepteur = recepteur;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
