package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

sealed class LoginStatus {
    object Loading : LoginStatus()
    object Success : LoginStatus()
    data class Error(val errorMessage: String) : LoginStatus()
}