package demo.com.household.presentation.screens.main.user.payment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.com.household.data.Cart
import demo.com.household.data.RequestStatus
import demo.com.household.presentation.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    private var job: Job? = null

    private val _stateRequest = mutableStateOf(DataState<String>())
    val stateRequest: State<DataState<String>> = _stateRequest


    fun updateCart() {
        generalGeneralPrefsStoreImpl.getID().onEach {
            _stateRequest.value = DataState(isLoading = true)
            databaseReference.child(it)
                .child("Cart")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data: DataSnapshot in snapshot.children) {
                            val cart = data.getValue(Cart::class.java)

                            if (cart?.status() == RequestStatus.Cart) {
                                cart._status=RequestStatus.Ready.status
                                cart.time=Calendar.getInstance().timeInMillis.toString()
                                databaseReference.child(it)
                                    .child("Cart")
                                    .child(data.key.toString())
                                    .setValue(cart)
                                    .addOnSuccessListener {
                                        _stateRequest.value = DataState(data = "Order Conformed")

                                    }.addOnFailureListener {
                                        _stateRequest.value = DataState(error = it.message.toString())
                                    }
                                return
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }.launchIn(viewModelScope)

    }

    fun resetState() {
        _stateRequest.value = DataState()
        job?.cancel()
    }


    fun setRequest() {

    }
}