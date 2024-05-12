package com.thrashspeed.gamecore.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.screens.viewmodels.AuthViewModel
import com.thrashspeed.gamecore.utils.Fonts

@Composable
fun AuthScreen(authCallback: () -> Unit, viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
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
            val (titleText, textFieldsContainer, changeActionModeButton, googleLogInButton) = createRefs()
            var emailInput by remember { mutableStateOf("") }
            var fullnameInput by remember { mutableStateOf("") }
            var usernameInput by remember { mutableStateOf("") }
            var passwordInput by remember { mutableStateOf("") }
            var passwordConfirmationInput by remember { mutableStateOf("") }

            TitleText(
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top, 70.dp)
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
                    viewModel = viewModel,
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
                authCallback = authCallback,
                viewModel = viewModel
            )
        }
    }
}

//@Composable
//fun GitHubButton(modifier: Modifier = Modifier) {
//    Button(
//        onClick = { /*TODO*/ },
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.background,
//            contentColor = MaterialTheme.colorScheme.onBackground
//        ),
//        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
//        modifier = modifier
//    ) {
//        Row (
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(imageVector = Icons.Default.Translate, contentDescription = "translateButton")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(LocalContext.current.getString(R.string.languageBtnText))
//        }
//    }
//}

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
fun ActionButton(context: Context, viewModel: AuthViewModel, modifier: Modifier = Modifier, containerColor: Color, contentColor: Color, isRegistering: Boolean, authCallback: () -> Unit, email: String, password: String, passwordConfirmation: String, username: String, fullName: String) {
    Button(
        onClick = {
            if (isRegistering) {
                viewModel.signUp(context, authCallback, email, password, passwordConfirmation, username, fullName)
            } else {
                viewModel.loginUser(context, authCallback, username, password)
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
fun GoogleLogInButton(modifier: Modifier = Modifier, authCallback: () -> Unit, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    viewModel.firebaseAuthWithGoogle(context, account.idToken!!, authCallback)
                }
            } catch (e: ApiException) {
                Log.d("GoogleSignIn", "signInGoogle:failure ${e.message}")
            }
        }
    }

    Button(
        onClick = { viewModel.signInWithGoogle(context, googleSignInLauncher) },
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
