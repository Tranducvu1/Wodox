package com.wodox.data.home.datasource.local.database.task

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wodox.data.home.datasource.local.database.task.entity.AttachmentEntity
import com.wodox.data.home.datasource.local.database.task.converter.AttachmentTypeConverter
import com.wodox.data.home.datasource.local.database.task.converter.Converter
import com.wodox.data.home.datasource.local.database.task.converter.DateConverter
import com.wodox.data.home.datasource.local.database.task.converter.UUIDConverter
import com.wodox.data.home.datasource.local.database.task.dao.AiChatDao
import com.wodox.data.home.datasource.local.database.task.dao.AttachmentDao
import com.wodox.data.home.datasource.local.database.task.dao.CheckListDao
import com.wodox.data.home.datasource.local.database.task.dao.CommentDao
import com.wodox.data.home.datasource.local.database.task.dao.LogDao
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskAssignDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskDao
import com.wodox.data.home.datasource.local.database.task.dao.UserFriendDao
import com.wodox.data.home.datasource.local.database.task.entity.AiChatEntity
import com.wodox.data.home.datasource.local.database.task.entity.CheckListEntity
import com.wodox.data.home.datasource.local.database.task.entity.CommentEntity
import com.wodox.data.home.datasource.local.database.task.entity.LogEntity
import com.wodox.data.home.datasource.local.database.task.entity.SubTaskEntity
import com.wodox.data.home.datasource.local.database.task.entity.TaskAssigneeEntity
import com.wodox.data.home.datasource.local.database.task.entity.TaskEntity
import com.wodox.data.home.datasource.local.database.task.entity.UserEntity
import com.wodox.data.home.datasource.local.database.task.entity.UserFriendEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskAssigneeEntity::class,
        UserEntity::class,
        AttachmentEntity::class,
        SubTaskEntity::class,
        CheckListEntity::class,
        UserFriendEntity::class,
        LogEntity::class,
        CommentEntity::class,
        AiChatEntity::class
    ],
    version = TaskDatabase.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    UUIDConverter::class,
    Converter::class,
    AttachmentTypeConverter::class,
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun checkListDao(): CheckListDao
    abstract fun userFriendDao(): UserFriendDao
    abstract fun taskAssignDao(): TaskAssignDao
    abstract fun logDao(): LogDao

    abstract fun aiChatDao(): AiChatDao

    abstract fun commentDao() : CommentDao

    companion object {
        private const val DATABASE_NAME = "task.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var db: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return db ?: synchronized(TaskDatabase::class) {
                db ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    DATABASE_NAME
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
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