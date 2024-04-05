package com.example.plotguide.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.graphics.Color
import android.health.connect.datatypes.ExerciseRoute.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.graphics.alpha
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.plotguide.R
import com.example.plotguide.databinding.FragmentMapsBinding
import com.example.plotguide.model.LocationDetails
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog

class MapsFragment : Fragment(),OnMapReadyCallback{
    private var binding:FragmentMapsBinding?=null
    private  lateinit var myMap:GoogleMap
    private val FINE_PERMISSION_CODE=1
    private lateinit var loacation:Location
    private  lateinit var fusedLoacationProvider:FusedLocationProviderClient
    private  lateinit var currentLocation:LatLng
    private  var scaleFactor:Float= 14f
    private var mArrayList: ArrayList<LocationDetails> = ArrayList()
    private lateinit var permissionRequest : ActivityResultLauncher<Array<String>>
    private var isVisible=false
    private lateinit var lastLocation:String;
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

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mArrayList.add(LocationDetails(50.45, 30.2,"sydney"))
        mArrayList.add(LocationDetails(50.5, 30.0,"us"))
        mArrayList.add(LocationDetails(50.5555, 30.13,"london"))
        mArrayList.add(LocationDetails(50.56655, 30.1093,"london"))
        mArrayList.add(LocationDetails(50.557755, 30.1003,"london"))
        mArrayList.add(LocationDetails(50.555677595, 30.1783,"london"))
        fusedLoacationProvider=LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
        binding!!.myLocation.setOnClickListener {
//            if(scaleFactor==14f){
//                scaleFactor=8f
//                myMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
//            }else{
                scaleFactor=14f
                myMap.addMarker(MarkerOptions().position(currentLocation).title("kolkata"))
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
                myMap.addCircle(CircleOptions().center(currentLocation).radius(500.0).fillColor(R.color.cyan).strokeColor(Color.TRANSPARENT))
                binding!!.markerDetailsCardView.isVisible=false
//            }

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

    override fun onMapReady(googleMap: GoogleMap) {
        myMap=googleMap
//        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Marker in Kolkata"))
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,scaleFactor))
        val boundsBuilder=LatLngBounds.builder()
        for(i in 0..<mArrayList.size){
            val location=mArrayList[i]
            val lating=LatLng(location.latitude,location.longitude)
            boundsBuilder.include(lating)
            myMap.addMarker( MarkerOptions().position(lating).title(location.name))

//            googleMap.animateCamera(CameraUpdateFactory.newLatLng(lating))
        }

        myMap.setOnMarkerClickListener {
//            it.title?.let { it1 -> btnDialog(it1) };
//             Toast.makeText(requireContext(),it.position.latitude.toString(),Toast.LENGTH_SHORT).show()
            if(binding!!.markerDetailsCardView.visibility==View.VISIBLE && it.id == lastLocation){
                binding!!.markerDetailsCardView.visibility= View.GONE
                lastLocation=""
            }else{
                lastLocation=it.id
                binding!!.markerDetailsCardView.visibility= View.VISIBLE
            }

            true
        }

        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),350,350,0))


    }
   @SuppressLint("MissingInflatedId")
   fun btnDialog(text:String){
        val dialog=BottomSheetDialog(requireContext())
         val view =layoutInflater.inflate(R.layout.bottom_sheet_dialog,null)
         view.findViewById<TextView>(R.id.text1).text=text
          dialog.setContentView(view)
       dialog.show()
    }
}