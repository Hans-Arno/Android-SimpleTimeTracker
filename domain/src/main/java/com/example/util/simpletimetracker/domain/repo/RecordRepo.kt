package com.example.util.simpletimetracker.domain.repo

import com.example.util.simpletimetracker.domain.model.Range
import com.example.util.simpletimetracker.domain.model.Record

interface RecordRepo {

    suspend fun isEmpty(): Boolean

    suspend fun getAll(adjusted: Boolean): List<Record>

    suspend fun getByType(typeIds: List<Long>): List<Record>

    suspend fun getByTypeWithAnyComment(typeIds: List<Long>): List<Record>

    suspend fun searchComment(text: String): List<Record>

    suspend fun searchByTypeWithComment(typeIds: List<Long>, text: String): List<Record>

    suspend fun searchAnyComments(): List<Record>

    suspend fun get(id: Long, adjusted: Boolean): Record?

    suspend fun getFromRange(range: Range, adjusted: Boolean): List<Record>

    suspend fun getFromRangeByType(typeIds: List<Long>, range: Range): List<Record>

    suspend fun getPrev(
        timeStarted: Long,
        limit: Long,
        adjusted: Boolean,
    ): List<Record>

    suspend fun getNext(timeEnded: Long, adjusted: Boolean): Record?

    suspend fun add(record: Record): Long

    suspend fun update(
        recordId: Long,
        typeId: Long,
        comment: String,
    )

    suspend fun remove(id: Long)

    suspend fun removeByType(typeId: Long)

    suspend fun clear()

    fun clearCache()
}