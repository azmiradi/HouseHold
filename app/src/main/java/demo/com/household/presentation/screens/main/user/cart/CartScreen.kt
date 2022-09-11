package demo.com.household.presentation.screens.main.user.cart

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
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
import com.google.accompanist.pager.ExperimentalPagerApi
import demo.com.household.R
import demo.com.household.data.Cart
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.TextColor

@Composable
fun CartScreenScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    var carts by remember {
        mutableStateOf<List<Cart>>(ArrayList())
    }
    viewModel.stateCart.value.data?.let {
        LaunchedEffect(Unit) {
            carts = it
        }
    }

    viewModel.stateDeleteCart.value.data?.let {
        LaunchedEffect(Unit) {
            viewModel.getCarts()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getCarts()
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

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(end = 16.dp, start = 16.dp)
                .weight(1f)
        ) {
            items(carts) {
                CartItem(cart = it) {
                    viewModel.deleteCart(it.product?.productID)
                }
            }
        }

    }
    HandelResonances()


}


@Composable
fun CartItem(cart: Cart, onDelete: () -> Unit) {
     Row(
        Modifier
            .fillMaxWidth()
            .clickable {

            }
            .clip(RoundedCornerShape(3.dp))
            .border(BorderStroke(1.dp, Color.Gray))
    ) {
        SubcomposeAsyncImage(
            model = cart.product?.images?.get(0).toString(),
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
                text = cart.product?.name.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Start
            )


            Text(
                text = cart.product?.price.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Start
            )

            Row(
                verticalAlignment = Alignment.CenterVertically) {
                var numQuantity by remember {
                    mutableStateOf(cart.count ?: "1")
                }
                IconButton(onClick = {
                    numQuantity = (numQuantity.toInt() + 1).toString()
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

