package hr.ferit.gabrielveselovac.becafe

import android.content.res.Resources
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PlacesRecyclerAdapter(private val data: List<PlaceDetails>):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaceViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.cafe_info, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is PlaceViewHolder -> {
                holder.bind(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size;
    }
}

class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val noDataFound = "[no data found]"
    private val cafeName: TextView = itemView.findViewById(R.id.cafeName)
    private val cafeAddress: TextView = itemView.findViewById(R.id.cafeAddress)
    private val cafeRating: TextView = itemView.findViewById(R.id.cafeRating)
    private val cafePhoneNumber: TextView = itemView.findViewById(R.id.cafePhoneNumber)
    private val cafeSchedule: TextView = itemView.findViewById(R.id.cafeSchedule)
    fun bind(details: PlaceDetails) {
        //Glide
            //.with(itemView.context)
            //.load(makeup.image)
            //.into(photoImage)
        val name: String = if(details.name == null) noDataFound else details.name
        val rating: String = "<b>" + "RATING: " + "</b>" + (if(details.rating == null) noDataFound else details.rating.toString())
        val address: String = "<b>" + "ADDRESS: " + "</b>" + (if(details.formatted_address == null) noDataFound else details.formatted_address)
        val phoneNumber: String =  "<b>" + "TEL: " + "</b>" + (if(details.formatted_phone_number == null) noDataFound else details.formatted_phone_number)
        val schedule: String = "<b>" + "SCHEDULE: " + "</b>" + "<br>" + (if(details.opening_hours == null) noDataFound else details.opening_hours.toString())

        cafeName.text = Html.fromHtml(name)
        cafeAddress.text = Html.fromHtml(address)
        cafeRating.text = Html.fromHtml(rating)
        cafePhoneNumber.text = Html.fromHtml(phoneNumber)
        cafeSchedule.text = Html.fromHtml(schedule)
    }
}