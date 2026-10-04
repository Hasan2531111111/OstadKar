package com.ostadkar.app.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ostadkar.app.domain.repository.UserProfile
import com.ostadkar.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class ProfileFormState(
    val displayName: String = "",
    val specialty: String = "",
    val phone: String = "",
    val city: String = "",
    val photoPath: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val profile: StateFlow<UserProfile> = repository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    private val _form = MutableStateFlow(ProfileFormState())
    val form: StateFlow<ProfileFormState> = _form.asStateFlow()

    init {
        viewModelScope.launch {
            val p = repository.get()
            _form.value = ProfileFormState(
                displayName = p.displayName,
                specialty = p.specialty,
                phone = p.phone,
                city = p.city,
                photoPath = p.photoPath
            )
        }
    }

    fun updateName(v: String) { _form.value = _form.value.copy(displayName = v, saved = false, error = null) }
    fun updateSpecialty(v: String) { _form.value = _form.value.copy(specialty = v, saved = false) }
    fun updatePhone(v: String) { _form.value = _form.value.copy(phone = v.filter { it.isDigit() || it == '+' || it == ' ' }, saved = false) }
    fun updateCity(v: String) { _form.value = _form.value.copy(city = v, saved = false) }

    fun setPhotoFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val dir = File(context.filesDir, "profile").apply { mkdirs() }
                val outFile = File(dir, "avatar.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outFile).use { output -> input.copyTo(output) }
                }
                _form.value = _form.value.copy(photoPath = outFile.absolutePath, saved = false)
            } catch (e: Exception) {
                _form.value = _form.value.copy(error = "خطا در ذخیره عکس")
            }
        }
    }

    fun clearPhoto() {
        val path = _form.value.photoPath
        if (path.isNotBlank()) {
            runCatching { File(path).delete() }
        }
        _form.value = _form.value.copy(photoPath = "", saved = false)
    }

    fun save() {
        val f = _form.value
        if (f.displayName.isBlank()) {
            _form.value = f.copy(error = "نام را وارد کنید")
            return
        }
        viewModelScope.launch {
            _form.value = f.copy(isSaving = true, error = null)
            repository.save(
                UserProfile(
                    displayName = f.displayName,
                    specialty = f.specialty,
                    phone = f.phone,
                    city = f.city,
                    photoPath = f.photoPath
                )
            )
            _form.value = _form.value.copy(isSaving = false, saved = true)
        }
    }
}
