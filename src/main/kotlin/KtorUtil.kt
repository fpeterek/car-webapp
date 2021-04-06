package cz.vsb.cs.democar.webapp

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*


suspend fun ApplicationCall.respondJson(data: String, status: HttpStatusCode) =
    respondText(text=data, contentType=ContentType.Application.Json, status=status)

suspend fun ApplicationCall.respond(response: API.Response) =
    respondJson(response.data, response.status)
