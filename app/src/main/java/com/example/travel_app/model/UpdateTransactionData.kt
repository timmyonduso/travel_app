package com.example.travel_app.model

import android.os.Parcel
import android.os.Parcelable

data class UpdateTransactionData(
    val transactionId: String,
    val fromLocation: String,
    val toLocation: String,
    val hasLuggage: Boolean
) : Parcelable {
    constructor(transaction: CashCollection) : this(
        transaction.transactionKey ?: "",
        transaction.fromLocation,
        transaction.toLocation,
        transaction.hasLuggage
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(fromLocation)
        parcel.writeString(toLocation)
        parcel.writeByte(if (hasLuggage) 1 else 0) // Convert boolean to byte
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateTransactionData> {
        override fun createFromParcel(parcel: Parcel): UpdateTransactionData {
            val transactionId = parcel.readString() ?: ""
            val fromLocation = parcel.readString() ?: ""
            val toLocation = parcel.readString() ?: ""
            val hasLuggage = parcel.readByte() != 0.toByte()
            return UpdateTransactionData(transactionId, fromLocation, toLocation, hasLuggage)
        }

        override fun newArray(size: Int): Array<UpdateTransactionData?> {
            return arrayOfNulls(size)
        }
    }
}
