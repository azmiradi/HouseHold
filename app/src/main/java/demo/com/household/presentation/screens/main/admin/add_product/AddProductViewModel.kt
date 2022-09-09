package demo.com.household.presentation.screens.main.admin.add_product

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import demo.com.household.presentation.DataState
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.Resource
import demo.com.household.data.*
import demo.com.household.data.Constants.CategoriesChild
import demo.com.household.data.Constants.ProductsChild
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.domain.use_cases.UploadFirebaseImageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl,
    private val uploadFirebaseImageUseCase: UploadFirebaseImageUseCase
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null

    private val _stateCategories = mutableStateOf(DataState<List<Category>>())
    val stateCategories: State<DataState<List<Category>>> = _stateCategories

    private val _stateSubCategories = mutableStateOf(DataState<List<SubCategory>>())
    val stateSubCategories: State<DataState<List<SubCategory>>> = _stateSubCategories

    private val _stateAddProduct = mutableStateOf(DataState<Boolean>())
    val stateAddProduct: State<DataState<Boolean>> = _stateAddProduct


    private val _subCategoryID = mutableStateOf(false)
    val subCategoryID: State<Boolean> = _subCategoryID

    private val _images = mutableStateOf(false)
    val images: State<Boolean> = _images

    private val _productName = mutableStateOf(false)
    val productName: State<Boolean> = _productName

    private val _productPrice = mutableStateOf(false)
    val productPrice: State<Boolean> = _productPrice

    private val _productDesc = mutableStateOf(false)
    val productDesc: State<Boolean> = _productDesc

    init {
        getCategories()
    }

    private fun getCategories() {
        _stateCategories.value = DataState(isLoading = true)
        databaseReference.child(CategoriesChild)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories: MutableList<Category> = ArrayList()
                    for (data: DataSnapshot in snapshot.children) {
                        val category = data.getValue(Category::class.java)
                        category?.let { categories.add(it) }
                    }
                    _stateCategories.value = DataState(data = categories)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateCategories.value = DataState(error = error.message)
                }

            })
    }


    fun getSubCategories(categoryID: String) {
        _stateSubCategories.value = DataState(isLoading = true)
        databaseReference.child(CategoriesChild)
            .child(categoryID)
            .child(SubCategoriesChild)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories: MutableList<SubCategory> = ArrayList()
                    for (data: DataSnapshot in snapshot.children) {
                        val category = data.getValue(SubCategory::class.java)
                        category?.let { categories.add(it) }
                    }
                    _stateSubCategories.value = DataState(data = categories)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateSubCategories.value = DataState(error = error.message)
                }

            })
    }

    fun addProduct(
        productName: String,
        productDescription: String,
        productPrice: String,
        categoryID: String,
        subCategoryID: String,
        images: List<Uri>
    ) {
        validationInputs(
            productName,
            productDescription,
            productPrice,
            categoryID,
            images,
            subCategoryID
        )?.let {
            _stateAddProduct.value = DataState(isLoading = true)
            val productID = databaseReference.child(CategoriesChild).push().key
            val product = it
            product.productID = productID
            val imagesMutable: MutableList<Uri> = ArrayList()

            images.forEachIndexed { index, image ->
                uploadImage(image) {
                    imagesMutable.add(it.toUri())
                    if (index == images.size - 1) {
                        addProductData(product,imagesMutable)
                    }
                }
            }

        }
    }

    private fun addProductData(product: Product, imagesMutable: MutableList<Uri>,) {
        product.images = imagesMutable.map { it.toString() }

        databaseReference.child(ProductsChild).child(product.productID.toString())
            .setValue(product)
            .addOnSuccessListener {
                getCategories()
                _stateAddProduct.value = DataState(data = true)
            }
            .addOnFailureListener {
                _stateAddProduct.value = DataState(error = it.message.toString())
            }
    }

    private fun validationInputs(
        productName: String,
        productDescription: String,
        productPrice: String,
        categoryID: String,
        images: List<Uri>,
        subCategoryID: String
    ): Product? {
        if (productName.isEmpty() || productDescription.isEmpty() ||
            productPrice.isEmpty() || categoryID.isEmpty() ||
            images.isEmpty() || subCategoryID.isEmpty()
        ) {
            _productName.value = productName.isEmpty()
            _productDesc.value = productDescription.isEmpty()
            _productPrice.value = productPrice.isEmpty()
            _subCategoryID.value = categoryID.isEmpty()
            _images.value = images.isEmpty()
            _subCategoryID.value = subCategoryID.isEmpty()
            return null
        }
        return Product(
            name = productName, description = productDescription,
            price = productPrice.toInt(), images = null,
            subCategoryID = subCategoryID, categoryID = categoryID
        )
    }


    private var jobUpload: Job? = null

    fun resetState() {
        _productDesc.value = false
        _productPrice.value = false
        _subCategoryID.value = false
        _productName.value = false

        _stateAddProduct.value = DataState()
        _stateCategories.value = DataState()
        job?.cancel()
        jobUpload?.cancel()
    }


    private fun uploadImage(
        image: Uri,
        imageUploaded: (String) -> Unit
    ) {
        jobUpload?.cancel()
        jobUpload = uploadFirebaseImageUseCase(image).onEach {
            when (it) {
                is Resource.Success -> {
                    imageUploaded(it.data.toString())
                }
                is Resource.Error -> {
                    _stateAddProduct.value = DataState(error = it.message.toString())
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

}