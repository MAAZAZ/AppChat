package com.example.appchat.notification;

public class Sender {
    // identifier data (utilisateur actuel, ustilisateur destinateur, titre, contenu, icone)
    // identifier le token d'utilisateur
    public Data data;
    public String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
