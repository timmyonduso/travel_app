package com.example.travel_app.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.travel_app.BuildConfig
import com.example.travel_app.databinding.FragmentCashBinding
import com.example.travel_app.model.CashCollection
import com.example.travel_app.ui.activity.MainActivity
import com.example.travel_app.utils.Utils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CashFragment : Fragment() {


    private lateinit var binding: FragmentCashBinding
    private val LUGGAGE_FARE = 100

    private val startAutocompleteIntentListener = View.OnClickListener { view ->

        val editText = view as EditText  // Cast the clicked view to EditText

        // Clear any existing click listeners
        view.setOnClickListener(null)

        // Trigger autocomplete intent based on clicked EditText
        if (editText == binding.locationFrom) {
            startAutocompleteForLocationFrom()
        } else if (editText == binding.locationTo) {
            startAutocompleteForLocationTo()
        }
    }


    private val startAutocompleteFrom = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
            binding.locationFrom.setOnClickListener(startAutocompleteIntentListener)
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val locationName = place?.name ?: ""  // Handle null place object

                    binding.locationFrom.setText(locationName)  // Assuming locationFrom is selected

                    // Write a method to read the address components from the Place
                    // and populate the form with the address components
                    Log.d(TAG, "Place: " + place.name)
                }
            } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
            }
        } as ActivityResultCallback<ActivityResult>)

    private val startAutocompleteTo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult ->
            binding.locationTo.setOnClickListener(startAutocompleteIntentListener)
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val locationName = place?.name ?: ""  // Handle null place object

                    binding.locationTo.setText(locationName)  // Assuming locationFrom is selected

                    // Write a method to read the address components from the Place
                    // and populate the form with the address components
                    Log.d(TAG, "Place: " + place.name)
                }
            } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
            }
        } as ActivityResultCallback<ActivityResult>)
    // [END maps_solutions_android_autocomplete_define]

    // [START maps_solutions_android_autocomplete_intent]

    private fun startAutocompleteForLocationFrom() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(
            Place.Field.NAME
        )

        // Build the autocomplete intent with field, country, and type filters applied
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(listOf("KE"))
            .setTypesFilter(listOf(PlaceTypes.LOCALITY))
            .build(requireContext())
        startAutocompleteFrom.launch(intent)
    }

    private fun startAutocompleteForLocationTo() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(
            Place.Field.NAME
        )

        // Build the autocomplete intent with field, country, and type filters applied
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(listOf("KE"))
            .setTypesFilter(listOf(PlaceTypes.LOCALITY))
            .build(requireContext())
        startAutocompleteTo.launch(intent)
    }

    private fun updateTotalFare() {
        val fare = binding.fareAmount.text.toString().toIntOrNull() ?: 0
        var totalFare: Int
        val numPassengers = binding.passNo.text.toString().toIntOrNull() ?: 1  // Default to 1 passenger

        if (binding.radioButtonLuggage.isChecked) {
            totalFare = (fare + LUGGAGE_FARE) * numPassengers
        }
        else if (binding.radioButtonLuggageOnly.isChecked) {
            totalFare = LUGGAGE_FARE * numPassengers
        }
        else{
            totalFare = fare * numPassengers
        }

        binding.amountTendered.text = totalFare.toString()
    }

    private fun preventMultipleCheck() {
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            // One of the RadioButtons is already checked, clear it
            binding.radioGroup.clearCheck()
        }
    }

    private fun getFormattedTimestamp(): Long {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())  // Adjust format as needed
        sdf.format(Date(timestamp))
        return timestamp  // Return the original timestamp for storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCashBinding.inflate(inflater, container, false)

        binding.connect.setOnClickListener {
            Utils.initializeFirebase()

            // Extract data from UI elements
            val fromLocation = binding.locationFrom.text.toString().trim()
            val toLocation = binding.locationTo.text.toString().trim()
            val fareAmount = binding.fareAmount.text.toString().toDoubleOrNull() ?: 0.0
            val hasLuggage = binding.radioButtonLuggage.isChecked || binding.radioButtonLuggageOnly.isChecked
            val numPassengers = binding.passNo.text.toString().toIntOrNull() ?: 1
            val userId = FirebaseAuth.getInstance().currentUser?.displayName
            // Create TransactionsData object
            val transactionData = CashCollection(userId, fromLocation, toLocation, numPassengers, hasLuggage, fareAmount, getFormattedTimestamp())

            // Call Utils.saveTransaction to save data to Firebase
            Utils.saveTransaction(requireContext(), transactionData) { success ->
                if (success) {
                    // Start the main activity on success
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle failure scenario (e.g., show a toast)
                    Toast.makeText(requireContext(), "Failed to save transaction!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Attach an Autocomplete intent to the Address 1 EditText field
        binding.locationFrom.setOnClickListener(startAutocompleteIntentListener)
        binding.locationTo.setOnClickListener(startAutocompleteIntentListener)

        binding.fareAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalFare()
            }
        })
        binding.passNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalFare()
            }
        })

        binding.radioButtonLuggage.setOnCheckedChangeListener { _, isChecked ->
            updateTotalFare()
        }

        binding.radioButtonLuggageOnly.setOnCheckedChangeListener { _, isChecked ->
            updateTotalFare()
        }

        preventMultipleCheck()

        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.PLACES_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")

        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(requireContext(), apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(requireContext())

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(-4.6762, 33.9664), // Southwestern corner of Kenya
            LatLng(4.6225, 41.9062)   // Northeastern corner of Kenya
        )
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(LatLng(-4.6762, 33.9664))
                .setCountries("KE")
                .setTypesFilter(listOf(PlaceTypes.ADDRESS))
                .setSessionToken(token)
                .setQuery(startAutocompleteFrom.toString())
                .setQuery(startAutocompleteTo.toString())
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    Log.i(TAG, prediction.placeId)
                    Log.i(TAG, prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.statusCode}")
                }
            }


        val view = binding.root
        return view
    }

}