package com.wodox.chat.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.chat.mapper.MessageChatMapper
import com.wodox.chat.model.MessageChatEntity
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageChatMapper: MessageChatMapper,
) : ChatRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val messagesCollection = firestore.collection("messages")

    companion object {
        private const val TAG = "ChatRepository"
    }

    override fun searchMessages(query: String): Flow<List<MessageChat>> = callbackFlow {
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "SEARCH MESSAGES - Query: $query")
        android.util.Log.d(TAG, "========================================")

        val listener = messagesCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e(TAG, "Search error", error)
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(MessageChatEntity::class.java)?.let { entity ->
                            messageChatMapper.mapToDomain(entity)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Error parsing message", e)
                        null
                    }
                }?.filter { message ->
                    message.text.contains(query, ignoreCase = true)
                } ?: emptyList()

                android.util.Log.d(TAG, "Search found ${messages.size} messages")
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(message: MessageChat): MessageChat {
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "SEND MESSAGE - START")
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "Message ID: ${message.id}")
        android.util.Log.d(TAG, "Sender ID: ${message.senderId}")
        android.util.Log.d(TAG, "Receiver ID: ${message.receiverId}")
        android.util.Log.d(TAG, "Text: ${message.text}")
        android.util.Log.d(TAG, "Timestamp: ${message.timestamp}")

        return try {
            val entity = messageChatMapper.mapToEntity(message)

            android.util.Log.d(TAG, "----------------------------------------")
            android.util.Log.d(TAG, "MAPPED TO ENTITY:")
            android.util.Log.d(TAG, "Entity ID: ${entity.id}")
            android.util.Log.d(TAG, "Entity Sender: ${entity.senderId}")
            android.util.Log.d(TAG, "Entity Receiver: ${entity.receiverId}")
            android.util.Log.d(TAG, "Entity Text: ${entity.text}")
            android.util.Log.d(TAG, "Entity Timestamp: ${entity.timestamp}")
            android.util.Log.d(TAG, "----------------------------------------")

            android.util.Log.d(TAG, "Saving to Firestore...")
            messagesCollection
                .document(entity.id)
                .set(entity)
                .await()

            android.util.Log.d(TAG, "✅ MESSAGE SAVED SUCCESSFULLY!")

            // Verify saved message
            android.util.Log.d(TAG, "Verifying saved message...")
            val savedDoc = messagesCollection
                .document(entity.id)
                .get()
                .await()

            if (savedDoc.exists()) {
                android.util.Log.d(TAG, "✅ Document exists in Firestore")
                android.util.Log.d(TAG, "Document data: ${savedDoc.data}")
                android.util.Log.d(TAG, "  senderId: ${savedDoc.getString("senderId")}")
                android.util.Log.d(TAG, "  receiverId: ${savedDoc.getString("receiverId")}")
                android.util.Log.d(TAG, "  timestamp: ${savedDoc.getLong("timestamp")}")
            } else {
                android.util.Log.e(TAG, "❌ Document NOT found in Firestore!")
            }

            // Test manual query
            android.util.Log.d(TAG, "Testing manual query...")
            val testQuery = messagesCollection
                .whereEqualTo("senderId", entity.senderId)
                .whereEqualTo("receiverId", entity.receiverId)
                .get()
                .await()

            android.util.Log.d(TAG, "Manual query result: ${testQuery.documents.size} documents")
            testQuery.documents.forEach { doc ->
                android.util.Log.d(TAG, "  Found doc: ${doc.id} - ${doc.getString("text")}")
            }

            android.util.Log.d(TAG, "========================================")

            messageChatMapper.mapToDomain(entity)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "========================================")
            android.util.Log.e(TAG, "❌ ERROR SAVING MESSAGE", e)
            android.util.Log.e(TAG, "Error message: ${e.message}")
            android.util.Log.e(TAG, "Error cause: ${e.cause}")
            e.printStackTrace()
            android.util.Log.e(TAG, "========================================")
            throw e
        }
    }

    override suspend fun updateMessage(message: MessageChat) {
        android.util.Log.d(TAG, "UPDATE MESSAGE: ${message.id}")
        try {
            val entity = messageChatMapper.mapToEntity(message)
            messagesCollection
                .document(entity.id)
                .set(entity)
                .await()
            android.util.Log.d(TAG, "✅ Message updated")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error updating message", e)
            e.printStackTrace()
        }
    }

    override suspend fun deleteMessage(id: UUID) {
        android.util.Log.d(TAG, "DELETE MESSAGE: $id")
        try {
            messagesCollection
                .document(id.toString())
                .delete()
                .await()
            android.util.Log.d(TAG, "✅ Message deleted")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error deleting message", e)
            e.printStackTrace()
        }
    }

    override suspend fun clearMessages() {
        android.util.Log.d(TAG, "CLEAR ALL MESSAGES")
        try {
            val snapshot = messagesCollection.get().await()
            val batch = firestore.batch()

            android.util.Log.d(TAG, "Found ${snapshot.documents.size} messages to delete")

            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
            android.util.Log.d(TAG, "✅ All messages cleared")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error clearing messages", e)
            e.printStackTrace()
        }
    }

    override fun getConversationMessages(
        currentUserId: UUID,
        friendUserId: UUID
    ): Flow<List<MessageChat>> = callbackFlow {
        val currentUserIdStr = currentUserId.toString()
        val friendUserIdStr = friendUserId.toString()

        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "GET CONVERSATION MESSAGES - START")
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "Current User ID: $currentUserIdStr")
        android.util.Log.d(TAG, "Friend User ID: $friendUserIdStr")
        android.util.Log.d(TAG, "========================================")

        var sentMessages = listOf<MessageChatEntity>()
        var receivedMessages = listOf<MessageChatEntity>()
        var query1InitialLoad = false
        var query2InitialLoad = false

        fun emitCombinedMessages() {
            val allMessages = (sentMessages + receivedMessages)
                .sortedBy { it.timestamp }
                .map { entity ->
                    val domainMessage = messageChatMapper.mapToDomain(entity)
                    domainMessage.copy(
                        isCurrentUser = entity.senderId == currentUserIdStr
                    )
                }

            android.util.Log.d(TAG, "----------------------------------------")
            android.util.Log.d(TAG, "EMITTING COMBINED MESSAGES:")
            android.util.Log.d(TAG, "  Sent messages: ${sentMessages.size}")
            android.util.Log.d(TAG, "  Received messages: ${receivedMessages.size}")
            android.util.Log.d(TAG, "  Total messages: ${allMessages.size}")
            android.util.Log.d(
                TAG,
                "  Current user messages: ${allMessages.count { it.isCurrentUser }}"
            )
            android.util.Log.d(TAG, "  Friend messages: ${allMessages.count { !it.isCurrentUser }}")

            allMessages.forEachIndexed { index, msg ->
                android.util.Log.d(
                    TAG,
                    "  [$index] ${if (msg.isCurrentUser) "ME" else "FRIEND"}: ${msg.text.take(30)}..."
                )
            }
            android.util.Log.d(TAG, "----------------------------------------")

            trySend(allMessages)
        }

        // Query 1: Messages sent by current user to friend
        android.util.Log.d(TAG, "Setting up Query 1: Current -> Friend")
        val listener1 = messagesCollection
            .whereEqualTo("senderId", currentUserIdStr)
            .whereEqualTo("receiverId", friendUserIdStr)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e(TAG, "❌ Query 1 ERROR", error)
                    android.util.Log.e(TAG, "Error message: ${error.message}")
                    return@addSnapshotListener
                }

                if (!query1InitialLoad) {
                    android.util.Log.d(TAG, "📥 Query 1 INITIAL LOAD")
                    query1InitialLoad = true
                } else {
                    android.util.Log.d(TAG, "🔄 Query 1 UPDATE")
                }

                sentMessages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val entity = doc.toObject(MessageChatEntity::class.java)
                        android.util.Log.d(
                            TAG,
                            "  Parsed sent message: ${entity?.id} - ${entity?.text?.take(30)}"
                        )
                        entity
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "  ❌ Error parsing sent message", e)
                        null
                    }
                } ?: emptyList()

                android.util.Log.d(TAG, "Query 1 result: ${sentMessages.size} sent messages")
                emitCombinedMessages()
            }

        // Query 2: Messages received by current user from friend
        android.util.Log.d(TAG, "Setting up Query 2: Friend -> Current")
        val listener2 = messagesCollection
            .whereEqualTo("senderId", friendUserIdStr)
            .whereEqualTo("receiverId", currentUserIdStr)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e(TAG, "❌ Query 2 ERROR", error)
                    android.util.Log.e(TAG, "Error message: ${error.message}")
                    return@addSnapshotListener
                }

                if (!query2InitialLoad) {
                    android.util.Log.d(TAG, "📥 Query 2 INITIAL LOAD")
                    query2InitialLoad = true
                } else {
                    android.util.Log.d(TAG, "🔄 Query 2 UPDATE")
                }

                receivedMessages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val entity = doc.toObject(MessageChatEntity::class.java)
                        android.util.Log.d(
                            TAG,
                            "  Parsed received message: ${entity?.id} - ${entity?.text?.take(30)}"
                        )
                        entity
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "  ❌ Error parsing received message", e)
                        null
                    }
                } ?: emptyList()

                android.util.Log.d(
                    TAG,
                    "Query 2 result: ${receivedMessages.size} received messages"
                )
                emitCombinedMessages()
            }

        awaitClose {
            android.util.Log.d(TAG, "========================================")
            android.util.Log.d(TAG, "CLOSING CONVERSATION LISTENERS")
            android.util.Log.d(TAG, "========================================")
            listener1.remove()
            listener2.remove()
        }
    }
}