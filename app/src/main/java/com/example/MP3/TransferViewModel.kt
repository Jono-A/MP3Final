package com.example.MP3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class TransferViewModel  : ViewModel() {

    private val auth = Firebase.auth
    private val ref = Firebase.database.reference
    private var states = MutableLiveData<TransferState>()

    fun getStates(): LiveData<TransferState> = states


    fun getRecipientInfo(barcodeResult : String?) {
       val objectListener = object : ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
              val users = snapshot.getValue<UsersModel>()
               states.value = TransferState.ScanSuccess(users)
               states.value = TransferState.IsBalanceEnough(users)
           }

           override fun onCancelled(error: DatabaseError) {

           }

       }
        ref.child("MP3/users/$barcodeResult").addListenerForSingleValueEvent(objectListener)
    }

    fun transferFund(userid: String?,barcodeResult : String?, amount : Double) {
        if (userid != null) {
            val objectListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user = snapshot.getValue<UsersModel>()

                    val balance = user?.balance

                    if(balance != null && balance > amount){
                        if (barcodeResult != null) {
                            val objectListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val recipient = snapshot.getValue<UsersModel>()
                                    val uid = recipient?.uid
                                    val name = recipient?.name
                                    val email = recipient?.email
                                    val balance = recipient?.balance
                                    val usersNewBalance = balance?.plus(amount)

                                    val updatedUser = UsersModel(
                                        name,
                                        email,
                                        usersNewBalance,
                                        uid
                                    )

                                    ref.child("MP3/users/$barcodeResult").setValue(updatedUser)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) states.value =
                                                TransferState.TransactionSuccess
                                            else states.value = TransferState.Error
                                        }.addOnFailureListener {
                                            states.value = TransferState.Error
                                        }

                                }

                                override fun onCancelled(error: DatabaseError) {}
                            }

                            ref.child("MP3/users/$barcodeResult").addListenerForSingleValueEvent(objectListener)
                        } else {
                            //
                        }


                    }else
                        states.value = TransferState.insuficientAmountError

                }

                override fun onCancelled(error: DatabaseError) {}
            }
            ref.child("MP3/users/$userid").addListenerForSingleValueEvent(objectListener)
        }

    }

    fun deductTransferedAmount(userid : String?, amount: Double){
        val objectListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<UsersModel>()

                    val uid = user?.uid
                    val name = user?.name
                    val email = user?.email
                    val usersCurrentBalance = user?.balance?.minus(amount)

                val updatedUser = UsersModel(
                    name,
                    email,
                    usersCurrentBalance,
                    uid
                )
                ref.child("MP3/users/$userid").setValue(updatedUser)
                    .addOnCompleteListener {
                        if(it.isSuccessful) states.value = TransferState.DeductionSuccess
                        else states.value = TransferState.Error
                    }.addOnFailureListener {
                        states.value = TransferState.Error
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        ref.child("MP3/users/$userid").addListenerForSingleValueEvent(objectListener)
    }
    fun addValue(userid: String?, amount : Double) {
        val objectListener = object : ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {
                val recipient = snapshot.getValue<UsersModel>()

                val uid = recipient?.uid
                val name = recipient?.name
                val email = recipient?.email
                val currentUserNewBalance = recipient?.balance?.plus(amount)

                val updatedUser = UsersModel(
                    name,
                    email,
                    currentUserNewBalance,
                    uid
                )

                ref.child("MP3/users/$userid").setValue(updatedUser)
                    .addOnCompleteListener {
                        if(it.isSuccessful) states.value = TransferState.ValueAdded
                        else states.value = TransferState.Error
                    }.addOnFailureListener {
                        states.value = TransferState.Error
                    }

            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.child("MP3/users/$userid").addListenerForSingleValueEvent(objectListener)
    }
}
