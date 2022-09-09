package demo.com.household.presentation.screens.auth.signup

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.presentation.share_componennt.CustomTextInput
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.presentation.share_componennt.TextInputsPassword
import demo.com.household.R
import demo.com.household.ui.theme.BrinkPink

@Composable
fun SignupScreen(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    ProgressBar(
        isShow = viewModel.state.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
    val context = LocalContext.current
    if (viewModel.state.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.state.value.error, Toast.LENGTH_SHORT).show()
        }
    }
    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            viewModel.resetState()

            onNavigate(NavigationDestination.Home)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(end = 16.dp, start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "")
        Spacer(modifier = Modifier.height(18.dp))

        val emailInput = rememberSaveable() {
            mutableStateOf("")
        }
        val username = rememberSaveable() {
            mutableStateOf("")
        }
        val passwordInput = rememberSaveable() {
            mutableStateOf("")
        }
        val confirmPasswordInput = rememberSaveable() {
            mutableStateOf("")
        }

        val mobile = rememberSaveable() {
            mutableStateOf("")
        }

        CustomTextInput(
            hint = stringResource(id = R.string.username),
            mutableState = username, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.usernameInput.value
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomTextInput(
            hint = stringResource(id = R.string.email),
            mutableState = emailInput, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorEmail.value
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.mobile),
            mutableState = mobile, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.mobileInput.value,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(10.dp))
        TextInputsPassword(
            hint = stringResource(id = R.string.password),
            mutableState = passwordInput,
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorPassword.value
        )

        Spacer(modifier = Modifier.height(10.dp))
        TextInputsPassword(
            hint = stringResource(id = R.string.confirm_password),
            mutableState = confirmPasswordInput, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorConfirmPassword.value
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.signup(
                    email = emailInput.value,
                    password = passwordInput.value,
                    confirmPassword = confirmPasswordInput.value,
                    username = username.value,
                    mobile = mobile.value
                )
            },
            colors = ButtonDefaults.buttonColors(BrinkPink),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sign_up),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}