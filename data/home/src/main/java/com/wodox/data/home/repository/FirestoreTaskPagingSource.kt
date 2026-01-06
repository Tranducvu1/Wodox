package com.wodox.data.home.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.wodox.domain.home.model.local.*
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class FirestoreTaskPagingSource(
    private val tasksCollection: CollectionReference,
    private val ownerId: UUID,
    private val favouriteOnly: Boolean = false
) : PagingSource<DocumentSnapshot, Task>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Task> {
        return try {
            var query: Query = tasksCollection.whereEqualTo("ownerId", ownerId.toString())

            if (favouriteOnly) {
                query = query.whereEqualTo("isFavourite", true)
            }

            query = query.orderBy("calculatedPriority", Query.Direction.DESCENDING)
                .limit(params.loadSize.toLong())

            params.key?.let { lastDocument ->
                query = query.startAfter(lastDocument)
            }

            val snapshot: QuerySnapshot = query.get().await()
            val tasks = snapshot.documents.mapNotNull { doc ->
                mapDocumentToTask(doc)
            }

            val nextKey = if (snapshot.documents.isNotEmpty()) {
                snapshot.documents.last()
            } else {
                null
            }

            LoadResult.Page(
                data = tasks, prevKey = null, nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Task>): DocumentSnapshot? {
        return null
    }

    private fun mapDocumentToTask(doc: DocumentSnapshot): Task? {
        return try {
            val id = UUID.fromString(doc.getString("id") ?: return null)
            val ownerId = UUID.fromString(doc.getString("ownerId") ?: return null)

            Task(
                id = id,
                ownerId = ownerId,
                title = doc.getString("title") ?: "",
                description = doc.getString("description"),
                status = TaskStatus.valueOf(doc.getString("status") ?: "TODO"),
                priority = Priority.fromValue(doc.getLong("priority")?.toInt() ?: 1),
                difficulty = Difficulty.fromValue(doc.getLong("difficulty")?.toInt() ?: 1),
                support = SupportLevel.fromValue(doc.getLong("support")?.toInt() ?: 0),
                startAt = doc.getTimestamp("startAt")?.toDate(),
                dueAt = doc.getTimestamp("dueAt")?.toDate(),
                isFavourite = doc.getBoolean("isFavourite") ?: false,
                createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                deletedAt = doc.getTimestamp("deletedAt")?.toDate(),
                assignedUserIds = (doc.get("assignedUserIds") as? List<*>)?.mapNotNull {
                        UUID.fromString(it as? String)
                    } ?: emptyList(),
                calculatedPriority = doc.getDouble("calculatedPriority") ?: 0.0)
        } catch (e: Exception) {
            null
        }
    }
}