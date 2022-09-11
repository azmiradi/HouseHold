package demo.com.household.presentation.screens.main.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import demo.com.household.R
import demo.com.household.data.Category
import demo.com.household.data.SubCategory
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.main.admin.main.MainAdminViewModel
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.TapsColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainUserScreen(
    viewModel: MainUserViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination, subCategory: String) -> Unit,
    onBack: () -> Unit,
) {
    val categories = remember {
        mutableStateOf(listOf<Category>())
    }

    val subCategories = remember {
        mutableStateOf(listOf<SubCategory>())
    }

    viewModel.stateCategories.value.data?.let {
        LaunchedEffect(Unit) {
            categories.value = it
        }
    }

    viewModel.stateSubCategories.value.data?.let {
        LaunchedEffect(Unit) {
            subCategories.value = it
        }
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    Column(Modifier.fillMaxSize()) {
        if (categories.value.isNotEmpty()) {
            var tabIndex by remember { mutableStateOf(0) }


            LaunchedEffect(tabIndex) {
                viewModel.getSubCategories(categories.value[tabIndex].id.toString())

            }

            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                backgroundColor = TapsColor,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = BrinkPink
                    )

                }) {
                categories.value.forEachIndexed { index, tab ->
                    Tab(
                        text = {
                            Text(
                                tab.name.toString(),
                                color = if (tabIndex == index) BrinkPink else Color.White
                            )
                        },
                        selected = tabIndex == index,
                        onClick = {
                            scope.launch {
                                tabIndex = index
                            }
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        if (subCategories.value.isNotEmpty()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(subCategories.value) { item ->
                    SubCategoryItem(subCategory = item) {
                        item.image = null
                        onNavigate(NavigationDestination.Products, Gson().toJson(item))
                    }
                }
            }
        }

    }
    ProgressBar(
        isShow = viewModel.stateCategories.value.isLoading
                || viewModel.stateSubCategories.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )

}

@Composable
fun SubCategoryItem(subCategory: SubCategory, onClick: () -> Unit) {
    Column(
        Modifier
            .size(120.dp)
            .clickable {
                onClick()
            }
            .clip(RoundedCornerShape(3.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(TapsColor)
        ) {
            SubcomposeAsyncImage(
                model = subCategory.image,
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
            text = subCategory.name.toString(),
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Center
        )

    }
}

