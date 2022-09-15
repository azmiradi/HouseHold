package demo.com.household.presentation.screens.main.user.my_orders

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.data.Cart
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.*

@Composable
fun MyOrdersScreen(
    viewModel: MyOrdersViewModel = hiltViewModel(),
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
            title = "My orders",
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
                MyOrderItem(cart = cart)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


    }
    HandelResonances()


}


@Composable
fun MyOrderItem(
    cart: Cart
) {
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
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(ChineseSliver3)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(TitleHeader),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                Modifier
                    .fillMaxWidth().padding(15.dp)
                    .weight(1f)
            ) {
                Text(text = "Order number:", color = Color.Gray)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = cart.time.toString(), maxLines = 1 ,color = Color.Black)

            }

            Row(
                Modifier
                    .fillMaxWidth().padding(15.dp)
                    .weight(1f)
            ) {
                Text(text = "Total price", color = Color.Gray)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = totalAmount, color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(17.dp))

        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(5.dp),
                backgroundColor = if ((cart._status ?: 0) > 0) Malachite else ChineseSliver
            ) {
                Text(
                    text = "Ready",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(10.dp)
                )

            }

            Divider(
                thickness = 3.dp, modifier = Modifier.width(40.dp),
                color = if ((cart._status ?: 0) > 0) Malachite else ChineseSliver
            )

            Card(
                shape = RoundedCornerShape(5.dp),
                backgroundColor = if ((cart._status ?: 0) > 1) Malachite else ChineseSliver
            ) {
                Text(
                    text = "On way",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(10.dp)
                )

            }
            Divider(
                thickness = 3.dp, modifier = Modifier.width(40.dp),
                color = if ((cart._status ?: 0) > 1) Malachite else ChineseSliver
            )
            Card(
                shape = RoundedCornerShape(5.dp),
                backgroundColor = if ((cart._status ?: 0) > 2) Malachite else ChineseSliver
            ) {
                Text(
                    text = "Deliverd",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(10.dp)
                )

            }
        }
        Spacer(modifier = Modifier.height(17.dp))

    }
}

@Composable
fun HandelResonances(viewModel: MyOrdersViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateCart.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateCart.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }


    ProgressBar(
        isShow = viewModel.stateCart.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}

