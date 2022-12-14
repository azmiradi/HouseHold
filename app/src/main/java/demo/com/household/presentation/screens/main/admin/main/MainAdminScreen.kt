package demo.com.household.presentation.screens.main.admin.main

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import demo.com.household.R
import demo.com.household.data.Category
import demo.com.household.data.SubCategory
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.share_componennt.*
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.CharlestonGreen
import demo.com.household.ui.theme.ChineseSliver
import demo.com.household.ui.theme.ChineseSliver2

@Composable
fun MainAdminScreen(
    onNavigate: (NavigationDestination, String) -> Unit,
    viewModel: MainAdminViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val imageUri = "android.resource://demo.com.household/drawable/add_image"

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }
    val selectedImage =
        remember {
            mutableStateOf(imageUri.toUri())
        }


    var pickImage by
    remember {
        mutableStateOf(false)
    }

    val categories = remember {
        mutableStateOf(listOf<Category>())
    }
    val categoryName = remember {
        mutableStateOf("")
    }
    val selectedCategory = remember {
        mutableStateOf("")
    }
    val subCategoryName = remember {
        mutableStateOf("")
    }
    viewModel.stateCategories.value.data?.let {
        LaunchedEffect(Unit) {
            categories.value = it
        }
    }

    BackHandler {
        viewModel.resetState()
        onBack()
    }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, start = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(categories.value) { item ->
                ItemCategory(categoryName = item.name.toString()) {
                    viewModel.resetState()
                    onNavigate(NavigationDestination.SubCategory, item.id.toString())
                }
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = ChineseSliver, thickness = 3.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.add_new_category),
                color = CharlestonGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextInput(
                hint = stringResource(id = R.string.category_name),
                mutableState = categoryName, modifier = Modifier
                    .fillMaxWidth(),
                isError = viewModel.categoryName.value
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.addCategory(
                        Category(
                            name = categoryName.value
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(BrinkPink),
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = ChineseSliver, thickness = 3.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.add_sub_category),
                color = CharlestonGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner(
                hint = stringResource(id = R.string.select_category),
                list = categories.value.map {
                    Pair(it.name.toString(), it.id.toString())
                }, onSelectionChanged = {
                    selectedCategory.value = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            CustomTextInput(
                hint = stringResource(id = R.string.add_sub_category),
                mutableState = subCategoryName, modifier = Modifier
                    .fillMaxWidth(),
                isError = viewModel.subCategoryName.value
            )
            Spacer(modifier = Modifier.height(10.dp))

            ImageItems(selectedImage.value) {
                pickImage = true
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.addSubCategory(
                        SubCategory(
                            name = subCategoryName.value,
                            categoryID = selectedCategory.value,
                            image = selectedImage.value.toString()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(BrinkPink),
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

    }
    val context = LocalContext.current

    viewModel.stateAddCategory.value.data?.let {
        LaunchedEffect(Unit) {
            categoryName.value = ""
            Toast.makeText(context, "Category Added", Toast.LENGTH_SHORT).show()
        }
    }

    viewModel.stateAddSubCategory.value.data?.let {
        LaunchedEffect(Unit) {
            subCategoryName.value = ""
            selectedImage.value= imageUri.toUri()
            Toast.makeText(context, "Sub Category Added", Toast.LENGTH_SHORT).show()
        }
    }

    //Handel Resonances
    HandelResonances()


    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                selectedImage.value = it
            }
            pickImage = false
        }

    if (pickImage) {
        UIRequirePermissions(permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            onPermissionGranted = {
                galleryLauncher.launch("image/*")
            })
    }


}

@Composable
fun ItemCategory(categoryName: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(ChineseSliver2)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = categoryName,
            fontSize = 18.sp,
            color = CharlestonGreen,
            modifier = Modifier.padding(10.dp)
        )

        Icon(
            imageVector = Icons.Default.ArrowRight, contentDescription = "",
            tint = BrinkPink,
            modifier = Modifier.padding(10.dp)
        )
    }
}


@Composable
fun HandelResonances(viewModel: MainAdminViewModel = hiltViewModel()) {
    val context = LocalContext.current
    if (viewModel.stateCategories.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateCategories.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateAddCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddCategory.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateAddSubCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddSubCategory.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (viewModel.categorySelected.value) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Select Category First ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    ProgressBar(
        isShow = viewModel.stateCategories.value.isLoading
                || viewModel.stateAddCategory.value.isLoading
                || viewModel.stateAddSubCategory.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}

