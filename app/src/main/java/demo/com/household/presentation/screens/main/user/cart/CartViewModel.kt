package demo.com.household.presentation.screens.main.user.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Cart
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null

    private val _stateCart = mutableStateOf(DataState<List<Cart>>())
    val stateCart: State<DataState<List<Cart>>> = _stateCart

    private val _stateDeleteCart = mutableStateOf(DataState<String>())
    val stateDeleteCart: State<DataState<String>> = _stateDeleteCart

    fun getCarts() {
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateCart.value = DataState(isLoading = true)
            databaseReference.child(it)
                .child("Cart")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val cartList: MutableList<Cart> = ArrayList()
                        for (data: DataSnapshot in snapshot.children) {
                            val cart = data.getValue(Cart::class.java)
                            if (cart != null) {
                                cartList.add(cart)
                            }
                        }
                        _stateCart.value = DataState(data = cartList)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        _stateCart.value = DataState(error = error.message)
                    }

                })
        }.launchIn(viewModelScope)

    }

    fun resetState() {
        _stateCart.value = DataState()
        job?.cancel()
    }

    fun deleteCart(productID: String?) {
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateDeleteCart.value = DataState(isLoading = true)
            databaseReference.child(it)
                .child("Cart")
                .child(productID.toString())
                .removeValue()
                .addOnSuccessListener {
                    _stateDeleteCart.value = DataState(data = "deleted Success")

                }.addOnFailureListener {
                    _stateDeleteCart.value = DataState(error = it.message.toString())

                }
        }.launchIn(viewModelScope)
    }
}