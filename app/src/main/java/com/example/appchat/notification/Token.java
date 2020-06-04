package com.example.appchat.notification;

public class Token {
    // Firebase donne un contrôle complet sur l'authentification en nous permettant
    // d'authentifier des utilisateurs à l'aide de jetons Web JSON sécurisés (JWT)
    // chaque utilisateur de cet application est identifié par un token unique stocké dans la base de données de Firebase
    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
