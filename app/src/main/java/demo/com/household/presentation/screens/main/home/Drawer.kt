package demo.com.household.presentation.screens.main.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Outbox
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
import demo.com.household.data.Constants.accountType
import demo.com.household.presentation.NavigationDestination
import demo.com.household.ui.theme.BrinkPink
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    scaffoldState: ScaffoldState,
    onNavigate: (NavigationDestination,data:String) -> Unit,
    onBack: () -> Unit, ) {
    val scope = rememberCoroutineScope()

    BackHandler {
       if (scaffoldState.drawerState.isOpen)
           scope.launch {
               scaffoldState.drawerState.close()
           }
        else
           onBack()
    }
    Column {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(BrinkPink)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_white),
                contentDescription = R.drawable.logo_white.toString(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(200.dp)
                    .width(160.dp)
                    .padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                 },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Outbox,
                contentDescription = "",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(
                    id = if (accountType?.equals(
                            AccountType
                                .User
                        ) == true
                    ) R.string.my_orders
                    else R.string.orders
                ),
                fontSize = 20.sp,
                color = Color.White
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.Gray)
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.logout),
                fontSize = 20.sp,
                color = Color.White
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color.Gray)
        Spacer(modifier = Modifier.height(5.dp))

    }
}
