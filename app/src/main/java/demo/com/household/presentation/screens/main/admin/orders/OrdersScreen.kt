package demo.com.household.presentation.screens.main.admin.orders

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import demo.com.household.R
import demo.com.household.data.Cart
import demo.com.household.data.Constants
import demo.com.household.data.Product
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.presentation.share_componennt.SampleSpinner
import demo.com.household.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    val carts = remember {
        mutableStateListOf<Cart>()
    }


    viewModel.stateCart.value.data?.let {
        LaunchedEffect(Unit) {
            carts.clear()
            carts.addAll(it)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBarWithBack(
            title = "Orders",
            onBack = onBack
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(end = 16.dp, start = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(carts) { index, cart ->
                OrderItem(cart = cart)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


    }
    HandelResonances()


}


@Composable
fun OrderItem(
    cart: Cart,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val timeFormat = SimpleDateFormat("HH:mm:aa", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)

    val time = remember {
        timeFormat.format(cart.time.toString().toLong())
    }

    val date = remember {
        dateFormat.format(cart.time.toString().toLong())
    }

    var totalAmount by remember {
        mutableStateOf<String>("0")
    }
    LaunchedEffect(Unit) {
        cart.products?.forEach {
            totalAmount =
                (((it.price ?: 0) * (it.count ?: "0").toInt())
                        + totalAmount.toInt())
                    .toString()
        }
        if (cart.isDeliver)
            totalAmount=(totalAmount.toInt()+5).toString()
    }

    Card(
        backgroundColor = Color.White, border = BorderStroke(1.dp, Border),
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(TitleHeader),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cart.userName.toString(), color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp)
                )

                Text(
                    text = time + "\n" + date, color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(10.dp)

                )

            }
            Spacer(modifier = Modifier.height(17.dp))
            cart.products?.let { products ->
                products.forEach {
                    ProductItem(product = it)
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            Spacer(modifier = Modifier.height(17.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(BrinkPink2)
                    .padding(start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total", color = Color.Gray,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(12.dp)
                )

                Text(
                    text = "$totalAmount ${Constants.CURRENCY}",
                    fontSize = 17.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val status = remember {
                    mutableStateOf("1")
                }
                SampleSpinner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .weight(1f),
                    hint = "Update status", list = listOf(
                        Pair("Ready", "1"), Pair("On way", "2"), Pair("Delivered", "3")
                    ), onSelectionChanged = {
                        status.value = it
                    }
                )
                IconButton(
                    onClick = {
                        if (status.value.toInt() > 0) {
                            viewModel.updateCart(
                                cartID = cart.id.toString(),
                                userID = cart.userID.toString(),
                                status = status.value.toInt()
                            )
                        }
                    }, modifier =
                    Modifier
                        .padding(12.dp)
                        .background(BrinkPink)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.done),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

}

@Composable
fun ProductItem(product: Product) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp)
            .background(Header),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.images?.get(0).toString(),
            contentDescription = "",
            modifier = Modifier.size(35.dp)
        )

        Spacer(
            modifier = Modifier
                .width(15.dp)
                .padding(15.dp)
        )
        Text(
            text = product.name.toString(), color = Color.Gray,
            fontSize = 17.sp
        )
        Text(
            text = product.count + " x", color = BrinkPink,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "${((product.count ?: "0").toInt() * (product.price ?: 0)).toString()} ${Constants.CURRENCY}",
            color = Color.Black,
            fontSize = 17.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun HandelResonances(viewModel: OrdersViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateCart.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateCart.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateUpdate.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateUpdate.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    viewModel.stateUpdate.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT)
                .show()
        }
    }



    ProgressBar(
        isShow = viewModel.stateCart.value.isLoading ||
                viewModel.stateUpdate.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}

