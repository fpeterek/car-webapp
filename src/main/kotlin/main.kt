package cz.vsb.cs.democar.webapp

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


// Should only be used internally with API.Response which only returns 200 or 500 or with 404 on invalid request
private val Int.asStatus
    get() = when (this) {
        200 -> HttpStatusCode.OK
        404 -> HttpStatusCode.NotFound
        else -> HttpStatusCode.InternalServerError
    }

private suspend fun ApplicationCall.respondJson(data: String, status: Int) =
    respondText(text=data, contentType=ContentType.Application.Json, status=status.asStatus)


fun main() {

    val conf = Config.default
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
                call.respondJson(response.data, response.code)
            }
            get("/position") {
                val response = api.getPosition()
                call.respondJson(response.data, response.code)
            }
            get("/heading") {
                val response = api.getHeading()
                call.respondJson(response.data, response.code)
            }
            get("/waypoints") {
                val response = api.getWaypoints()
                call.respondJson(response.data, response.code)
            }
            post("/waypoints") {

            }
            delete("/waypoints") {

            }
        }
    }.start(wait=true)
}
