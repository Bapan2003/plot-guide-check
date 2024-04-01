package com.example.plotguide.fragment

import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.health.connect.datatypes.ExerciseRoute.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.plotguide.R
import com.example.plotguide.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsFragment : Fragment() {
    private var binding:FragmentMapsBinding?=null
    private  var myMap:GoogleMap?=null
    private val FINE_PERMISSION_CODE=1
    private lateinit var loacation:Location
    private  lateinit var fusedLoacationProvider:FusedLocationProviderClient
    private  lateinit var currentLocation:LatLng
    private  var scaleFactor:Float= 14f
    private lateinit var permissionRequest : ActivityResultLauncher<Array<String>>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater,container,false)
        return binding!!.root
    }


    private fun getLastLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
          ActivityCompat.requestPermissions(requireActivity(),
              arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),FINE_PERMISSION_CODE
          )

            return
        }
        val task: Task<android.location.Location> = fusedLoacationProvider.lastLocation
        task.addOnSuccessListener {
            currentLocation= LatLng(it.latitude, it.longitude)
            val callback = OnMapReadyCallback { googleMap ->
                myMap=googleMap
                googleMap.addMarker(MarkerOptions().position(currentLocation).title("Marker in Kolkata"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
            }
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLoacationProvider=LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
        binding!!.myLocation.setOnClickListener {
            if(scaleFactor==14f){
                scaleFactor=8f
                myMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
            }else{
                scaleFactor=14f
                myMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
            }

        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==FINE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults.get(0) == PERMISSION_GRANTED){
                Toast.makeText(context,"last",Toast.LENGTH_SHORT).show()
                getLastLocation()
            }else{
                Toast.makeText(context,"Permission denied",Toast.LENGTH_SHORT).show()
            }
        }
    }
}