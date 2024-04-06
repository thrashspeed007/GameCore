package com.thrashspeed.gamecore.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thrashspeed.gamecore.firebase.FirebaseInstances

/**
 * Clase singleton que proporciona utilidades para interactuar con Firestore en Firebase.
 */
object FirestoreUtilities {

    /**
     * Guarda la información del usuario en Firestore.
     *
     * @param username Nombre de usuario.
     * @param email Correo electrónico del usuario.
     * @param fullname Nombre completo del usuario.
     * @param callback Función de retorno de llamada que indica si la operación fue exitosa o no.
     */
    fun saveUserInFirestore(email: String, username: String, fullname: String, callback: (Boolean) -> Unit) {
        val firestore: FirebaseFirestore = FirebaseInstances.firestoreInstance
        val auth: FirebaseAuth = FirebaseInstances.authInstance

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userData = hashMapOf(
                "email" to email,
                "username" to username,
                "fullname" to fullname
            )

            firestore.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    createUsernameEntry(username, user.uid, email) { success ->
                        // Habría que hacer rollback de lo anterior si result es false
                        callback(success)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }

    private fun createUsernameEntry(username: String, uid: String, email: String, callback: (Boolean) -> Unit) {
        FirebaseInstances.firestoreInstance.collection("usernames").document(username)
            .set(
                hashMapOf(
                    "uid" to uid,
                    "email" to email
                )
            )
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}