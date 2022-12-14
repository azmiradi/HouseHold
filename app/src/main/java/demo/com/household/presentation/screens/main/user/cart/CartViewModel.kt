package demo.com.household.presentation.screens.main.user.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Cart
import demo.com.household.data.Product
import demo.com.household.data.RequestStatus
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CartViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null

    private val _stateCart = mutableStateOf(DataState<Pair<List<Product>,String>>())
    val stateCart: State<DataState<Pair<List<Product>,String>>> = _stateCart

    private val _stateDeleteCart = mutableStateOf(DataState<String>())
    val stateDeleteCart: State<DataState<String>> = _stateDeleteCart

    private val _stateRequestData = mutableStateOf(DataState<String>())
    val stateRequestData: State<DataState<String>> = _stateRequestData

    fun getCarts() {
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateCart.value = DataState(isLoading = true)
            databaseReference.child(it)
                .child("Cart")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val cartList: MutableList<Product> = ArrayList()

                        for (data: DataSnapshot in snapshot.children) {
                            val cart = data.getValue(Cart::class.java)
                            if (cart?.status() == RequestStatus.Cart) {
                                cart.products?.let { it1 -> cartList.addAll(it1) }
                                _stateCart.value = DataState(data = Pair(cartList,data.key.toString()))
                                return
                            }
                        }
                        _stateCart.value = DataState(data = Pair(cartList,""))

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

    fun deleteCart(productID: String?,cartID:String) {
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateDeleteCart.value = DataState(isLoading = true)
            databaseReference.child(it)
                .child("Cart")
                .child(cartID)
                .child("products")
                .child(productID.toString())
                .removeValue()
                .addOnSuccessListener {
                    _stateDeleteCart.value = DataState(data = "deleted Success")

                }.addOnFailureListener {
                    _stateDeleteCart.value = DataState(error = it.message.toString())

                }
        }.launchIn(viewModelScope)
    }

    fun setRequest() {

    }


    fun updateCart() {
        generalGeneralPrefsStoreImpl.getID().onEach {
             databaseReference.child(it)
                .child("Cart")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data: DataSnapshot in snapshot.children) {
                            val cart = data.getValue(Cart::class.java)
                            if (cart?.status() == RequestStatus.Cart) {
                                 cart.isDeliver=true
                                 databaseReference.child(it)
                                    .child("Cart")
                                    .child(data.key.toString())
                                    .setValue(cart)
                                    .addOnSuccessListener {

                                    }.addOnFailureListener {
                                     }
                                return
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }.launchIn(viewModelScope)

    }
}