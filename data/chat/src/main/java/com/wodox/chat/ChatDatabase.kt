package com.wodox.chat

import com.wodox.chat.dao.NotificationDao
import com.wodox.chat.model.NotificationEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wodox.chat.dao.ChannelDao
import com.wodox.chat.dao.MessageDao
import com.wodox.chat.model.ChannelEntity
import com.wodox.chat.model.ChannelMemberEntity
import com.wodox.chat.model.ChannelMessageEntity
import com.wodox.chat.model.MessageChatEntity
import com.wodox.data.home.datasource.local.database.task.converter.DateConverter
import com.wodox.data.home.datasource.local.database.task.converter.UUIDConverter


@Database(
    entities = [
        NotificationEntity::class,
        MessageChatEntity::class,
        ChannelMessageEntity::class,
        ChannelMemberEntity::class,
        ChannelEntity::class
    ],
    version = ChatDatabase.DATABASE_VERSION,
    exportSchema = false
)

@TypeConverters(
    DateConverter::class,
    UUIDConverter::class,
)

abstract class ChatDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao

    abstract fun messageDao(): MessageDao

    abstract fun channelDao(): ChannelDao

    companion object {
        private const val DATABASE_NAME = "chat.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var db: ChatDatabase? = null
        fun getInstance(context: Context): ChatDatabase {
            return db ?: synchronized(ChatDatabase::class) {
                db ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    DATABASE_NAME
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                        }
                    })
                    .build()
                    .also {
                        it.openHelper.setWriteAheadLoggingEnabled(true)
                        db = it
                    }
            }
        }
    }
}

