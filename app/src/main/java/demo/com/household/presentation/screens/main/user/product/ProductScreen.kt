package demo.com.household.presentation.screens.main.user.product

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import demo.com.household.R
import demo.com.household.data.Product
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.screens.main.user.products.ProductsViewModel
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.ChineseSliver2
import demo.com.household.ui.theme.TextColor

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductScreen(
    productID: String,
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }

    var product by remember {
        mutableStateOf<Product?>(null)
    }
    viewModel.stateProducts.value.data?.let {
        LaunchedEffect(Unit) {
            product = it
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getProducts(productID)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBarWithBack(title = product?.name.toString(), onBack = onBack)
        Spacer(modifier = Modifier.height(20.dp))
        product?.images?.let { ImageSlider(it) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product?.name.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = product?.price.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = BrinkPink
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            color = ChineseSliver2, modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = product?.description.toString(),
            fontSize = 13.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            color = ChineseSliver2, modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.quntity),
            fontSize = 17.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        var numQuantity by remember {
            mutableStateOf("1")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {

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
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            onClick = {
                viewModel.addProductToCart(
                    product,
                    numQuantity
                )
            },
            colors = ButtonDefaults.buttonColors(BrinkPink),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_cart),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
    HandelResonances()
    val context = LocalContext.current

    viewModel.stateAddCart.value.data?.let {
        LaunchedEffect(Unit){
            Toast.makeText(context, "Product Added To Cart", Toast.LENGTH_SHORT)
                .show()
            onBack()
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@ExperimentalPagerApi
@Composable
fun ImageSlider(
    images: List<String>,
) {

    val pagerState = rememberPagerState()
    Card(
        modifier = Modifier
            .height(230.dp)
            .fillMaxWidth()
    ) {

        Box(Modifier.fillMaxSize()) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
            ) { page ->
                AsyncImage(
                    model = images[page], contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }
            PagerIndicator(
                pagerState = pagerState,
                activeColor = BrinkPink,
                inactiveColor = Color.White,
                activeIndicatorHeight = 8.dp,
                activeIndicatorWidth = 8.dp,
                inactiveIndicatorHeight = 8.dp,
                inactiveIndicatorWidth = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            )
        }

    }


}


@Composable
fun HandelResonances(viewModel: ProductViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateProducts.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateProducts.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (viewModel.stateAddCart.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddCart.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    ProgressBar(
        isShow = viewModel.stateProducts.value.isLoading||
                viewModel.stateProducts.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}