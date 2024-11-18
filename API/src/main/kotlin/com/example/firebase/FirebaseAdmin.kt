package com.example.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.InputStream


object FirebaseAdmin {
   val serviceAccount : InputStream? = this::class.java.getResourceAsStream("/serviceAccountKey.json")
   val options = FirebaseOptions.builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .build()

   fun init() : FirebaseApp = FirebaseApp.initializeApp(options)
}