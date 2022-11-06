package hr.ferit.gabrielveselovac.becafe

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() /*AppCompatActivity()*/ {

     private var placesId: List<String> = listOf()
     private var places: List<Place> = listOf()
     private var placesDetails: List<PlaceDetails> = listOf()

     var averageRating: Double = 0.0
     var searchRadius: String = "250"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    // Fragment <-> Activity
    fun savePlacesIDData(placesId: List<String>)
    {
        this.placesId = placesId
    }

    fun getPlacesIDData(): List<String>
    {
        return placesId
    }

    fun savePlacesData(places: List<Place>)
    {
        this.places = places
    }

    fun getPlacesData(): List<Place>
    {
        return this.places
    }

    fun savePlacesDetailsData(places: List<PlaceDetails>)
    {
        this.placesDetails = places
    }

    fun getPlacesDetailsData(): List<PlaceDetails>
    {
        return this.placesDetails
    }

    fun saveAverageRating(averageRating: Double){
        this.averageRating = averageRating
    }

    fun getAvgRating(): Double{
        return this.averageRating
    }

    fun saveRadius(radius: String){
        this.searchRadius = radius
    }

    fun getRadius(): String{
        return this.searchRadius
    }

}