package demo.com.household.presentation.share_componennt

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ImageItems(image: Uri, onClick: () -> Unit) {
    IconButton(
        onClick = onClick, modifier = Modifier
            .size(84.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = image), contentDescription = "",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    } 
}