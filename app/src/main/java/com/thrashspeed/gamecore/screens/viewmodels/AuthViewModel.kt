package com.thrashspeed.gamecore.screens.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.config.FirebaseConectionData
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import com.thrashspeed.gamecore.firebase.firestore.FirestoreUtilities
import com.thrashspeed.gamecore.screens.AuthCallback

class AuthViewModel: ViewModel() {
    fun signInWithGoogle(context: Context, googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(FirebaseConectionData.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()

        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    fun signUp(context: Context, authCallback: AuthCallback, email: String, password: String, passwordConfirmation: String, username: String, fullname: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showAlert(context,"Rellena todos los campos porfavor.")
            return
        }

        if (password != passwordConfirmation) {
            showAlert(context,"Las contraseñas no son iguales")
            return
        }

        if (password.length > 6) {
            showAlert(context, "La contraseña debe tener al menos 6 caracteres")
            return
        }

        val usernames = FirebaseInstances.firestoreInstance.collection("usernames")

        usernames.document(username)
            .get().addOnSuccessListener { document ->
                if(document.exists()){
                    showAlert(context, "El nombre de usuario introducido ya está en uso por otra cuenta")
                } else {
                    FirebaseInstances.authInstance.createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirestoreUtilities.saveUserInFirestore(email, username, fullname) { success ->
                                if (success) {
                                    saveUserInfo(context, email, username, fullname)
                                    authCallback.onAuthSuccess()
                                } else {
                                    showAlert(context, "Error al guardar el usuario en la base de datos")
                                }
                            }
                        } else {
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                showAlert(context, "El email introducido ya está en uso por otra cuenta")
                            } else {
                                showAlert(context, task.exception.toString())
                            }
                        }
                    }
                }
            }
    }

    fun loginUser(context: Context, authCallback: AuthCallback, username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            return
        }

        val usernames = FirebaseInstances.firestoreInstance.collection("usernames")
        val users = FirebaseInstances.firestoreInstance.collection("users")

        usernames.document(username)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userEmail = document.getString("email").toString()

                    if (userEmail.isNotBlank()) {
                        val auth = FirebaseInstances.authInstance
                        auth.signInWithEmailAndPassword(userEmail, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    users.document(auth.currentUser?.uid ?: "")
                                        .get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                saveUserInfo(context, userEmail, username, document.getString("fullname").toString())
                                                authCallback.onAuthSuccess()
                                            }
                                        }
                                } else {
                                    handleAuthException(context, task.exception)
                                }
                            }
                    } else {
                        showAlert(context , "Error al obtener el email del usuario.")
                    }
                } else {
                    showAlert(context, "Nombre de usuario o contraseña incorrectos.")
                }
            }
            .addOnFailureListener { exception ->
                showAlert(context, "Error al verificar las credenciales: ${exception.message}")
            }
    }

    private fun handleAuthException(context: Context, exception: Exception?) {
        if (exception is FirebaseAuthInvalidCredentialsException) {
            showAlert(context, "Nombre de usuario o contraseña incorrectos.")
        } else {
            showAlert(context, exception?.message ?: "Se produjo un error al iniciar sesión")
        }
    }

    private fun showAlert(context: Context, error: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(error)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun firebaseAuthWithGoogle(context: Context, idToken: String, authCallback: AuthCallback) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseInstances.authInstance.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "signInWithCredential:success")
                    val user = FirebaseInstances.authInstance.currentUser
                    FirestoreUtilities.saveUserInFirestore(user?.email.toString(), user?.displayName.toString(), user?.displayName.toString()) { success ->
                        if (success) {
                            saveUserInfo(context, user?.email.toString(), user?.displayName.toString(), user?.displayName.toString())
                            authCallback.onAuthSuccess()
                        } else {
                            showAlert(context, "Error al guardar el usuario en la base de datos")
                        }
                    }
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "error when logging in with google: ${task.exception}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserInfo(context: Context, email: String, username: String, fullName: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putString("fullname", fullName)
        editor.apply()
    }
}