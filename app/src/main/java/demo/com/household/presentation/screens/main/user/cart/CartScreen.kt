package demo.com.household.presentation.screens.main.user.cart

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import demo.com.household.R
import demo.com.household.data.Constants
import demo.com.household.data.Product
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.TextColor

@Composable
fun CartScreenScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination, String) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    val products = remember {
        mutableStateListOf<Product>()
    }

    var totalAmount by remember {
        mutableStateOf<String>("0")
    }
    var cartID by remember {
        mutableStateOf<String>("0")
    }
    viewModel.stateCart.value.data?.let {
        LaunchedEffect(Unit) {
            cartID = it.second
            products.clear()
            products.addAll(it.first)
            products.forEach {
                totalAmount =
                    (((it.price ?: 0) * (it.count ?: "0").toInt())
                            + totalAmount.toInt())
                        .toString()
            }
        }
    }


    viewModel.stateDeleteCart.value.data?.let {
        LaunchedEffect(Unit) {
            totalAmount = "0"
            viewModel.resetState()
            viewModel.getCarts()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getCarts()
    }
    val checkDeliveryDrone = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBarWithBack(
            title = stringResource(id = R.string.cart),
            onBack = onBack
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(colors = CheckboxDefaults.colors(
                checkmarkColor = Color.White,
                checkedColor = BrinkPink,
            ), checked = checkDeliveryDrone.value, onCheckedChange = {
                checkDeliveryDrone.value = !checkDeliveryDrone.value
                totalAmount = if (it) {
                    (totalAmount.toFloat() + 5).toString()
                } else {
                    (totalAmount.toFloat() - 5).toString()
                }
            })
            Text(
                text = "Deliver by Drone",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
             )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(end = 16.dp, start = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(products) { index, product ->
                CartItem(cart = product, onDelete = {
                    viewModel.deleteCart(index.toString(), cartID = cartID)
                }) {
                    product.count = it
                    totalAmount = "0"
                    products.forEach {
                        totalAmount =
                            (((it.price ?: 0) * (it.count ?: "0").toInt()) +
                                    totalAmount.toInt())
                                .toString()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp)
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
                text = "$totalAmount ${Constants.CURRENCY}",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(15.dp)

            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            onClick = {

                if (products.isNotEmpty()) {
                    if (checkDeliveryDrone.value){
                        viewModel.updateCart()
                        onNavigate(NavigationDestination.Purchase, totalAmount)
                        viewModel.resetState()
                    }
                    else{
                        onNavigate(NavigationDestination.Purchase, totalAmount)
                        viewModel.resetState()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(BrinkPink),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "CONFIRM PURCHASE",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(59.dp))


    }
    HandelResonances()


}


@Composable
fun CartItem(
    cart: Product, onDelete: () -> Unit,
    onCountChange: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {

            }
            .clip(RoundedCornerShape(3.dp))
            .border(BorderStroke(1.dp, Color.LightGray))
    ) {
        SubcomposeAsyncImage(
            model = cart.images?.get(0).toString(),
            contentDescription = "",
            modifier = Modifier
                .size(105.dp),
            loading = {
                CircularProgressIndicator(
                    color = BrinkPink,
                    modifier = Modifier.size(25.dp)
                )
            },
            error = {
                Image(imageVector = Icons.Default.Error, contentDescription = "")
            },
            contentScale = ContentScale.FillBounds,

            )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = cart.name.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Start
            )


            Text(
                text = " ${cart.price.toString()} ${Constants.CURRENCY}",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Start
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var numQuantity by remember {
                    mutableStateOf(cart.count ?: "1")
                }
                IconButton(onClick = {
                    numQuantity = (numQuantity.toInt() + 1).toString()
                    onCountChange(numQuantity)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.add), contentDescription = ""
                    )
                }
                Text(
                    text = numQuantity,
                    Modifier.background(TextColor),
                    fontSize = 12.sp,
                    color = Color.Black
                )
                IconButton(onClick = {
                    if (numQuantity.toInt() > 1)
                        numQuantity = (numQuantity.toInt() - 1).toString()
                    onCountChange(numQuantity)

                }) {
                    Image(
                        painter = painterResource(id = R.drawable.menus), contentDescription = ""
                    )
                }
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete, contentDescription = "",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun HandelResonances(viewModel: CartViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateCart.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateCart.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (viewModel.stateDeleteCart.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateDeleteCart.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    ProgressBar(
        isShow = viewModel.stateCart.value.isLoading ||
                viewModel.stateDeleteCart.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}

