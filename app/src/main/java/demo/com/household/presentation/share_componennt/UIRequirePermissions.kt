package demo.com.household.presentation.share_componennt

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun UIRequirePermissions(
    permissions: Array<String>,
    onPermissionGranted:  () -> Unit
) {
    val context = LocalContext.current

    var grantState by remember {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    if (grantState)
    {
        LaunchedEffect(key1 = Unit){
            onPermissionGranted()
        }
    }
    else {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                 grantState = !it.containsValue(false)
            }
        )
        SideEffect {
            launcher.launch(permissions)
        }
    }
}