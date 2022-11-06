package hr.ferit.gabrielveselovac.becafe

data class Place(
    val name: String,
    val place_id: String,
    val geometry: Geometry,
    val rating: Double
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)