package demo.com.household.presentation.screens.main.admin.add_product

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Category
import demo.com.household.data.Constants.CategoriesChild
import demo.com.household.data.Constants.ProductsChild
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.data.Product
import demo.com.household.data.SubCategory
import demo.com.household.domain.use_cases.UploadFirebaseImageUseCase
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl,
    private val uploadFirebaseImageUseCase: UploadFirebaseImageUseCase
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")
    private val imagesList: MutableList<String> = ArrayList()

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
        images: MutableList<Uri>
    ) {
        images.removeAt(0)
        validationInputs(
            productName,
            productDescription,
            productPrice,
            categoryID,
            images,
            subCategoryID
        )?.let { product ->
            val allImages: MutableList<String> = ArrayList()
            _stateAddProduct.value = DataState(isLoading = true)
            val productID = databaseReference.child(CategoriesChild).push().key

            product.productID = productID

            uploadImage(images) {
                allImages.addAll(imagesList)
                product.images = allImages
                addProductData(product)
                jobUpload?.cancel()
            }
        }
    }

    private fun addProductData(product: Product) {

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
    private fun uploadImage(
        imagesUris: List<Uri>,
        onComplete: () -> Unit
    ) {
        val images: MutableList<Uri> = ArrayList()
        images.addAll(imagesUris)
        images.removeAt(0)

        imagesList.clear()
        uploadImages(0, images) {
            onComplete()
        }
    }

    private fun uploadImages(
        index: Int,
        imagesUris: List<Uri>,
        onComplete: () -> Unit
    ) {
        val ref = FirebaseStorage.getInstance().reference
            .child(Calendar.getInstance().timeInMillis.toString())
        val uploadTask = ref.putFile(imagesUris[index])

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (index == (imagesUris.size - 1)) {
                    imagesList.add(task.result.toString())
                    onComplete()
                } else {
                    imagesList.add(task.result.toString())
                    uploadImages(index + 1, imagesUris, onComplete)
                }
            } else {
                _stateAddProduct.value = DataState(error = task.exception?.message.toString())
            }
        }.addOnFailureListener {
            _stateAddProduct.value = DataState(error = it.message.toString())
        }
    }

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
}