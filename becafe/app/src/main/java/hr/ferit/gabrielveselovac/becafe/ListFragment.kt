package hr.ferit.gabrielveselovac.becafe

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse


class ListFragment : Fragment() {

    private var placesId: List<String> = listOf()
    var places: List<PlaceDetails> = listOf()

    private val apiService = ServiceBuilder.buildService(PlacesEndPoint::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.backButtonList)

        placesId = (activity as MainActivity?)!!.getPlacesIDData()
        places = (activity as MainActivity?)!!.getPlacesDetailsData()

        if(placesId.indices.count() != places.indices.count())
        {
            getPlacesDetails()
        }
        else {
            view?.findViewById<RecyclerView>(R.id.placesList)?.apply {
                layoutManager =
                    LinearLayoutManager(requireActivity())
                adapter =
                    PlacesRecyclerAdapter(places)
                rootView.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }
        }

        backButton.setOnClickListener {

            backButton.setBackgroundColor(resources.getColor(R.color.gray))
            val handler = Handler()
            handler.postDelayed(Runnable {
                backButton.setBackgroundColor(resources.getColor(R.color.transparent))
            }, 50)

            val mapFragment = MapFragment()

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right)
            fragmentTransaction?.replace(R.id.mainFragment, mapFragment)
            fragmentTransaction?.commit()

        }

        return view
    }

    private fun inflateRecycler() {
        view?.findViewById<RecyclerView>(R.id.placesList)?.apply {
            layoutManager =
                LinearLayoutManager(requireActivity())
            adapter =
                PlacesRecyclerAdapter(places)
            rootView.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        }
    }

    private fun getPlacesDetails() {

        places = listOf()

        // see type
        /*
        var call = async {
            apiService.getDetails(
            "500",
            getString(R.string.google_maps_key)
            )}
        call.await().awaitResponse()
        */

        var callsDeferred: List<Deferred<Call<PlaceDetailsData>>> = listOf()

        CoroutineScope(Dispatchers.IO).launch {
            for (id: String in placesId) {
                callsDeferred += async {
                    apiService.getDetails(
                        id,
                        getString(R.string.google_maps_key)
                    )
                }
            }

            val calls = callsDeferred.awaitAll()

            var responses: List<Response<PlaceDetailsData>> = listOf()

            for(i: Int in calls.indices)
            {
                responses = responses + calls[i].awaitResponse()
            }

            for(i: Int in responses.indices)
            {
                if(responses[i].isSuccessful) {
                    places = places + responses[i].body()!!.result
                }
            }

            (activity as MainActivity?)!!.savePlacesDetailsData(places)

            // inflate after all requests are handled
            requireActivity().runOnUiThread(java.lang.Runnable {
                inflateRecycler()
            })

            }
        }
    }

