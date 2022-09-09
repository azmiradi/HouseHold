package demo.com.household.domain.use_cases

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import demo.com.household.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UploadFirebaseImageUseCase @Inject  constructor() {
    operator fun invoke(imageUri: Uri): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val downloadUrl = FirebaseStorage.getInstance().reference
                .child("images")
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            emit(Resource.Success<String>(downloadUrl.toString()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}