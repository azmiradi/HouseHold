package demo.com.household.presentation.screens.main.admin.add_product

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import demo.com.household.R
import demo.com.household.data.Category
import demo.com.household.data.SubCategory
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.TopBarWithBack
import demo.com.household.presentation.screens.main.admin.main.MainAdminViewModel
import demo.com.household.presentation.share_componennt.*
import demo.com.household.ui.theme.BrinkPink

@Composable
fun AddProduct(
    viewModel: AddProductViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
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


    val productName = rememberSaveable() {
        mutableStateOf("")
    }
    val productDescription = rememberSaveable() {
        mutableStateOf("")
    }

    val price = rememberSaveable() {
        mutableStateOf("")
    }
    val subCategory = rememberSaveable() {
        mutableStateOf("")
    }
    val categoryID = rememberSaveable() {
        mutableStateOf("")
    }
    val imageUri = "android.resource://demo.com.household/drawable/add_image"
    val selectedImages =
        remember {
            mutableStateListOf(imageUri.toUri())
        }


    var selectImagePos by
    remember {
        mutableStateOf(-1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, start = 16.dp)
    ) {
        TopBarWithBack(title = stringResource(id = R.string.add_product), onBack = onBack)

        Spacer(modifier = Modifier.height(24.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.product_name),
            mutableState = productName, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.productName.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.dicription),
            mutableState = productDescription,
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp),
            isError = viewModel.productDesc.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.price),
            mutableState = price, modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Number,
            isError = viewModel.productPrice.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        SampleSpinner(hint = stringResource(id = R.string.select_category),
            list = categories.value.map {
                Pair(it.name.toString(), it.id.toString())
            }, onSelectionChanged = {
                categoryID.value = it
                viewModel.getSubCategories(it)
            })

        Spacer(modifier = Modifier.height(15.dp))
        SampleSpinner(hint = stringResource(id = R.string.select_sub_category),
            list = subCategories.value.map {
                Pair(it.name.toString(), it.id.toString())
            }, onSelectionChanged = {
                subCategory.value = it
            })
        Spacer(modifier = Modifier.height(15.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            content = {
                itemsIndexed(selectedImages) { index, image ->
                    ImageItems(image) {
                        selectImagePos = index
                    }
                }
            })
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                viewModel.addProduct(
                    productName = productName.value,
                    productDescription = productDescription.value,
                    categoryID = categoryID.value,
                    subCategoryID = subCategory.value,
                    images = selectedImages,
                    productPrice = price.value,
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

    var pickImage by
    remember {
        mutableStateOf(false)
    }

    if (selectImagePos > -1) {
        UIRequirePermissions(permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            onPermissionGranted = {
                pickImage = true
            })
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (selectImagePos == 0) {
                it?.let { it1 -> selectedImages.add(it1) }
            } else {
                if (it != null) {
                    selectedImages[selectImagePos] = it
                }
            }
            selectImagePos = -1
            pickImage = false
        }
    if (pickImage) {
        galleryLauncher.launch("image/*")
    }
    HandelResonances()
    val context = LocalContext.current

    viewModel.stateAddProduct.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Product Added", Toast.LENGTH_SHORT)
                .show()
            viewModel.resetState()
            onBack()
        }
    }
}


@Composable
fun HandelResonances(viewModel: AddProductViewModel = hiltViewModel()) {
    val context = LocalContext.current
    if (viewModel.stateCategories.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateCategories.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateAddProduct.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddProduct.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    if (viewModel.stateSubCategories.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateSubCategories.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (viewModel.subCategoryID.value) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Select  Category and Sub Category First ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    ProgressBar(
        isShow = viewModel.stateCategories.value.isLoading
                || viewModel.stateSubCategories.value.isLoading
                || viewModel.stateAddProduct.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = BrinkPink,
    )
}



