package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.mendelu.pef.xchomo.filmbuddy.architecture.BaseViewModel
import cz.mendelu.pef.xchomo.filmbuddy.firebase.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel(private val authRepository: AuthRepository) : BaseViewModel() {
    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

    fun login(email: String, password: String) {
        _loginStatus.postValue(LoginStatus.Loading)
        launch {
            try {
                authRepository.login(email, password).await()
                _loginStatus.postValue(LoginStatus.Success)
            } catch (e: Exception) {
                _loginStatus.postValue(LoginStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }
}

