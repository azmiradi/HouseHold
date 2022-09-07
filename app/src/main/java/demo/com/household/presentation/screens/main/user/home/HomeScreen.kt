package demo.com.household.presentation.screens.main.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import demo.com.household.R
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.main.admin.main.MainAdminScreen
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
        topBar = { TopBar(scaffoldState = scaffoldState) },
        drawerBackgroundColor = CharlestonGreen,
        drawerContent = {
            Drawer(
                scaffoldState = scaffoldState, onBack = onBack,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            MainAdminScreen(onNavigate = {}, onBack = {})
        }
    }
}

@Composable
fun TopBar(scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.menu), "")
            }
        },
        actions = {
            Box(Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = ""
                )
            }
        },
        backgroundColor = BrinkPink,
        contentColor = Color.White,
        title = {}
    )
}