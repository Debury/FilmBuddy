package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.mendelu.pef.xchomo.filmbuddy.architecture.BaseViewModel
import cz.mendelu.pef.xchomo.filmbuddy.firebase.AuthRepository

class RegisterViewModel(private val authRepository: AuthRepository) : BaseViewModel() {
    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    fun register(email: String, password: String, username: String){
        _registrationStatus.postValue(RegistrationStatus.Loading)

        authRepository.register(email, password, username)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registrationStatus.postValue(RegistrationStatus.Success)
                } else {
                    _registrationStatus.postValue(
                        RegistrationStatus.Error(task.exception?.message ?: "Unknown error")
                    )
                }
            }
    }
}