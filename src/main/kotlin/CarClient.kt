package cz.vsb.cs.democar.webapp

import cz.vsb.cs.democar.webapp.conf.Config
import cz.vsb.cs.democar.webapp.data.CarInfo
import cz.vsb.cs.democar.webapp.data.Position
import cz.vsb.cs.democar.webapp.data.Waypoint
import io.github.rybalkinsd.kohttp.dsl.httpDelete
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject


class CarClient(config: Config) {
    private val apiUrl = config.carApiAddress
    private val apiPort = config.carApiPort
    private val apiScheme = config.carApiScheme

    private val Response.unsuccessful
        get() = !isSuccessful

    private fun getRequest(requestPath: String) = httpGet {
        host = apiUrl
        port = apiPort
        scheme = apiScheme
        path = requestPath
    }

    private fun postRequest(requestPath: String, jsonBody: String) = httpPost {
        host = apiUrl
        port = apiPort
        scheme = apiScheme
        path = requestPath
        body {
            json(jsonBody)
        }
    }

    private fun deleteWaypointRequest(id: Long) = httpDelete {
        host = apiUrl
        port = apiPort
        scheme = apiScheme
        path = "/waypoints"
        param {
            "id" to id
        }
    }

    fun getPosition(): Position? = getRequest("/position").let { resp ->
        resp.use {
            if (resp.unsuccessful) {
                return null
            }

            (resp.body()?.string() ?: "").let { body ->
                when {
                    body.isBlank() -> null
                    else -> Position.fromJson(JSONObject(body))
                }
            }
        }
    }

    fun getWaypoints(): List<Waypoint>? = getRequest("/waypoints").let { resp ->
        resp.use {
            if (resp.unsuccessful) {
                return null
            }

            (resp.body()?.string() ?: "").let { body ->
                when {
                    body.isBlank() -> null
                    else -> JSONArray(body).map { Waypoint.fromJson(it as JSONObject) }
                }
            }
        }
    }

    fun getDirection(): Double? = getRequest("/heading").let { resp ->
        resp.use {
            if (resp.unsuccessful) {
                return null
            }

            (resp.body()?.string() ?: "").let { body ->
                when {
                    body.isBlank() -> null
                    else -> JSONObject(body).let {
                        when {
                            it.isNull("heading") -> null
                            else -> it.getDouble("heading")
                        }
                    }
                }
            }
        }
    }

    fun getInfo(): CarInfo? = getRequest("/info").let { resp ->
        resp.use {
            if (resp.unsuccessful) {
                return null
            }

            (resp.body()?.string() ?: "").let { body ->
                when {
                    body.isBlank() -> null
                    else -> CarInfo.fromJson(JSONObject(body))
                }
            }
        }
    }

    fun postWaypoint(waypoint: Waypoint) =
        postRequest("/waypoints", waypoint.toString()).isSuccessful

    fun deleteWaypoint(id: Long) = deleteWaypointRequest(id).isSuccessful
}
