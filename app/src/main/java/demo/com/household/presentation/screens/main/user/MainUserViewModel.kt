package demo.com.household.presentation.screens.main.user

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.Resource
import demo.com.household.data.*
import demo.com.household.data.Constants.CategoriesChild
import demo.com.household.data.Constants.ProductsChild
import demo.com.household.data.Constants.SubCategoriesChild
import demo.com.household.domain.use_cases.UploadFirebaseImageUseCase
import demo.com.household.presentation.DataState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainUserViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("data")

    private var job: Job? = null

    private val _stateCategories = mutableStateOf(DataState<List<Category>>())
    val stateCategories: State<DataState<List<Category>>> = _stateCategories

    private val _stateSubCategories = mutableStateOf(DataState<List<SubCategory>>())
    val stateSubCategories: State<DataState<List<SubCategory>>> = _stateSubCategories

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

    fun resetState() {
        _stateSubCategories.value = DataState()
        _stateCategories.value = DataState()
        job?.cancel()
    }


}