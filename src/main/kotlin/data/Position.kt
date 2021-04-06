package cz.vsb.cs.democar.webapp.data

import org.json.JSONObject


data class Position(val latitude: Double, val longitude: Double) {

    companion object {
        fun fromJson(json: JSONObject) = Position(
            latitude=json.getDouble("latitude"),
            longitude=json.getDouble("longitude")
        )
    }

    private val latitudeIsValid
        get() = -180.0 <= latitude && latitude <= 360.0

    private val longitudeIsValid
        get() = -90.0 <= longitude && longitude <= 90.0

    val isValid
        get() = latitudeIsValid && longitudeIsValid

    fun toJson() = JSONObject()
        .put("latitude", latitude)
        .put("longitude", longitude)

    override fun toString() = toJson().toString()
}
