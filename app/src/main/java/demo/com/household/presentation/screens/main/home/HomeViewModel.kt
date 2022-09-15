package demo.com.household.presentation.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl) :
    ViewModel() {

        fun logout(){
           viewModelScope.launch {
               generalGeneralPrefsStoreImpl.clearData()
           }
        }
}