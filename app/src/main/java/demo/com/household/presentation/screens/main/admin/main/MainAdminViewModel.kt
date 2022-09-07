package demo.com.household.presentation.screens.main.admin.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import demo.com.household.presentation.DataState
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.*
import demo.com.household.data.Constants.CategoriesChild
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.data.Constants.accountType
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainAdminViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
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
            subCategory.categoryID.isNullOrEmpty()) {
            _subCategoryName.value = subCategory.name.isNullOrEmpty()
            _categorySelected.value =subCategory.categoryID.isNullOrEmpty()

            return
        }
        _stateAddSubCategory.value = DataState(isLoading = true)
        val subCategoryID = databaseReference.child(CategoriesChild).push().key
        subCategory.id = subCategoryID
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

    fun resetState() {
        _categoryName.value = false
        _subCategoryName.value = false
        _stateAddCategory.value = DataState()
        _stateAddSubCategory.value = DataState()
        _stateCategories.value = DataState()
        job?.cancel()
    }


}