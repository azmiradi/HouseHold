package demo.com.household.presentation.screens.main.user.payment

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.CustomTextInput
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.presentation.share_componennt.SampleSpinner
import demo.com.household.ui.theme.BrinkPink

@Composable
fun PaymentScreen(
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
    cartID: String,
) {
    Column(
        Modifier.fillMaxSize(),
    ) {
        val cardNum = remember {
            mutableStateOf("")
        }

        val prefix = remember {
            mutableStateOf("")
        }
        val totalAmount by remember {
            mutableStateOf<String>("0")
        }
        val month = remember {
            mutableStateOf("")
        }
        val year = remember {
            mutableStateOf("")
        }
        TopBarWithBack(
            title = stringResource(id = R.string.cart),
            onBack = onBack
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.knet_logo),
                contentDescription = "",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(37.dp))

            SampleSpinner(
                hint = "Select your bank",
                list = listOf(
                    Pair("CIB", "CIB"),
                    Pair("EgyBank", "EgyBank"),
                    Pair("Al Ahly Bank", "Al Ahly Bank")
                ), onSelectionChanged = {


                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row() {
                CustomTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    hint = "Prefix", mutableState = prefix
                )
                Spacer(modifier = Modifier.width(9.dp))

                CustomTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    hint = "Card number", mutableState = cardNum
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row() {
                CustomTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    hint = "MM", mutableState = month
                )
                Spacer(modifier = Modifier.width(9.dp))

                CustomTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    hint = "YYYY", mutableState = year
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.LightGray)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Products Amounts",
                    color = Color.LightGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(15.dp)
                )

                Text(
                    text = totalAmount,
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(15.dp)

                )
            }
            Spacer(modifier = Modifier.height(100.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.updateCart()
                },
                colors = ButtonDefaults.buttonColors(BrinkPink),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }


    }
    val context = LocalContext.current
    viewModel.stateRequest.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT)
                .show()
            onNavigate(NavigationDestination.Home)
        }
    }
    HandelResonances()
}


@Composable
fun HandelResonances(viewModel: PaymentViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateRequest.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateRequest.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }


    ProgressBar(
        isShow = viewModel.stateRequest.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}