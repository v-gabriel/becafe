package hr.ferit.gabrielveselovac.becafe

import android.content.res.Resources
import android.provider.Settings.Global.getString

data class PlaceDetails (
    val formatted_address: String,
    val formatted_phone_number: String,
    val opening_hours: OpenHours,
    val name: String,
    val rating: Double
)

data class OpenHours(
    val weekday_text: List<String>,
) {
    override fun toString(): String {
        var text = ""
        for(i: Int in weekday_text.indices)
        {
            text += weekday_text[i]
            text += "<br>"
        }
        return text;
    }
}