package demo.com.household.presentation.screens.main.user.products

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Constants.ProductsChild
import demo.com.household.data.Product
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null

    private val _stateProducts = mutableStateOf(DataState<List<Product>>())
    val stateProducts: State<DataState<List<Product>>> = _stateProducts


    fun getProducts(subCategoryID: String) {
        _stateProducts.value = DataState(isLoading = true)
        databaseReference.child(ProductsChild)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products: MutableList<Product> = ArrayList()
                    for (data: DataSnapshot in snapshot.children) {
                        val product = data.getValue(Product::class.java)
                        if (product?.subCategoryID == subCategoryID)
                            products.add(product)
                    }
                    _stateProducts.value = DataState(data = products)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateProducts.value = DataState(error = error.message)
                }

            })
    }


    fun resetState() {
        _stateProducts.value = DataState()
        job?.cancel()
    }

}