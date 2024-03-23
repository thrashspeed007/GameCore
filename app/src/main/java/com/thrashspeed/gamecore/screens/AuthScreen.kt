package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.thrashspeed.gamecore.R

@Composable
fun AuthScreen() {
    val backgroundImage: Painter = painterResource(R.drawable.nes_wallpaper)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (titleText, userTextField) = createRefs()

            Text(
                "GameCore",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )

            OutlinedTextField(
                value = "",
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                onValueChange = {  },
                label = { Text("User") },
                placeholder = { Text("Placeholder") },
                modifier = Modifier.constrainAs(userTextField) {
                    top.linkTo(titleText.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )
        }
    }
}

@Preview()
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}