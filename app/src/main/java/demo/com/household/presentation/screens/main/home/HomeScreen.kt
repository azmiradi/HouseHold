package demo.com.household.presentation.screens.main.home

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
import demo.com.household.data.AccountType
import demo.com.household.data.Constants
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.main.admin.main.MainAdminScreen
import demo.com.household.presentation.screens.main.admin.add_product.AddProduct
import demo.com.household.presentation.screens.main.user.MainUserScreen
import demo.com.household.ui.theme.BrinkPink
import demo.com.household.ui.theme.CharlestonGreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigate: (NavigationDestination, data: String) -> Unit,
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
            if (Constants.accountType == AccountType.Admin) {
                //MainAdminScreen(onNavigate = onNavigate, onBack = onBack)
                MainUserScreen(onBack = onBack, onNavigate =onNavigate)
            }

        }
    }
}

@Composable
fun TopBar(scaffoldState: ScaffoldState, onNavigate: (NavigationDestination,String) -> Unit) {
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
            if (Constants.accountType == AccountType.Admin)
                onNavigate(NavigationDestination.AddProduct,"")
        }) {
            Icon(
                painter = if (Constants.accountType == AccountType.Admin)
                    painterResource(id = R.drawable.add)
                else
                    painterResource(id = R.drawable.cart), "",
                tint = Color.White
            )
        }

    }
}