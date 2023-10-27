package com.example.MP3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val ref = Firebase.database.reference
    private var states = MutableLiveData<AuthenticationStates>()

    private lateinit var gso:GoogleSignInOptions

    fun getStates(): LiveData<AuthenticationStates> = states

    fun signUp(email : String, password : String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.SignedUp

            else states.value = AuthenticationStates.Error

        }.addOnSuccessListener {
            states.value = AuthenticationStates.Error
        }
    }


    fun getUserProfile() {
        val objectListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.getValue<UsersModel>()
                states.value = AuthenticationStates.Default(users)
            }

            override fun onCancelled(error: DatabaseError) {
                states.value = AuthenticationStates.Error
            }

        }
        ref.child("MP3/users/" + auth.currentUser?.uid).addValueEventListener(objectListener)
    }

        fun updateUserProfile(newName : String) {
        val userUpdates = userProfileChangeRequest {
            displayName = newName
        }

        auth.currentUser?.updateProfile(userUpdates)?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.ProfileUpdated
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun isLoggedIn(){
        states.value = AuthenticationStates.IsLoggedIn(auth.currentUser != null)
    }

    fun login(email : String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.LoggedIn
            else states.value = AuthenticationStates.Error
        }.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.VerificationEmailSent
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun sendPasswordResetEmail(email : String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.PasswordResetEmailSent
            else states.value = AuthenticationStates.Error

        }.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun logOut() {
        auth.signOut()
        states.value = AuthenticationStates.LogOut
    }
    fun deleteUser() {
        auth.currentUser?.delete()?.addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.UserDeleted
            else states.value = AuthenticationStates.Error

        }?.addOnFailureListener {
            states.value = AuthenticationStates.Error
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) states.value = AuthenticationStates.LoggedIn
            else states.value = AuthenticationStates.Error
        }.addOnFailureListener {

        }
    }
    fun createUserRecord(name : String, email : String, balance : Double) {

        val users = UsersModel(
            name,
            email,
            balance,
            auth.currentUser?.uid

        )

        ref.child("MP3/users/" + auth.currentUser?.uid).setValue(users).addOnCompleteListener {
            if(it.isSuccessful) states.value = AuthenticationStates.ProfileUpdated
            else states.value = AuthenticationStates.Error
        }
    }


}