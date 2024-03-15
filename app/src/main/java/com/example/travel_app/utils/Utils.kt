package com.example.travel_app.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.travel_app.model.CashCollection
import com.example.travel_app.model.TransactionsData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

object Utils {

    lateinit var database: FirebaseDatabase

    fun initializeFirebase() {
        database = FirebaseDatabase.getInstance()
    }


        fun updateLocations(context: Context, transactionId: String, fromLocation: String, toLocation: String, successCallback: (Boolean) -> Unit) {
        // Get a reference to the specific transaction in the database
        val reference = database.getReference("transactions").child(transactionId)

        // Create a map of the fields to update
        val updates = hashMapOf<String, Any>(
            "fromLocation" to fromLocation,
            "toLocation" to toLocation
        )

        // Update the transaction fields in the database
        reference.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Locations updated successfully!")
                successCallback(true)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Failed to update locations.", exception)
                successCallback(true)
            }
    }
    fun saveTransaction(context: Context, transaction: CashCollection, successCallback: (Boolean) -> Unit) {
        val reference = database.getReference("transactions")  // Reference to "transactions" node
        val key = reference.push().key ?: return // Generate a unique key or handle missing key

        // Add the key to the transaction object
        transaction.transactionKey = key

        reference.child(key).setValue(transaction)
            .addOnSuccessListener {
                Log.d(TAG, "Message written successfully!")
                successCallback(true) // Call the callback with success flag
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Failed to write message.", exception)
                successCallback(false) // Call the callback with failure flag
            }// Set the value at the generated key

    }


//    fun testDatabaseWrite() {
//        val myRef = database.getReference("message")
//        myRef.setValue("Hello, World!")
//            .addOnSuccessListener {
//                Log.d(TAG, "Message written successfully!")
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Failed to write message.", exception)
//            }
//    }

//    fun testDatabaseRead() {
//        val myRef = database.getReference("message")
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue<String>()
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//
//        })
//
//
//    }

}