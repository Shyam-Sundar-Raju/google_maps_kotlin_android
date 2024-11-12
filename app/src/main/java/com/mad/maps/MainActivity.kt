package com.mad.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.app.AlertDialog
import com.google.android.gms.maps.model.Marker
import com.mad.maps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isCardVisible = false

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationPermission", "Requesting permissions")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("LocationPermission", "Permissions already granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val latitudeInput = findViewById<EditText>(R.id.latitude_input)
        val longitudeInput = findViewById<EditText>(R.id.longitude_input)
        val titleInput = findViewById<EditText>(R.id.title_input)
        val addMarkerButton = findViewById<Button>(R.id.add_marker_button)

        addMarkerButton.setOnClickListener {
            val latitude = latitudeInput.text.toString().toDoubleOrNull()
            val longitude = longitudeInput.text.toString().toDoubleOrNull()
            val title = titleInput.text.toString()

            if (latitude != null && longitude != null && title.isNotEmpty()) {
                val latLng = LatLng(latitude, longitude)
                addMarker(latLng, title)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            } else {
                Toast.makeText(this, "Please enter valid coordinates and title", Toast.LENGTH_SHORT).show()
            }
        }

        val fabAddMarker = findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.fab_add_marker)
        fabAddMarker.setOnClickListener {
            isCardVisible = !isCardVisible
            updateAddMarkerCardVisibility()
        }

    }

    private fun showDeleteConfirmationDialog(marker: Marker) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Marker")
        builder.setMessage("Do you want to delete this marker?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            marker.remove()
            Toast.makeText(this, "Marker deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Uncomment the following line to enable satellite view.
        // mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        // Adding a dummy marker in Sydney.
        mMap.setOnMarkerClickListener { marker ->
            showDeleteConfirmationDialog(marker)
            true // Return true to indicate we have handled the event
        }

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, update the map
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
                        }
                    }
                }
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String) {
        mMap.addMarker(MarkerOptions().position(latLng).title(title))
    }

    private fun updateAddMarkerCardVisibility() {
        if (isCardVisible) {
            binding.fabAddMarker.text = getString(R.string.close)
            binding.latitudeInput.text.clear()
            binding.longitudeInput.text.clear()
            binding.titleInput.text.clear()
            binding.cardAddMarker.visibility = View.VISIBLE
        } else {
            binding.fabAddMarker.text = getString(R.string.add_marker_button_text)
            binding.latitudeInput.text.clear()
            binding.longitudeInput.text.clear()
            binding.titleInput.text.clear()
            binding.cardAddMarker.visibility = View.GONE
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
