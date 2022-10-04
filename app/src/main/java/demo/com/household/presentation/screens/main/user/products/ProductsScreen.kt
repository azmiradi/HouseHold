package demo.com.household.presentation.screens.main.user.products

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.google.gson.Gson
import demo.com.household.R
import demo.com.household.data.Constants
import demo.com.household.data.Product
import demo.com.household.data.SubCategory
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.TapsColor

@Composable
fun ProductsScreen(
    categoryOb: String,
    viewModel: ProductsViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination, String) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    val subCategory = Gson().fromJson(categoryOb, SubCategory::class.java)

    val products = remember {
        mutableStateOf<List<Product>>(ArrayList())
    }
    viewModel.stateProducts.value.data?.let {
        LaunchedEffect(Unit) {
            products.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getProducts(subCategory.id.toString())
    }
    Column {
        TopBarWithBack(title = subCategory.name.toString(), onBack = onBack)
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(products.value) { item ->
                ProductItem(productItem = item) {
                    onNavigate(NavigationDestination.Product, item.productID.toString())
                }
            }
        }
    }
    HandelResonances()

}


@Composable
fun ProductItem(productItem: Product, onClick: () -> Unit) {
    Column(
        Modifier
            .width(176.dp)
            .height(198.dp)
            .clickable {
                onClick()
            }
            .clip(RoundedCornerShape(3.dp))
            .border(BorderStroke(1.dp, Color.Gray)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(TapsColor)
        ) {
            SubcomposeAsyncImage(
                model = productItem.images?.get(0).toString(),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize(),
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
        }
        Text(
            text = productItem.name.toString(),
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text =" ${productItem.price.toString()} ${ Constants.CURRENCY}",
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Start
        )
    }
}


@Composable
fun HandelResonances(viewModel: ProductsViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateProducts.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateProducts.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    ProgressBar(
        isShow = viewModel.stateProducts.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}
