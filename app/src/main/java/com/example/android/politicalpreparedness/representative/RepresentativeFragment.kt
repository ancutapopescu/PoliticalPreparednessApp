package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.DetailFragment.Companion.REQUEST_LOCATION_PERMISSION
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    //TODO: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        //TODO: Define and assign Representative adapter
        val representativeAdapter = RepresentativeListAdapter()
        binding.rvRepresentatives.adapter = representativeAdapter

        //TODO: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeAdapter.submitList(it)
            }
        })

        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.state.setNewValue(it.state)
            }
        })

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentatives(viewModel.address.value.toString())
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            getLocation()
        }

    return binding.root
}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf<String>(ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        if (checkLocationPermissions()) {
            LocationServices.getFusedLocationProviderClient(requireContext())
                    .lastLocation.addOnSuccessListener { location ->
                        viewModel.getAddressFromLocation(geoCodeLocation(location))
                        viewModel.getRepresentatives(viewModel.address.value.toString())
                    }
        }
        else {
            // Request location permission here instead of in checkLocationPermissions
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

private fun geoCodeLocation(location: Location): Address {
    val geocoder = Geocoder(context, Locale.getDefault())
    return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
}

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}