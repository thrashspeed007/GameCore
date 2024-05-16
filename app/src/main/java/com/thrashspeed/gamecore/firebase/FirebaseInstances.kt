package com.thrashspeed.gamecore.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseInstances {
    /**
     * Instancia de FirebaseFirestore para acceder a la base de datos Firestore.
     */
    val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Instancia de FirebaseAuth para la autenticación de usuarios.
     */
    val authInstance: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}
