package com.example.modules

import com.example.models.toNoteResponse
import com.example.services.NoteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureNoteRouting(service: NoteService) {
  routing {
    get("/notes") {
      val response = service.findAll().map { it.toNoteResponse() }
      call.respond(HttpStatusCode.OK, response)
    }
    get("/notes/{id}") {
      val id = UUID.fromString(call.parameters["id"])
      service.findById(id)?.let { note ->
        val response = note.toNoteResponse()
        call.respond(HttpStatusCode.OK, response)
      }
          ?: call.respond(HttpStatusCode.NotFound)
    }
  }
}
