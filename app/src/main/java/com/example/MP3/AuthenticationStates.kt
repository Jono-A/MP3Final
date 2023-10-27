package com.example.MP3

sealed class AuthenticationStates {
    data class Default(val user: UsersModel?) : AuthenticationStates()
    data class IsLoggedIn(val isSignedIn : Boolean) : AuthenticationStates()
    data class QrGenerated(val user : UsersModel?) : AuthenticationStates()
    data class CurrentBalance(val user : UsersModel?) : AuthenticationStates()
    data object SignedUp : AuthenticationStates()
    data object LoggedIn : AuthenticationStates()
    data object ProfileUpdated : AuthenticationStates()
    data object EmailUpdated : AuthenticationStates()
    data object PasswordUpdated : AuthenticationStates()
    data object VerificationEmailSent : AuthenticationStates()
    data object PasswordResetEmailSent : AuthenticationStates()
    data object LogOut : AuthenticationStates()
    data object UserDeleted : AuthenticationStates()
    data object Error : AuthenticationStates()

}