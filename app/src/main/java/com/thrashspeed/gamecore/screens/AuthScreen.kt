package com.thrashspeed.gamecore.screens

import android.app.Activity
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.config.FirebaseConectionData
import com.thrashspeed.gamecore.firebase.FirebaseInstances
import com.thrashspeed.gamecore.utils.Fonts

interface AuthCallback {
    fun onAuthSuccess()
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

@Composable
fun AuthScreen(authCallback: AuthCallback) {
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
            val (languageButton, titleText, userTextField, passwordTextField, logInButton, signUpButton, googleLogInButton) = createRefs()
            var usernameInput by remember { mutableStateOf("") }
            var passwordInput by remember { mutableStateOf("") }

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

            UsernameTextField(
                usernameInput = usernameInput,
                onUsernameInputChange = { usernameInput = it },
                modifier = Modifier.constrainAs(userTextField) {
                    top.linkTo(titleText.bottom, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

            PasswordTextField(
                passwordInput = passwordInput,
                onPasswordInputChange = { passwordInput = it },
                modifier = Modifier.constrainAs(passwordTextField) {
                    top.linkTo(userTextField.bottom, 8.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

            LogInButton(
                modifier = Modifier.constrainAs(logInButton) {
                    top.linkTo(passwordTextField.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            SignUpButton(
                modifier = Modifier.constrainAs(signUpButton) {
                    bottom.linkTo(googleLogInButton.top, 16.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
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
            unfocusedLabelColor = Color.White,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        onValueChange = onUsernameInputChange,
        singleLine = true,
        label = { Text(LocalContext.current.getString(R.string.usernameInputPlaceholder)) },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun PasswordTextField(
    passwordInput: String,
    onPasswordInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = passwordInput,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            disabledBorderColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = onPasswordInputChange,
        singleLine = true,
        label = { Text(LocalContext.current.getString(R.string.passwordInputPlaceholder)) },
        modifier = modifier.width(280.dp)
    )
}

@Composable
fun LogInButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer),
        modifier = modifier
    ) {
        Text(text = LocalContext.current.getString(R.string.logInBtnText))
    }
}

@Composable
fun SignUpButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
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
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "signUp", tint = Color.White)
            Text(
                text = LocalContext.current.getString(R.string.signUpBtnText),
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