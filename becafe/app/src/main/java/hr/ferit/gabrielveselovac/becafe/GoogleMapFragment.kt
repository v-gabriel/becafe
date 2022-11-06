package hr.ferit.gabrielveselovac.becafe

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GoogleMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    enum class PlaceRatingCategory {
        BELOW_AVERAGE, AVERAGE
    }

    private lateinit var googleMap : GoogleMap
    private lateinit var fusedLocationProviderService: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private var places: List<Place> = listOf()
    private var averageRating: Double = 0.0
    private var radius: String = ""

    private var placesId : List<String> = listOf()

    private var flag: Boolean = false
    private var done: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_google_map, container, false)

        places = (activity as MainActivity?)!!.getPlacesData()
        averageRating = (activity as MainActivity?)!!.getAvgRating()
        radius = (activity as MainActivity?)!!.getRadius()

        val radiusButton = requireActivity().findViewById<ImageButton>(R.id.mapRadiusMap)

        radiusButton.setOnClickListener{

            radiusButton.setBackgroundColor(resources.getColor(R.color.gray))
            val handler = Handler()
            handler.postDelayed(Runnable {
                radiusButton.setBackgroundColor(resources.getColor(R.color.transparent))
            }, 50)

            showPopup(radiusButton)
        }

        val mapFragment = childFragmentManager.findFragmentById((R.id.googleMapFragment)) as SupportMapFragment

        fusedLocationProviderService = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFragment.getMapAsync(this)

        return view;
    }


    // Request and handle API Places data
    private fun requestPlacesApi() {

        placesId = listOf()

        val request = ServiceBuilder.buildService(PlacesEndPoint::class.java)

        val location: String = lastLocation.latitude.toString() + "," + lastLocation.longitude.toString()

        val call = request.getData(location,radius,"cafe", getString(R.string.google_maps_key))

        call.enqueue(object : Callback<PlacesList> {
            override fun onResponse(
                call: Call<PlacesList>,
                response: Response<PlacesList>
            ) {
                if (response.isSuccessful) {

                    var latLng: LatLng
                    var desc: String

                    averageRating = getAverageRating(response.body()!!.results)

                    (activity as MainActivity?)!!.saveAverageRating(averageRating)

                    for(place: Place in response.body()!!.results){

                        latLng = LatLng(place.geometry.location.lat,place.geometry.location.lng)
                        desc = place.name + " | " + place.rating.toString()
                        placesId = placesId + place.place_id

                        if( place.rating >= averageRating ) {
                            placeMarkerOnMap(latLng, desc, PlaceRatingCategory.AVERAGE)
                        }
                        else{
                            placeMarkerOnMap(latLng, desc, PlaceRatingCategory.BELOW_AVERAGE)
                        }

                    }
                    (activity as MainActivity?)!!.savePlacesData(response.body()!!.results)
                    places = response.body()!!.results
                    (activity as MainActivity?)!!.savePlacesIDData(placesId)
                }
            }

            override fun onFailure(call: Call<PlacesList>, t: Throwable) {
                Log.d("FAIL", t.message.toString())
            }
        })
    }

    private fun getAverageRating(results: List<Place>): Double {
        var sum = 0.0
        for(place: Place in results)
        {
            sum += place.rating
        }
        return sum/results.size
    }

    // Popup for different radius sizes
    private fun showPopup(view: View) {

        val popup = PopupMenu(requireContext(), view)

        popup.inflate(R.menu.menu_radius)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.radiusOption1 -> {
                    if(this.radius != getString(R.string.radiusOption1))
                    {
                        flag = true
                    }
                    radius = getString(R.string.radiusOption1).trim('m')
                    refreshMap()
                }
                R.id.radiusOption2 -> {
                    if(this.radius != getString(R.string.radiusOption2))
                    {
                        flag = true
                    }
                    radius = getString(R.string.radiusOption2).trim('m')
                    refreshMap()
                }
                R.id.radiusOption3 -> {
                    if(this.radius != getString(R.string.radiusOption3))
                    {
                        flag = true
                    }
                    radius = getString(R.string.radiusOption3).trim('m')
                    refreshMap()
                }
                R.id.radiusOption4 -> {
                    if(this.radius != getString(R.string.radiusOption4))
                    {
                        flag = true
                    }
                    radius = getString(R.string.radiusOption4).trim('m')
                    refreshMap()
                }
            }
            true
        })

        popup.show()
    }

    private fun refreshMap() {
        (activity as MainActivity?)!!.saveRadius(radius)
        setUpMap()
    }

    // Google maps setup and logic
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.uiSettings.isZoomControlsEnabled = true
        this.googleMap.setOnMarkerClickListener(this)
        this.done = true

        setUpMap()
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    private fun setUpMap() {

        googleMap.clear()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        googleMap.isMyLocationEnabled = true

        fusedLocationProviderService.lastLocation.addOnSuccessListener(requireActivity()) { location: Location? ->
            if(location == null)
            {
                setUpMap()
            }
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng (location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong, "My location")
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
                if(places.indices.count() == 0 || this.flag) {
                    this.flag = false
                    requestPlacesApi()
                }
                else {
                    fillKnownMarkers()
                }
            }
        }
    }

    private fun fillKnownMarkers() {
        var latLng: LatLng
        var desc = ""

        for(place: Place in places){

            latLng = LatLng(place.geometry.location.lat,place.geometry.location.lng)
            desc = place.name + " | " + place.rating.toString()
            placesId = placesId + place.place_id

            if( place.rating >= averageRating ) {
                placeMarkerOnMap(latLng, desc, PlaceRatingCategory.AVERAGE)
            }
            else{
                placeMarkerOnMap(latLng, desc, PlaceRatingCategory.BELOW_AVERAGE)
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng, name: String, type: PlaceRatingCategory? = null) {
        val marker = MarkerOptions().position(currentLatLong)
        if(type != null) {
            if (type.name == PlaceRatingCategory.AVERAGE.name) {
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
            if (type.name == PlaceRatingCategory.BELOW_AVERAGE.name) {
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            }
        }
        marker.title(name)
        googleMap.addMarker(marker)
    }
}