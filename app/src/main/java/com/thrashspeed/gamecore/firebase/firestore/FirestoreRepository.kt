package com.thrashspeed.gamecore.firebase.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.thrashspeed.gamecore.data.model.GameCover
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.data.model.GameStatus
import com.thrashspeed.gamecore.data.model.ListEntity
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import kotlinx.coroutines.tasks.await

/**
 * Clase singleton que proporciona utilidades para interactuar con Firestore en Firebase.
 */
object FirestoreRepository {

    private fun getGamesCollection(): CollectionReference? {
        val uid = FirebaseInstances.authInstance.currentUser?.uid
        return uid?.let {
            FirebaseInstances.firestoreInstance
                .collection("users")
                .document(uid)
                .collection("games")
        }
    }

    private fun getListsCollection(): CollectionReference? {
        val uid = FirebaseInstances.authInstance.currentUser?.uid
        return uid?.let {
            FirebaseInstances.firestoreInstance
                .collection("users")
                .document(uid.toString())
                .collection("lists")
        }
    }

    suspend fun getGamesAsList(): List<GameEntity> {
        val gamesCollection = getGamesCollection()
        val gamesList = mutableListOf<GameEntity>()

        try {
            gamesCollection?.get()?.await()?.documents?.forEach { document ->
                val game = GameEntity(
                    id = document.getLong("id") ?: 0,
                    name = document.getString("name") ?: "",
                    releaseDate = document.getLong("releaseDate") ?: 0,
                    coverImageUrl = document.getString("coverImageUrl") ?: "",
                    genres = document.getString("genres") ?: "",
                    status = GameStatus.valueOf(document.getString("status") ?: "TO_PLAY"),
                    sessionStartedTempDate = document.getLong("sessionStartedTempDate") ?: 0,
                    lastSessionTimePlayed = document.getLong("lastSessionTimePlayed") ?: 0,
                    timePlayed = document.getLong("timePlayed") ?: 0,
                    firstDayOfPlay = document.getLong("firstDayOfPlay") ?: 0,
                    dayOfCompletion = document.getLong("dayOfCompletion") ?: 0
                )
                game.let { gamesList.add(it) }
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting games: ${e.message}", e)
        }

        return gamesList
    }

    suspend fun getListsAsList(): List<ListEntity> {
        val listsCollection = getListsCollection()
        val listsList = mutableListOf<ListEntity>()

        try {
            val querySnapshot = listsCollection?.get()?.await()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getLong("id") ?: 0
                val name = document.getString("name") ?: ""
                val description = document.getString("description") ?: ""
                val gamesList = document.get("games") as? List<HashMap<String, Any>> ?: emptyList()

                val games = gamesList.map { gameMap ->
                    val gameId = gameMap["id"] as? Long ?: 0
                    val gameName = gameMap["name"] as? String ?: ""
                    val cover = gameMap["cover"] as? HashMap<String, Any>? ?: hashMapOf("id" to -1, "image_id" to "")
                    val firstReleaseDate = gameMap["first_release_date"] as? Long ?: 0
                    GameItem(id = gameId, name = gameName, cover = GameCover(cover["id"].toString().toInt(), cover["image_id"].toString()), first_release_date = firstReleaseDate)
                }

                val list = ListEntity(id = id, name = name, description = description, games = games)
                listsList.add(list)
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting lists: ${e.message}", e)
        }

        return listsList
    }


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
                Log.d("xd", "${it.message}")
                callback(false)
            }
    }

    fun insertGame(gameEntity: GameEntity, callback: (Boolean) -> Unit) {
        val gameMap = gameEntity.toMap()

        getGamesCollection()?.document(gameEntity.id.toString())?.set(gameMap)
            ?.addOnSuccessListener {
                callback.invoke(true)
            }
            ?.addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error adding game: ${e.message}")
                callback.invoke(false)
            }
    }

    fun updateGame(gameId: Long, fields: Map<String, Any>, callback: (Boolean) -> Unit) {
        getGamesCollection()?.document(gameId.toString())?.update(fields)
            ?.addOnSuccessListener {
                callback.invoke(true)
            }
            ?.addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error updating game: ${e.message}")
                callback.invoke(false)
            }
    }

    fun deleteGame(gameEntity: GameEntity, callback: (Boolean) -> Unit) {
        val gameDocRef = getGamesCollection()?.document(gameEntity.id.toString())

        gameDocRef?.delete()
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error deleting game: ${e.message}")
                callback(false)
            }
    }

    fun insertList(listEntity: ListEntity, callback: (Boolean) -> Unit = {}) {
        val listMap = listEntity.toMap()

        getListsCollection()?.document(listEntity.id.toString())?.set(listMap)
            ?.addOnSuccessListener {
                callback.invoke(true)
            }
            ?.addOnFailureListener {  e ->
                Log.d("FirestoreUtilities", "Error adding list: ${e.message}")
                callback.invoke(false)
            }
    }

    fun updateList(listId: Long, fields: Map<String, Any>, callback: (Boolean) -> Unit = {}) {
        getListsCollection()?.document(listId.toString())?.update(fields)
            ?.addOnSuccessListener {
                callback.invoke(true)
            }
            ?.addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error updating list: ${e.message}")
                callback.invoke(false)
            }
    }

    fun deleteList(listEntity: ListEntity, callback: (Boolean) -> Unit = {}) {
        val listDocRef = getListsCollection()?.document(listEntity.id.toString())

        listDocRef?.delete()
            ?.addOnSuccessListener {
                callback(true)
            }
            ?.addOnFailureListener { e ->
                Log.d("FirestoreUtilities", "Error deleting list: ${e.message}")
                callback(false)
            }
    }

}