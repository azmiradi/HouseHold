package demo.com.household.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBarWithBack(title: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                "", tint = Color.Black,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 5.dp, bottom = 5.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


@Composable
fun TopBarWithBack(
    title: String, onBack: () -> Unit,
    onDeleteIcon: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                "", tint = Color.Black,
            )
        }

        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 5.dp, bottom = 5.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        IconButton(
            onClick = onDeleteIcon,
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                "", tint = Color.Red,
            )
        }
    }
}