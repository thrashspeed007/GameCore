package com.thrashspeed.gamecore.firebase.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.ListEntity
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

            val userDocRef = firestore.collection("users").document(user.uid)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        callback(true)
                    } else {
                        userDocRef.set(userData)
                            .addOnSuccessListener {
                                createUsernameEntry(username, user.uid, email) { success ->
                                    callback(success)
                                }
                            }
                            .addOnFailureListener {
                                callback(false)
                            }
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

    fun insertGame(gameEntity: GameEntity, callback: (Boolean) -> Unit) {
        val userUid = FirebaseInstances.authInstance.uid.toString()
        val gamesCollectionRef = FirebaseInstances.firestoreInstance
            .collection("users")
            .document(userUid)
            .collection("games")

        val gameMap = gameEntity.toMap()

        gamesCollectionRef.document(gameEntity.id.toString()).set(gameMap)
            .addOnSuccessListener {
                callback.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error adding game: ${e.message}")
                callback.invoke(false)
            }
    }

    fun insertList(listEntity: ListEntity, callback: (Boolean) -> Unit) {
        val userUid = FirebaseInstances.authInstance.uid.toString()
        val listsCollectionRef = FirebaseInstances.firestoreInstance
            .collection("users")
            .document(userUid)
            .collection("lists")

        val listMap = listEntity.toMap()

        listsCollectionRef.document(listEntity.id.toString()).set(listMap)
            .addOnSuccessListener {
                callback.invoke(true)
            }
            .addOnFailureListener {  e ->
                Log.d("FirestoreUtilities", "Error adding list: ${e.message}")
                callback.invoke(false)
            }
    }

}