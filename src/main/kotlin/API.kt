package cz.vsb.cs.democar.webapp

import cz.vsb.cs.democar.webapp.data.Waypoint
import org.json.JSONArray
import org.json.JSONObject


class API(config: Config) {

    data class Response(val code: Int, val data: String) {
        companion object {
            val failure
                get() = Response(500, JSONObject().put("failure", "failure").toString())

            val success
                get() = Response(200, JSONObject().put("success", "success").toString())

            fun success(data: String) = Response(200, data)
        }
    }

    val client = CarClient(config)

    private fun formatWaypointList(list: List<Waypoint>) = JSONArray().apply {
        list.forEach {
            put(it.toJson())
        }
    }.toString()

    private fun formatHeading(heading: Double?) = JSONObject()
        .put("heading", heading)
        .toString()

    fun getWaypoints() = client.getWaypoints().let {
        when (it) {
            null -> Response.failure
            else -> Response.success(formatWaypointList(it))
        }
    }

    fun getHeading() = Response.success(formatHeading(client.getDirection()))

    fun getPosition() = client.getPosition().let {
        when (it) {
            null -> Response.failure
            else -> Response.success(it.toString())
        }
    }

    fun getInfo() = client.getInfo().let {
        when (it) {
            null -> Response.failure
            else -> Response.success(it.toString())
        }
    }

    fun deleteWaypoint(id: Long) = when (client.deleteWaypoint(id)) {
        true -> Response.success
        else -> Response.failure
    }

    fun postWaypoint(waypoint: Waypoint) = when (client.postWaypoint(waypoint)) {
        true -> Response.success
        else -> Response.failure
    }
}
