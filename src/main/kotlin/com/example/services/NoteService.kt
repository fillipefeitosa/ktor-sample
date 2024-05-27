package com.example.services

import com.example.models.Note
import java.util.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class NoteService(database: Database) {
  private object Notes : Table() {
    val id = uuid("id")
    val title = varchar("title", 255)
    val message = text("message")

    override val primaryKey = PrimaryKey(id)
  }

  // Will create the Notes table if it does not exists
  init {
    transaction(database) { SchemaUtils.create(Notes) }
  }

  private suspend fun <T> dbQuery(block: suspend () -> T): T =
      newSuspendedTransaction(Dispatchers.IO) { block() }

  suspend fun findAll(): List<Note> = dbQuery { Notes.selectAll().map { row -> row.toNote() } }

  suspend fun findById(id: UUID): Note? {
    return dbQuery { Notes.select { Notes.id eq id }.map { row -> row.toNote() }.singleOrNull() }
  }

  private fun ResultRow.toNote() =
      Note(id = this[Notes.id], title = this[Notes.title], message = this[Notes.message])
}
