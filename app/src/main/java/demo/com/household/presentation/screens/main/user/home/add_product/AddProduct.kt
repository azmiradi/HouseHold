package demo.com.household.presentation.screens.main.user.home.add_product

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.himanshoe.pluck.PluckConfiguration
import com.himanshoe.pluck.ui.Pluck
import com.himanshoe.pluck.ui.permission.Permission
import demo.com.household.R
import demo.com.household.data.SubCategory
import demo.com.household.presentation.CustomTextInput
import demo.com.household.presentation.SampleSpinner
import demo.com.household.presentation.screens.TopBarWithBack

@Composable
fun AddProduct() {
    val categories = remember() {
        mutableStateListOf<SubCategory>()
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, start = 16.dp)
    ) {
        TopBarWithBack(title = stringResource(id = R.string.add_product)) {

        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.product_name),
            mutableState = productName, modifier = Modifier.fillMaxWidth(),
            //    isError = viewModel.errorUsername.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.dicription),
            mutableState = productDescription,
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp),
            // isError = viewModel.errorUsername.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        CustomTextInput(
            hint = stringResource(id = R.string.price),
            mutableState = price, modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Number,
            //isError = viewModel.errorUsername.value
        )
        Spacer(modifier = Modifier.height(15.dp))

        SampleSpinner(hint = stringResource(id = R.string.select_category),
            list = categories.map {
                Pair(it.name.toString(), it.id.toString())
            }, onSelectionChanged = {
                subCategory.value = it
            })
        Spacer(modifier = Modifier.height(15.dp))

        SelectedImage()

    }
}

@Composable
fun SelectedImage() {
    val imageUri = "drawable://" + R.drawable.add_image
    val selectedImages =
        remember {
            mutableStateListOf(imageUri.toUri())
        }

    val selectImagePos =
        remember {
            mutableStateOf(-1)
        }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        content = {
            itemsIndexed(selectedImages) { index, image ->
                ImageItems(image) {
                    selectImagePos.value = index
                }
            }
        })

    if (selectImagePos.value > -1) {
        Permission(
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            goToAppSettings = {

            }
        ) {
            Pluck(
                pluckConfiguration = PluckConfiguration(multipleImagesAllowed = false),
                onPhotoSelected = { images ->
                    if (selectImagePos.value == 0) {
                        selectedImages.add(images[0].uri)
                    } else {
                        selectedImages[selectImagePos.value] = images[0].uri
                    }
                    selectImagePos.value = -1
                }
            )
        }
    }
}

@Composable
fun ImageItems(image: Uri, onClick: () -> Unit) {
    Image(
        painter = rememberImagePainter(data = image), contentDescription = "",
        modifier = Modifier
            .size(84.dp)
            .clickable {
                onClick()
            },
        contentScale = ContentScale.FillBounds
    )

}
