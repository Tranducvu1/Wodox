package com.wodox.chat.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.chat.mapper.ChannelMapper
import com.wodox.chat.mapper.ChannelMemberMapper
import com.wodox.chat.mapper.ChannelMessageMapper
import com.wodox.chat.model.ChannelEntity
import com.wodox.chat.model.ChannelMemberEntity
import com.wodox.chat.model.ChannelMessageEntity
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.model.ChannelRole
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelMapper: ChannelMapper,
    private val channelMemberMapper: ChannelMemberMapper,
    private val channelMessageMapper: ChannelMessageMapper
) : ChannelRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val channelsCollection = firestore.collection("channels")
    private val channelMembersCollection = firestore.collection("channel_members")
    private val channelMessagesCollection = firestore.collection("channel_messages")

    override fun getAllChannels(): Flow<List<Channel>> = callbackFlow {
        val listener = channelsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val channels = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChannelEntity::class.java)?.let {
                        channelMapper.mapToDomain(it)
                    }
                } ?: emptyList()

                trySend(channels)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getChannelById(channelId: UUID): Channel? {
        return try {
            val doc = channelsCollection
                .document(channelId.toString())
                .get()
                .await()

            doc.toObject(ChannelEntity::class.java)?.let {
                channelMapper.mapToDomain(it)
            }
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error getting channel: ${e.message}")
            null
        }
    }

    override fun getJoinedChannels(userId: UUID): Flow<List<Channel>> = callbackFlow {
        android.util.Log.d("ChannelRepository", "getJoinedChannels called with userId: $userId")

        val listener = channelMembersCollection
            .whereEqualTo("userId", userId.toString())
            .addSnapshotListener { memberSnapshot, error ->
                if (error != null) {
                    android.util.Log.e("ChannelRepository", "Error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val channelIds = memberSnapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChannelMemberEntity::class.java)?.channelId
                } ?: emptyList()

                android.util.Log.d("ChannelRepository", "Found ${channelIds.size} joined channels")

                if (channelIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val channelBatches = channelIds.chunked(10)
                val allChannels = mutableListOf<Channel>()

                channelBatches.forEach { batch ->
                    channelsCollection
                        .whereIn("id", batch)
                        .get()
                        .addOnSuccessListener { channelSnapshot ->
                            val channels = channelSnapshot.documents.mapNotNull { doc ->
                                doc.toObject(ChannelEntity::class.java)?.let {
                                    channelMapper.mapToDomain(it.copy(isJoined = true))
                                }
                            }
                            allChannels.addAll(channels)

                            if (allChannels.size >= channelIds.size ||
                                channelBatches.last() == batch
                            ) {
                                android.util.Log.d(
                                    "ChannelRepository",
                                    "Sending ${allChannels.size} channels"
                                )
                                trySend(allChannels.toList())
                            }
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ChannelRepository", "Batch error: ${e.message}")
                        }
                }
            }

        awaitClose { listener.remove() }
    }

    override fun getMyChannels(userId: UUID): Flow<List<Channel>> = callbackFlow {
        val listener = channelsCollection
            .whereEqualTo("creatorId", userId.toString())
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val channels = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChannelEntity::class.java)?.let {
                        channelMapper.mapToDomain(it)
                    }
                } ?: emptyList()

                trySend(channels)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createChannel(channel: Channel): Channel {
        return try {
            val entity = channelMapper.mapToEntity(channel)
            val batch = firestore.batch()

            // Create channel
            val channelRef = channelsCollection.document(entity.id)
            batch.set(channelRef, entity)

            // Add owner as member
            val ownerMember = ChannelMember(
                channelId = channel.id,
                userId = channel.creatorId,
                role = ChannelRole.OWNER
            )
            val memberEntity = channelMemberMapper.mapToEntity(ownerMember)
            val memberRef = channelMembersCollection.document(memberEntity.id)
            batch.set(memberRef, memberEntity)

            batch.commit().await()
            channel
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error creating channel: ${e.message}")
            throw e
        }
    }

    override suspend fun updateChannel(channel: Channel) {
        try {
            val entity = channelMapper.mapToEntity(channel)
            channelsCollection
                .document(entity.id)
                .set(entity)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error updating channel: ${e.message}")
        }
    }

    override suspend fun deleteChannel(channelId: UUID) {
        try {
            val batch = firestore.batch()
            val channelIdStr = channelId.toString()

            // Delete channel
            batch.delete(channelsCollection.document(channelIdStr))

            // Delete all members
            val members = channelMembersCollection
                .whereEqualTo("channelId", channelIdStr)
                .get()
                .await()
            members.documents.forEach { batch.delete(it.reference) }

            // Delete all messages
            val messages = channelMessagesCollection
                .whereEqualTo("channelId", channelIdStr)
                .get()
                .await()
            messages.documents.forEach { batch.delete(it.reference) }

            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error deleting channel: ${e.message}")
        }
    }

    override suspend fun updateLastMessage(channelId: UUID, text: String, time: Long) {
        try {
            channelsCollection
                .document(channelId.toString())
                .update(
                    mapOf(
                        "lastMessageText" to text,
                        "lastMessageTime" to time
                    )
                )
                .await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error updating last message: ${e.message}")
        }
    }

    override suspend fun incrementUnreadCount(channelId: UUID) {
        try {
            channelsCollection
                .document(channelId.toString())
                .update("unreadCount", FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error incrementing unread: ${e.message}")
        }
    }

    override suspend fun clearUnreadCount(channelId: UUID) {
        try {
            channelsCollection
                .document(channelId.toString())
                .update("unreadCount", 0)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error clearing unread: ${e.message}")
        }
    }

    // Channel Members
    override fun getChannelMembers(channelId: UUID): Flow<List<ChannelMember>> = callbackFlow {
        val listener = channelMembersCollection
            .whereEqualTo("channelId", channelId.toString())
            .orderBy("joinedAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val members = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChannelMemberEntity::class.java)?.let {
                        channelMemberMapper.mapToDomain(it)
                    }
                } ?: emptyList()

                trySend(members)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addChannelMember(member: ChannelMember) {
        try {
            val batch = firestore.batch()
            val memberEntity = channelMemberMapper.mapToEntity(member)

            // Add member
            val memberRef = channelMembersCollection.document(memberEntity.id)
            batch.set(memberRef, memberEntity)

            // Increment member count
            val channelRef = channelsCollection.document(member.channelId.toString())
            batch.update(channelRef, "memberCount", FieldValue.increment(1))

            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error adding member: ${e.message}")
        }
    }

    override fun getAllChannels(userId: UUID?): Flow<List<Channel>> = callbackFlow {
        if (userId == null) {
            // Return all channels without join status
            val listener = channelsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val channels = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(ChannelEntity::class.java)?.let {
                            channelMapper.mapToDomain(it)
                        }
                    } ?: emptyList()

                    trySend(channels)
                }

            awaitClose { listener.remove() }
        } else {
            // Get all channels with join status
            val channelsListener = channelsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { channelSnapshot, channelError ->
                    if (channelError != null) {
                        close(channelError)
                        return@addSnapshotListener
                    }

                    // Get user's joined channels
                    channelMembersCollection
                        .whereEqualTo("userId", userId.toString())
                        .get()
                        .addOnSuccessListener { memberSnapshot ->
                            val joinedChannelIds = memberSnapshot.documents.mapNotNull { doc ->
                                doc.toObject(ChannelMemberEntity::class.java)?.channelId
                            }.toSet()

                            val channels = channelSnapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(ChannelEntity::class.java)?.let { entity ->
                                    val isJoined = joinedChannelIds.contains(entity.id)
                                    channelMapper.mapToDomain(entity.copy(isJoined = isJoined))
                                }
                            } ?: emptyList()

                            trySend(channels)
                        }
                        .addOnFailureListener { error ->
                            close(error)
                        }
                }

            awaitClose { channelsListener.remove() }
        }
    }

    override suspend fun removeChannelMember(channelId: UUID, userId: UUID) {
        try {
            val batch = firestore.batch()

            // Find and delete member
            val memberSnapshot = channelMembersCollection
                .whereEqualTo("channelId", channelId.toString())
                .whereEqualTo("userId", userId.toString())
                .get()
                .await()

            memberSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            // Decrement member count
            val channelRef = channelsCollection.document(channelId.toString())
            batch.update(channelRef, "memberCount", FieldValue.increment(-1))

            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error removing member: ${e.message}")
        }
    }

    override suspend fun getChannelMember(channelId: UUID, userId: UUID): ChannelMember? {
        return try {
            val snapshot = channelMembersCollection
                .whereEqualTo("channelId", channelId.toString())
                .whereEqualTo("userId", userId.toString())
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(ChannelMemberEntity::class.java)?.let {
                channelMemberMapper.mapToDomain(it)
            }
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error getting member: ${e.message}")
            null
        }
    }

    override fun getChannelMessages(channelId: UUID): Flow<List<ChannelMessage>> = callbackFlow {
        val listener = channelMessagesCollection
            .whereEqualTo("channelId", channelId.toString())
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChannelMessageEntity::class.java)?.let {
                        channelMessageMapper.mapToDomain(it)
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendChannelMessage(message: ChannelMessage): ChannelMessage {
        try {
            val batch = firestore.batch()
            val messageEntity = channelMessageMapper.mapToEntity(message)

            // Add message
            val messageRef = channelMessagesCollection.document(messageEntity.id)
            batch.set(messageRef, messageEntity)

            // Update last message in channel
            val channelRef = channelsCollection.document(message.channelId.toString())
            batch.update(
                channelRef,
                mapOf(
                    "lastMessageText" to message.text,
                    "lastMessageTime" to message.timestamp
                )
            )

            batch.commit().await()
            return message
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error sending message: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteChannelMessage(message: ChannelMessage) {
        try {
            channelMessagesCollection
                .document(message.id.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error deleting message: ${e.message}")
        }
    }

    override suspend fun clearChannelMessages(channelId: UUID) {
        try {
            val snapshot = channelMessagesCollection
                .whereEqualTo("channelId", channelId.toString())
                .get()
                .await()

            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("ChannelRepository", "Error clearing messages: ${e.message}")
        }
    }
}