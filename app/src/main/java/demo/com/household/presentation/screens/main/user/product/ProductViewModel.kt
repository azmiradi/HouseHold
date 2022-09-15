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
import demo.com.household.data.RequestStatus
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
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

    fun addProductToCart(product: Product, count: String) {
        product.count = count
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateAddCart.value = DataState(isLoading = true)
            databaseReference =
                FirebaseDatabase.getInstance().getReference("users")
                    .child(it).child("Cart")
            getCartID { cartID, products ->

                val mutableList: MutableList<Product> = ArrayList()
                mutableList.addAll(products)
                mutableList.add(product)
                databaseReference.child(cartID)
                    .child("_status")
                    .setValue(RequestStatus.Cart.status)
                    .addOnSuccessListener {
                        databaseReference
                            .child(cartID)
                            .child("products")
                            .setValue(mutableList)
                            .addOnSuccessListener {
                                _stateAddCart.value = DataState(data = "Product Added")
                            }
                            .addOnFailureListener {
                                _stateAddCart.value = DataState(error = it.message.toString())
                            }
                    }.addOnFailureListener {
                        _stateAddCart.value = DataState(error = it.message.toString())
                    }

            }


        }.launchIn(viewModelScope)
    }

    private fun getCartID(onGetID: (String, List<Product>) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        val cart = data.getValue(Cart::class.java)
                        if (cart?.status() == RequestStatus.Cart) {
                            onGetID(data.key.toString(), cart.products ?: ArrayList())
                            return
                        }
                    }
                    onGetID(databaseReference.push().key.toString(), ArrayList())
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    fun resetState() {
        _stateProducts.value = DataState()
        job?.cancel()
    }

}