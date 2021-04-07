package cz.vsb.cs.democar.webapp.data

import org.json.JSONObject


data class Waypoint(val position: Position, val id: Long = -1) {

    companion object {
        fun fromJson(json: JSONObject) = Waypoint(
            position = Position.fromJson(json.getJSONObject("position")),
            id = if (json.has("id")) { json.getLong("id") } else { -1 }
        )
    }

    fun toJson() = JSONObject()
        .put("position", position.toJson())
        .put("id", id)

    override fun toString() = toJson().toString()
}
