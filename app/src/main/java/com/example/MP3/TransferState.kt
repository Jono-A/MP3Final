package com.example.MP3

sealed class TransferState {

    data class ScanSuccess(val user : UsersModel?) : TransferState()
    data class IsBalanceEnough(val user : UsersModel?) : TransferState()
    data object TransactionSuccess : TransferState()
    data object DeductionSuccess : TransferState()
    data object ValueAdded: TransferState()
    data object Error : TransferState()
    data object  insuficientAmountError : TransferState()
}