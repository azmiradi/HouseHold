package demo.com.household.presentation.screens.main.admin.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Cart
import demo.com.household.data.RegistrarRequest
import demo.com.household.data.RequestStatus
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null

    private val _stateCart = mutableStateOf(DataState<List<Cart>>())
    val stateCart: State<DataState<List<Cart>>> = _stateCart

    private val _stateUpdate = mutableStateOf(DataState<String>())
    val stateUpdate: State<DataState<String>> = _stateUpdate

    init {
        getCarts()
    }

    fun getCarts() {
        _stateCart.value = DataState(isLoading = true)
        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartList: MutableList<Cart> = ArrayList()
                    for (data: DataSnapshot in snapshot.children) {
                        val user = data.getValue(RegistrarRequest::class.java)
                        if (data.hasChild("Cart")) {
                            for (cartsData: DataSnapshot in data.child("Cart").children) {
                                val cart = cartsData.getValue(Cart::class.java)
                                cart?.id = cartsData.key
                                cart?.userName = user?.username
                                cart?.userID = user?.id
                                if (cart?.status() != null &&
                                    cart.status() != RequestStatus.Cart
                                ) {
                                    cartList.add(cart)
                                }
                            }
                        }
                    }
                    _stateCart.value = DataState(data = cartList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateCart.value = DataState(error = error.message)
                }

            })
    }

    fun updateCart(cartID: String, userID: String, status: Int) {
        _stateUpdate.value = DataState(isLoading = true)
        databaseReference.child(userID)
            .child("Cart")
            .child(cartID)
            .child("_status")
            .setValue(status)
            .addOnSuccessListener {
                _stateUpdate.value = DataState(data = "Request Updated")
            }
            .addOnFailureListener {
                _stateUpdate.value = DataState(error = it.message.toString())
            }
    }

    fun resetState() {
        _stateCart.value = DataState()
        job?.cancel()
    }


}