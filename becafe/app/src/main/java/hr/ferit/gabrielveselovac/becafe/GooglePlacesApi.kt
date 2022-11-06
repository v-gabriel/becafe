package hr.ferit.gabrielveselovac.becafe

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesEndPoint {

    @GET("api/place/nearbysearch/json?")
    fun getData(@Query("location") location: String, @Query("radius") radius: String, @Query("type") type: String,@Query("key") key: String): Call<PlacesList>

    @GET("api/place/details/json?")
    fun getDetails(@Query("place_id") place_id: String,@Query("key") key: String): Call<PlaceDetailsData>

}