package demo.com.household.presentation.screens.main.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demo.com.household.R
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.main.admin.main.MainAdminScreen
import demo.com.household.presentation.screens.main.user.home.add_product.AddProduct
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.CharlestonGreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(scaffoldState = scaffoldState, onNavigate = onNavigate)
        },
        drawerBackgroundColor = CharlestonGreen,
        drawerContent = {
            Drawer(
                scaffoldState = scaffoldState, onBack = onBack,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            //MainAdminScreen(onNavigate = {}, onBack = {})
            AddProduct()
        }
    }
}

@Composable
fun TopBar(scaffoldState: ScaffoldState, onNavigate: (NavigationDestination) -> Unit) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(BrinkPink),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            scope.launch {
                scaffoldState.drawerState.open()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                "", tint = Color.White
            )
        }

        Image(
            painter = painterResource(id = R.drawable.logo_white),
            contentDescription = ""
        )

        IconButton(onClick = {
            //  onNavigate(N)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.add), "",
                tint = Color.White
            )
        }

    }
}