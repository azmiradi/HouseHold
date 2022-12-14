package demo.com.household.presentation.screens.auth.splash

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.share_componennt.UIRequirePermissions
import demo.com.household.ui.theme.BrinkPink

@Composable
fun SplashScreen(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    if (state.notLogin) {
        LaunchedEffect(Unit) {
            onNavigate(NavigationDestination.Login)
            viewModel.resetState()
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


    }
}