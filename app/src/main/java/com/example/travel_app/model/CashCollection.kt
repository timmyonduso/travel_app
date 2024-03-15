package com.example.travel_app.model

import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

data class CashCollection(
    val userId: String? = FirebaseAuth.getInstance().currentUser?.displayName, // Use a nullable type
    val fromLocation: String,
    val toLocation: String,
    val passengerCount: Int,
    val hasLuggage: Boolean,
    val fare: Double,
    val timestamp: Long,
    var transactionKey: String? = null
) : Serializable {
    // Add a no-argument constructor
    constructor() : this(null, "", "", 0, false, 0.0, 0L) {
        // Optional initialization code if needed
    }
}
