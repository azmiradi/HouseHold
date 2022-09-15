package demo.com.household.presentation.screens.auth.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Constants
import demo.com.household.data.RegistrarRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {


    private var job: Job? = null
    private val _state = mutableStateOf(SplashChecker<String>())
    val state: State<SplashChecker<String>> = _state


    init {
        viewModelScope.launch {
            delay(3000)
            checkLogin()
        }

    }

    private fun checkLogin() {
        _state.value = SplashChecker()
        job?.cancel()
        job = generalGeneralPrefsStoreImpl.getID()
            .onEach {
                if (it.isNotEmpty()) {
                    getUserType(it)
                } else {
                    _state.value = SplashChecker(notLogin = true)
                }
            }.catch {
                _state.value = SplashChecker(notLogin = true)
            }.launchIn(viewModelScope)
    }


    fun resetState() {
        _state.value = SplashChecker()
        job?.cancel()
    }

    fun getUserType(userID: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(RegistrarRequest::class.java)
                    Constants.accountType = user?.accountType()
                    _state.value = SplashChecker(login = userID)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


}

data class SplashChecker<T>(
    val login: T? = null,
    val notLogin: Boolean = false,
)