package com.thrashspeed.gamecore.screens

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
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.utils.Fonts

@Composable
fun AuthScreen() {
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

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.constrainAs(languageButton) {
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
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

            Text(
                "GameCore",
                color = Color.Yellow,
                fontSize = 48.sp,
                fontFamily = Fonts.galactica,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(languageButton.bottom, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

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
                onValueChange = { usernameInput = it },
                singleLine = true,
                label = { Text(LocalContext.current.getString(R.string.usernameInputPlaceholder)) },
                modifier = Modifier.constrainAs(userTextField) {
                    top.linkTo(titleText.bottom, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }.width(280.dp)
            )

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
                onValueChange = { passwordInput = it },
                singleLine = true,
                label = { Text(LocalContext.current.getString(R.string.passwordInputPlaceholder)) },
                modifier = Modifier.constrainAs(passwordTextField) {
                    top.linkTo(userTextField.bottom, 8.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }.width(280.dp)
            )

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier
                    .constrainAs(logInButton) {
                        top.linkTo(passwordTextField.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(text = LocalContext.current.getString(R.string.logInBtnText))
            }

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .constrainAs(signUpButton) {
                        bottom.linkTo(googleLogInButton.top, 16.dp)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    }
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

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .constrainAs(googleLogInButton) {
                        bottom.linkTo(parent.bottom, 96.dp)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    }
                    .fillMaxWidth()
                    .padding(32.dp, 0.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(R.drawable.google_logo), contentDescription = "googleLogIn", tint = Color.Unspecified)
                    Text(
                        text = LocalContext.current.getString(R.string.googleLogInBtnText),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview()
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}