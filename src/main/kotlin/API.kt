package cz.vsb.cs.democar.webapp

import cz.vsb.cs.democar.webapp.conf.Config
import cz.vsb.cs.democar.webapp.data.Waypoint
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class API(config: Config) {

    data class Response(val status: HttpStatusCode, val data: String) {
        companion object {

            private fun jsonMessage(key: String, value: String) = JSONObject()
                .put(key, value)
                .toString()

            val serverError
                get() = Response(HttpStatusCode.InternalServerError, jsonMessage("failure", "Server error"))

            val success
                get() = Response(HttpStatusCode.OK, JSONObject().put("success", "success").toString())

            fun badRequest(msg: String) = Response(HttpStatusCode.BadRequest, jsonMessage("failure", msg))

            fun success(data: String) = Response(HttpStatusCode.OK, data)
        }
    }

    private val client = CarClient(config)

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
            null -> Response.serverError
            else -> Response.success(formatWaypointList(it))
        }
    }

    fun getHeading() = Response.success(formatHeading(client.getDirection()))

    fun getPosition() = client.getPosition().let {
        when (it) {
            null -> Response.serverError
            else -> Response.success(it.toString())
        }
    }

    fun getInfo() = client.getInfo().let {
        when (it) {
            null -> Response.serverError
            else -> Response.success(it.toString())
        }
    }

    fun deleteWaypoint(id: Long) = when (client.deleteWaypoint(id)) {
        true -> Response.success
        else -> Response.serverError
    }

    fun postWaypoint(waypoint: Waypoint) = when (client.postWaypoint(waypoint)) {
        true -> Response.success
        else -> Response.serverError
    }

    fun postWaypoint(waypoint: String) = try {
        val wp = Waypoint.fromJson(JSONObject(waypoint))
        when {
            wp.position.isValid -> postWaypoint(wp)
            else -> Response.badRequest("Invalid position")
        }
    } catch (e: JSONException) {
        Response.badRequest("Invalid JSON")
    }

    fun deleteWaypoint(id: String) = id.toLongOrNull().let {
        when (it) {
            null -> Response.badRequest("Invalid waypoint ID")
            else -> deleteWaypoint(it)
        }
    }

    fun deleteWaypointNullable(id: String?) = when (id) {
        null -> Response.badRequest("Missing waypoint ID")
        else -> deleteWaypoint(id)
    }
}
