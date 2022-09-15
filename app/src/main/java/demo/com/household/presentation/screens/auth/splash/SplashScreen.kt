package demo.com.household.presentation.screens.auth.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.data.Constants
import demo.com.household.presentation.NavigationDestination
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.CharlestonGreen

@Composable
fun SplashScreen(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val showButtons = remember {
        mutableStateOf(false)
    }
    val state = viewModel.state.value
    if (state.notLogin) {
        LaunchedEffect(Unit) {
            showButtons.value = true

        }
    }


    state.login?.let {
        LaunchedEffect(Unit) {
            onNavigate(NavigationDestination.Home)
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(BrinkPink),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo_splash), contentDescription = "")
        Spacer(modifier = Modifier.height(18.dp))

        AnimatedVisibility(visible = showButtons.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(170.dp))
                Button(
                    modifier = Modifier.width(250.dp),
                    onClick = {
                        onNavigate(NavigationDestination.Login)
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(CharlestonGreen),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login_as_a_user),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    modifier = Modifier.width(250.dp),
                    onClick = {
                        onNavigate(NavigationDestination.Login)
                        viewModel.resetState()

                    },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login_as_admin),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = BrinkPink
                    )
                }
            }
        }

    }
}