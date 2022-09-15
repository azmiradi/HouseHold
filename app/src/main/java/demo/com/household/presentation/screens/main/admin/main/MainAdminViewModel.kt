package demo.com.household.presentation.screens.main.admin.main

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
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.domain.use_cases.UploadFirebaseImageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainAdminViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl,
    private val uploadFirebaseImageUseCase: UploadFirebaseImageUseCase
) : ViewModel() {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null
    private val _stateCategories = mutableStateOf(DataState<List<Category>>())
    val stateCategories: State<DataState<List<Category>>> = _stateCategories

    private val _stateAddCategory = mutableStateOf(DataState<Boolean>())
    val stateAddCategory: State<DataState<Boolean>> = _stateAddCategory

    private val _stateAddSubCategory = mutableStateOf(DataState<Boolean>())
    val stateAddSubCategory: State<DataState<Boolean>> = _stateAddSubCategory

    private val _categoryName = mutableStateOf(false)
    val categoryName: State<Boolean> = _categoryName

    private val _subCategoryName = mutableStateOf(false)
    val subCategoryName: State<Boolean> = _subCategoryName

    private val _categorySelected = mutableStateOf(false)
    val categorySelected: State<Boolean> = _categorySelected

    private val _subCategoryImage = mutableStateOf(false)
    val subCategoryImage: State<Boolean> = _subCategoryImage


    fun getCategories() {
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

    fun addCategory(category: Category) {
        if (category.name.isNullOrEmpty()) {
            _categoryName.value = category.name.isNullOrEmpty()
            return
        }

        _stateAddCategory.value = DataState(isLoading = true)
        val categoryID = databaseReference.child(CategoriesChild).push().key
        category.id = categoryID
        databaseReference.child(CategoriesChild).child(categoryID.toString())
            .setValue(category)
            .addOnSuccessListener {
                getCategories()
                _stateAddCategory.value = DataState(data = true)
            }
            .addOnFailureListener {
                _stateAddCategory.value = DataState(error = it.message.toString())
            }
    }

    fun addSubCategory(subCategory: SubCategory) {
        if (subCategory.name.isNullOrEmpty() ||
            subCategory.categoryID.isNullOrEmpty() ||
            subCategory.image.isNullOrEmpty()
        ) {
            _subCategoryName.value = subCategory.name.isNullOrEmpty()
            _categorySelected.value = subCategory.categoryID.isNullOrEmpty()
            _subCategoryImage.value = subCategory.image.isNullOrEmpty()
            return
        }
        _stateAddSubCategory.value = DataState(isLoading = true)
        val subCategoryID = databaseReference.child(CategoriesChild).push().key
        subCategory.id = subCategoryID

        uploadImage(subCategory.image.toString().toUri()) {
            subCategory.image = it
            databaseReference.child(CategoriesChild)
                .child(subCategory.categoryID.toString())
                .child(SubCategoriesChild)
                .child(subCategoryID.toString())
                .setValue(subCategory)
                .addOnSuccessListener {
                    _stateAddSubCategory.value = DataState(data = true)
                }
                .addOnFailureListener {
                    _stateAddSubCategory.value = DataState(error = it.message.toString())
                }
        }

    }

    private var jobUpload: Job? = null

    fun resetState() {
        _categoryName.value = false
        _subCategoryName.value = false
        _stateAddCategory.value = DataState()
        _stateAddSubCategory.value = DataState()
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
                    _stateAddSubCategory.value = DataState(error = it.message.toString())
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

}