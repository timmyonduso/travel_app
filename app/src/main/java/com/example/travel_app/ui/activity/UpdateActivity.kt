package com.example.travel_app.ui.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.travel_app.BuildConfig
import com.example.travel_app.R
import com.example.travel_app.databinding.ActivityUpdateBinding
import com.example.travel_app.model.AutocompleteEditText
import com.example.travel_app.model.CashCollection
import com.example.travel_app.model.TransactionData
import com.example.travel_app.model.UpdateTransactionData
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
import kotlin.properties.Delegates

class UpdateActivity : AppCompatActivity() {

    private lateinit var transactionId: String
    private lateinit var transactionName: String
    private lateinit var transactionRoute: String
    private var transactionFare by Delegates.notNull<Double>()
    private lateinit var binding: ActivityUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = intent
        if (intent.hasExtra("transactionId") && intent.hasExtra("transactionFare")) {
            transactionId = intent.getStringExtra("transactionId").toString()
            transactionFare = intent.getDoubleExtra("transactionFare", 0.0)
            transactionName = intent.getStringExtra("transactionName").toString()
            transactionRoute = intent.getStringExtra("transactionRoute").toString()

            // Now you can access transactionId and transactionFare
            binding.transactionNo.text = transactionId
            binding.fare.text = transactionFare.toString()
            binding.passengerName.text = transactionName
            binding.route.text = transactionRoute


        } else {
            // Handle the case where data is missing (optional)
            Log.e("UpdateActivity", "Missing transaction data in the Intent")
        }

        binding.connect.setOnClickListener {
            Utils.initializeFirebase()

            Utils.updateLocations(
                this,
                transactionId,
                binding.locationFrom.text.toString(),
                binding.locationTo.text.toString()
            ) { success ->
                if (success) {
                    // Start the main activity on success
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle failure scenario (e.g., show a toast)
                    Toast.makeText(this, "Failed to save transaction!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Attach an Autocomplete intent to the Address 1 EditText field
        binding.locationFrom.setOnClickListener(startAutocompleteIntentListener)
        binding.locationTo.setOnClickListener(startAutocompleteIntentListener)

        preventMultipleCheck()

        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.PLACES_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")

        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(this, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

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
                    Log.i(ContentValues.TAG, prediction.placeId)
                    Log.i(ContentValues.TAG, prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e(ContentValues.TAG, "Place not found: ${exception.statusCode}")
                }
            }

    }


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
                    Log.d(ContentValues.TAG, "Place: " + place.name)
                }
            } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(ContentValues.TAG, "User canceled autocomplete")
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
                    Log.d(ContentValues.TAG, "Place: " + place.name)
                }
            } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(ContentValues.TAG, "User canceled autocomplete")
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
            .build(this)
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
            .build(this)
        startAutocompleteTo.launch(intent)
    }

    private fun preventMultipleCheck() {
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            // One of the RadioButtons is already checked, clear it
            binding.radioGroup.clearCheck()
        }
    }
}