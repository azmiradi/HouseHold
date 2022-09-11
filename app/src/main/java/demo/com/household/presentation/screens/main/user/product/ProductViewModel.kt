package demo.com.household.presentation.screens.main.user.product

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Cart
import demo.com.household.data.Constants.ProductsChild
import demo.com.household.data.Product
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null

    private val _stateProducts = mutableStateOf(DataState<Product>())
    val stateProducts: State<DataState<Product>> = _stateProducts

    private val _stateAddCart = mutableStateOf(DataState<String>())
    val stateAddCart: State<DataState<String>> = _stateAddCart

    fun getProducts(productID: String) {
        _stateProducts.value = DataState(isLoading = true)
        databaseReference.child(ProductsChild)
            .child(productID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val product = snapshot.getValue(Product::class.java)
                    _stateProducts.value = DataState(data = product)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateProducts.value = DataState(error = error.message)
                }

            })
    }

    fun addProductToCart(product: Product?, count: String) {
        val cart =Cart(product, count)
         generalGeneralPrefsStoreImpl.getID().onEach {
            _stateAddCart.value = DataState(isLoading = true)
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users")
            databaseReference.child(it)
                .child("Cart")
                .child(product?.productID.toString())
                .setValue(cart)
                .addOnSuccessListener {
                    _stateAddCart.value = DataState(data = "Product Added")
                }
                .addOnFailureListener {
                    _stateAddCart.value = DataState(error = it.message.toString())
                }
        }.launchIn(viewModelScope)
    }

    fun resetState() {
        _stateProducts.value = DataState()
        job?.cancel()
    }

}