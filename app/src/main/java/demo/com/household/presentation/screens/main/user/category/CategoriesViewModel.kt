package demo.com.household.presentation.screens.main.user.category

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.*
import demo.com.household.data.Constants.CategoriesChild
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.presentation.DataState
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null

    private val _stateDeleteCategory = mutableStateOf(DataState<String>())
    val stateDeleteCategory: State<DataState<String>> = _stateDeleteCategory

    private val _stateDeleteSubCategory = mutableStateOf(DataState<String>())
    val stateDeleteSubCategory: State<DataState<String>> = _stateDeleteSubCategory

    private val _stateSubCategories = mutableStateOf(DataState<List<SubCategory>>())
    val stateSubCategories: State<DataState<List<SubCategory>>> = _stateSubCategories

    private val _stateCategory = mutableStateOf(DataState<String>())
    val stateCategory: State<DataState<String>> = _stateCategory

    fun getCategory(categoryID: String) {
        _stateSubCategories.value = DataState(isLoading = true)
        databaseReference.child(CategoriesChild)
            .child(categoryID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val category = snapshot.getValue(Category::class.java)
                    _stateCategory.value= DataState(data = category?.name.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateCategory.value = DataState(error = error.message)
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

    fun deleteCategory(categoryID: String) {
        _stateDeleteCategory.value = DataState(isLoading = true)
        databaseReference.child(CategoriesChild)
            .child(categoryID)
            .removeValue().addOnSuccessListener {
                _stateDeleteCategory.value = DataState(data = "Category Deleted")

            }.addOnFailureListener {
                _stateDeleteCategory.value = DataState(error = it.message.toString())
            }
    }

    fun deleteSubCategory(categoryID: String, subCategoryID: String) {
        _stateDeleteSubCategory.value = DataState(isLoading = true)
        databaseReference.child(CategoriesChild)
            .child(categoryID)
            .child(SubCategoriesChild)
            .child(subCategoryID)
            .removeValue().addOnSuccessListener {
                _stateDeleteSubCategory.value = DataState(data = "Category Deleted")

            }.addOnFailureListener {
                _stateDeleteSubCategory.value = DataState(error = it.message.toString())
            }
    }

    fun resetState() {
        _stateSubCategories.value = DataState()
        _stateDeleteCategory.value = DataState()
        _stateDeleteSubCategory.value = DataState()
        job?.cancel()
    }


}