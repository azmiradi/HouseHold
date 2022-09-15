package demo.com.household.presentation.screens.main.user.category

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.data.SubCategory
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.share_componennt.ProgressBar
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.CharlestonGreen
import demo.com.household.ui.theme.ChineseSliver2

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    categoryID: String,
    onBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.getSubCategories(categoryID)
        viewModel.getCategory(categoryID)
    }
    val subCategories = remember {
        mutableStateOf(listOf<SubCategory>())
    }

    viewModel.stateSubCategories.value.data?.let {
        LaunchedEffect(Unit) {
            subCategories.value = it
        }
    }

    BackHandler {
        viewModel.resetState()
        onBack()
    }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        TopBarWithBack(title = viewModel.stateCategory.value.data ?: "", onDeleteIcon = {
            viewModel.deleteCategory(categoryID)
        }, onBack = onBack)

        Spacer(modifier = Modifier.height(15.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(subCategories.value) { item ->
                ItemCategory(categoryName = item.name.toString()) {
                    viewModel.deleteSubCategory(categoryID, item.id.toString())
                }
            }
        }
    }
    HandelResonances()
    val context = LocalContext.current

    viewModel.stateDeleteCategory.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateDeleteCategory.value.data, Toast.LENGTH_SHORT)
                .show()
            onBack()
        }
    }

    viewModel.stateDeleteSubCategory.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateDeleteSubCategory.value.data, Toast.LENGTH_SHORT)
                .show()
            viewModel.getSubCategories(categoryID = categoryID)
        }
    }
}


@Composable
fun ItemCategory(categoryName: String, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(ChineseSliver2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = categoryName,
            fontSize = 18.sp,
            color = CharlestonGreen,
            modifier = Modifier.padding(10.dp)
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete, contentDescription = "",
                tint = Color.Red,
            )
        }
    }
}


@Composable
fun HandelResonances(viewModel: CategoriesViewModel = hiltViewModel()) {
    val context = LocalContext.current

    if (viewModel.stateDeleteCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateDeleteCategory.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateDeleteSubCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(
                context,
                viewModel.stateDeleteSubCategory.value.error,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
    if (viewModel.stateSubCategories.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateSubCategories.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    ProgressBar(
        isShow = viewModel.stateSubCategories.value.isLoading ||
                viewModel.stateDeleteSubCategory.value.isLoading ||
                viewModel.stateDeleteCategory.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}
