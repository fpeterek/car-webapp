package cz.vsb.cs.democar.webapp

import cz.vsb.cs.democar.webapp.conf.Config
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {

    val conf = Config.fromEnv
    val api = API(conf)

    embeddedServer(Netty, port=conf.webappPort) {
        routing {
            static {
                defaultResource("index.html")
            }
            static("js") {
                resources("js")
            }
            static("img") {
                resources("img")
            }
            get("/info") {
                val response = api.getInfo()
                call.respond(response)
            }
            get("/position") {
                val response = api.getPosition()
                call.respond(response)
            }
            get("/heading") {
                val response = api.getHeading()
                call.respond(response)
            }
            get("/waypoints") {
                val response = api.getWaypoints()
                call.respond(response)
            }
            post("/waypoints") {
                val response = api.postWaypoint(call.receiveText())
                call.respond(response)
            }
            delete("/waypoints") {
                val response = api.deleteWaypointNullable(call.request.queryParameters["id"])
                call.respond(response)
            }
        }
    }.start(wait=true)
}
