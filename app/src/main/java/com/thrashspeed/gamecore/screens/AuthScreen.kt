package com.thrashspeed.gamecore.screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.config.FirebaseConectionData
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import com.thrashspeed.gamecore.firebase.firestore.FirestoreUtilities
import com.thrashspeed.gamecore.utils.Fonts

interface AuthCallback {
    fun onAuthSuccess()
}

@Composable
fun AuthScreen(authCallback: AuthCallback) {
    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSecondayContainer = MaterialTheme.colorScheme.secondaryContainer
    val colorOnPrimary = MaterialTheme.colorScheme.onPrimary
    val colorOnSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer

    var isRegistering by remember { mutableStateOf(true) }

    var textFieldsBoxColor by remember { mutableStateOf(colorSecondayContainer) }
    var boxButtonContentColor by remember { mutableStateOf(colorOnSecondaryContainer) }
    var changeActionModeButtonColor by remember { mutableStateOf(colorPrimary) }
    var changeActionModeButtonContentColor by remember { mutableStateOf(colorOnPrimary) }

    val backgroundImage: Painter = painterResource(R.drawable.nes_wallpaper)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (languageButton, titleText, textFieldsContainer, changeActionModeButton, googleLogInButton) = createRefs()
            var emailInput by remember { mutableStateOf("") }
            var fullnameInput by remember { mutableStateOf("") }
            var usernameInput by remember { mutableStateOf("") }
            var passwordInput by remember { mutableStateOf("") }
            var passwordConfirmationInput by remember { mutableStateOf("") }

            LanguageButton(
                modifier = Modifier.constrainAs(languageButton) {
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

            TitleText(
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(languageButton.bottom, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .constrainAs(textFieldsContainer) {
                        top.linkTo(titleText.bottom)
                        bottom.linkTo(changeActionModeButton.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    }
                    .padding(16.dp)
                    .border(
                        border = BorderStroke(2.dp, textFieldsBoxColor),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                if (isRegistering) {
                    EmailTextField(
                        emailInput = emailInput,
                        onEmailInputChange = { emailInput = it },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )

                    FullnameTextField(
                        fullnameInput = fullnameInput,
                        onFullnameInputChange = { fullnameInput = it },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }

                UsernameTextField(
                    usernameInput = usernameInput,
                    onUsernameInputChange = { usernameInput = it },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                PasswordTextField(
                    isConfirmationField = false,
                    passwordInput = passwordInput,
                    onPasswordInputChange = { passwordInput = it },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                if (isRegistering) {
                    PasswordTextField(
                        isConfirmationField = true,
                        passwordInput = passwordConfirmationInput,
                        onPasswordInputChange = { passwordConfirmationInput = it },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }

                ActionButton(
                    context = LocalContext.current,
                    containerColor = textFieldsBoxColor,
                    contentColor = boxButtonContentColor,
                    isRegistering = isRegistering,
                    authCallback = authCallback,
                    email = emailInput,
                    password = passwordInput,
                    passwordConfirmation = passwordConfirmationInput,
                    username = usernameInput,
                    fullName = fullnameInput
                )
            }

            ChangeActionModeButton(
                onAction = {
                    isRegistering = !isRegistering
                    textFieldsBoxColor = if (isRegistering) colorSecondayContainer else colorPrimary
                    boxButtonContentColor = if (isRegistering) colorOnSecondaryContainer else colorOnPrimary
                    changeActionModeButtonColor = if (isRegistering) colorPrimary else colorSecondayContainer
                    changeActionModeButtonContentColor = if (isRegistering) colorOnPrimary else colorOnSecondaryContainer
                },
                isRegistering,
                modifier = Modifier
                    .constrainAs(changeActionModeButton) {
                    bottom.linkTo(googleLogInButton.top, 16.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
                changeActionModeButtonColor,
                changeActionModeButtonContentColor,
            )

            GoogleLogInButton(
                modifier = Modifier.constrainAs(googleLogInButton) {
                    bottom.linkTo(parent.bottom, 96.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
                authCallback = authCallback
            )
        }
    }
}

@Composable
fun LanguageButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
        modifier = modifier
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Translate, contentDescription = "translateButton")
            Spacer(modifier = Modifier.width(8.dp))
            Text(LocalContext.current.getString(R.string.languageBtnText))
        }
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier) {
    Text(
        "GameCore",
        color = Color.Yellow,
        fontSize = 48.sp,
        fontFamily = Fonts.galactica,
        modifier = modifier
    )
}

@Composable
fun EmailTextField(
    emailInput: String,
    onEmailInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = emailInput,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.None, false, KeyboardType.Email, ImeAction.Next),
        onValueChange = onEmailInputChange,
        singleLine = true,
        label = { Text(LocalContext.current.getString(R.string.emailInputPlaceholder)) },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun FullnameTextField(
    fullnameInput: String,
    onFullnameInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = fullnameInput,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words, false, KeyboardType.Text, ImeAction.Next),
        onValueChange = onFullnameInputChange,
        singleLine = true,
        label = { Text(LocalContext.current.getString(R.string.fullnameInputPlaceholder)) },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun UsernameTextField(
    usernameInput: String,
    onUsernameInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = usernameInput,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.None, false, KeyboardType.Text, ImeAction.Next),
        onValueChange = onUsernameInputChange,
        singleLine = true,
        label = { Text(LocalContext.current.getString(R.string.usernameInputPlaceholder)) },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    isConfirmationField: Boolean = false,
    passwordInput: String,
    onPasswordInputChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = passwordInput,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,
            unfocusedPlaceholderColor = Color.Gray,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        keyboardOptions = KeyboardOptions(KeyboardCapitalization.None, false, KeyboardType.Password, ImeAction.Next),
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = onPasswordInputChange,
        singleLine = true,
        label = {
            Text(
                text = if (isConfirmationField) LocalContext.current.getString(R.string.passwordConfirmationPlaceHolder) else LocalContext.current.getString(R.string.passwordInputPlaceholder)
            )
        },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun ActionButton(context: Context, modifier: Modifier = Modifier, containerColor: Color, contentColor: Color, isRegistering: Boolean, authCallback: AuthCallback, email: String, password: String, passwordConfirmation: String, username: String, fullName: String) {
    Button(
        onClick = {
            if (isRegistering) {
                signUp(context, authCallback, email, password, passwordConfirmation, username, fullName)
            } else {
                loginUser(context, authCallback, username, password)
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer),
        modifier = modifier
    ) {
        Text(
            text = if (isRegistering) LocalContext.current.getString(R.string.signUpBtnText) else LocalContext.current.getString(R.string.logInBtnText)
        )
    }
}

@Composable
fun ChangeActionModeButton(onAction: () -> Unit, isRegistering: Boolean, modifier: Modifier = Modifier, containerColor: Color, contentColor: Color) {
    Button(
        onClick = onAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp, 0.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isRegistering) Icons.AutoMirrored.Filled.Login else Icons.Default.PersonAdd,
                contentDescription = "changeActionModeButton",
                tint = contentColor
            )
            Text(
                text = if (isRegistering) LocalContext.current.getString(R.string.logInBtnText) else LocalContext.current.getString(R.string.signUpBtnText),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GoogleLogInButton(modifier: Modifier = Modifier, authCallback: AuthCallback) {
    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(context, account.idToken!!, authCallback)
                }
            } catch (e: ApiException) {
                Log.d("GoogleSignIn", "signInGoogle:failure ${e.message}")
            }
        }
    }

    Button(
        onClick = { signInWithGoogle(context, googleSignInLauncher) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(
            2.dp, MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp, 0.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "googleLogIn",
                tint = Color.Unspecified
            )
            Text(
                text = context.getString(R.string.googleLogInBtnText),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview()
@Composable
fun AuthScreenPreview() {
    AuthScreen(object : AuthCallback {
        override fun onAuthSuccess() {
            // This is a fake implementation of the auth callback for the preview that does nothing
        }
    })
}

private fun signInWithGoogle(context: Context, googleSignInLauncher: ActivityResultLauncher<Intent>) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(FirebaseConectionData.GOOGLE_WEB_CLIENT_ID)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()

    val signInIntent = googleSignInClient.signInIntent
    googleSignInLauncher.launch(signInIntent)
}

private fun signUp(context: Context, authCallback: AuthCallback, email: String, password: String, passwordConfirmation: String, username: String, fullname: String) {
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

private fun loginUser(context: Context, authCallback: AuthCallback, username: String, password: String) {
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

private fun firebaseAuthWithGoogle(context: Context, idToken: String, authCallback: AuthCallback) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    FirebaseInstances.authInstance.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("GoogleSignIn", "signInWithCredential:success")
                val user = FirebaseInstances.authInstance.currentUser
                saveUserInfo(context, user?.email.toString(), user?.displayName.toString(), user?.displayName.toString())
                authCallback.onAuthSuccess()
            } else {
                Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                Toast.makeText(context, "error when logging in with google: ${task.exception}", Toast.LENGTH_LONG).show()
            }
        }
}

fun saveUserInfo(context: Context, email: String, username: String, fullName: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    editor.putString("email", email)
    editor.putString("username", username)
    editor.putString("fullname", fullName)
    editor.apply()
}