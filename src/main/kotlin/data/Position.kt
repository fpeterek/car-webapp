package cz.vsb.cs.democar.webapp.data

import org.json.JSONObject


data class Position(val latitude: Double, val longitude: Double) {

    companion object {
        fun fromJson(json: JSONObject) = Position(
            latitude=json.getDouble("latitude"),
            longitude=json.getDouble("longitude")
        )
    }

    fun toJson() = JSONObject()
        .put("latitude", latitude)
        .put("longitude", longitude)

    override fun toString() = toJson().toString()
}
