package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

sealed class RegistrationStatus {
    object Loading : RegistrationStatus()
    object Success : RegistrationStatus()
    data class Error(val message: String) : RegistrationStatus()
    object EmailAlreadyExists : RegistrationStatus()
}