package com.example.appchat.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

// retrofit2 nous permet de générer un objet Json et envoyé sous forme de requete (header, body)
// header pour le type d'object envoyé
// body pour le contenu de la requete
public interface APIService {
    // Méthodes de requete URL spécifiées par les annotations (@Post, @GET, etc)
    // @Headers spécifie l'en-tête avec la valeur du paramètre annoté
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAFspYx48:APA91bFMmjkxeXyFKlull-Ix0AvsKdx3SjUGDAOXrGo360Mpp4mkxrkolRBthxrMT4Z3CUXTOj1AOZEFClxtRXhXWZj5h77FMckOM4aFbnipW3KqAV3riph97Te4y7zBrmtEi0C3y8Ku"
            }
    )

    // Retrofit nous permet de remplacer l'URL de base spécifiée en la modifiant dans l'annotation
    @POST("fcm/send")
    // l'appel de la méthode de envoye la notification
    Call<Response> sendNotification(@Body Sender body);
}
