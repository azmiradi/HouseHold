package demo.com.household.presentation.screens.auth.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.AccountType
import demo.com.household.data.Constants
import demo.com.household.data.RegistrarRequest
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl) :
    ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference

    private var job: Job? = null
    private val _state = mutableStateOf(DataState<Boolean>())
    val state: State<DataState<Boolean>> = _state

    private val _errorTermsConditions = mutableStateOf(false)
    val errorTermsConditions: State<Boolean> = _errorTermsConditions


    private val _errorEmail = mutableStateOf(false)
    val errorEmail: State<Boolean> = _errorEmail

    private val _errorPassword = mutableStateOf(false)
    val errorPassword: State<Boolean> = _errorPassword

    private val _errorConfirmPassword = mutableStateOf(false)
    val errorConfirmPassword: State<Boolean> = _errorConfirmPassword

    private val _usernameInput = mutableStateOf(false)
    val usernameInput: State<Boolean> = _usernameInput

    private val _mobileInput = mutableStateOf(false)
    val mobileInput: State<Boolean> = _mobileInput

    fun signup(
        email: String,
        password: String,
        confirmPassword: String,
        username: String,
        mobile: String,
    ) {
        _state.value = DataState()
        isValidRequest(email, password, confirmPassword, username, mobile)?.let {
            job?.cancel()
            _state.value = DataState(isLoading = true)
            val userID = databaseReference.child("users").push().key
            checkUserExist(username, email, userExit = {
                _state.value = DataState(error = "user Alard Register with username or email!")

            }, userNotExit = {
                databaseReference.child("users").child(userID.toString())
                    .setValue(it.copy(id = userID))
                    .addOnFailureListener {
                        _state.value = DataState(error = it.message ?: "Something went wrong!")
                    }.addOnSuccessListener {
                        Constants.accountType = AccountType.User
                        saveData(userID.toString())
                        _state.value = DataState(data = true)
                    }
            }) {
                _state.value = DataState(error = it)
            }

        }
    }

    private fun checkUserExist(
        username: String, email: String,
        userNotExit: () -> Unit,
        userExit: (String) -> Unit,
        error: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("users")) {
                    for (userSnap: DataSnapshot in snapshot.child("users").children) {
                        val user = userSnap.getValue(RegistrarRequest::class.java)
                        user?.let {
                            if (it.email == email || it.username == username) {
                                userExit(it.id.toString())
                                return
                            }
                        }
                    }
                    userNotExit()
                } else {
                    userNotExit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error(error.message)
            }

        })
    }

    private fun isValidRequest(
        email: String,
        password: String,
        confirmPassword: String,

        username: String,
        mobile: String
    ): RegistrarRequest? {
        return if (email.isEmpty() || password.isEmpty() ||
            username.isEmpty() || mobile.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            _errorEmail.value = email.isEmpty()
            _errorPassword.value = password.isEmpty()
            _usernameInput.value = username.isEmpty()
            _errorConfirmPassword.value = confirmPassword.isEmpty()
            _mobileInput.value = mobile.isEmpty()
            null
        } else if (confirmPassword != password) {
            _errorConfirmPassword.value = true
            _errorPassword.value = true

            null
        } else {
            _errorEmail.value = false
            _errorPassword.value = false
            _usernameInput.value = false
            _errorConfirmPassword.value = false
            _mobileInput.value = false
            RegistrarRequest(
                password = password,
                mobile = mobile,
                username = username,
                email = email,
                _accountType = 2,
            )
        }
    }

    private fun saveData(id: String) {
        Constants.UserID = id
        viewModelScope.launch {
            generalGeneralPrefsStoreImpl.saveID(id)
        }
    }

    fun resetState() {
        _state.value = DataState()
        _errorEmail.value = false
        _errorPassword.value = false
        _usernameInput.value = false
        _errorConfirmPassword.value = false
        _mobileInput.value = false

        job?.cancel()
    }


}