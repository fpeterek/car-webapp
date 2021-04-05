package cz.vsb.cs.democar.webapp.data

import org.json.JSONArray
import org.json.JSONObject


data class CarInfo(val position: Position, val waypoints: List<Waypoint>, val heading: Double?) {

    companion object {
        fun fromJson(json: JSONObject) = CarInfo(
            position=Position.fromJson(json.getJSONObject("position")),
            waypoints=json.getJSONArray("waypoints").map { Waypoint.fromJson(it as JSONObject) },
            heading=when {
                json.isNull("heading") -> null
                else -> json.getDouble("heading")
            }
        )
    }

    private val waypointsJson
        get() = JSONArray().apply {
            waypoints.forEach {
                put(it.toJson())
            }
        }

    fun toJson() = JSONObject()
        .put("position", position.toJson())
        .put("waypoints", waypointsJson)
        .put("heading", heading)

    override fun toString() = toJson().toString()
}
