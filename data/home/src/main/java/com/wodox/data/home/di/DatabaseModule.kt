package com.wodox.data.home.di

import android.content.Context
import com.wodox.data.home.datasource.local.database.task.TaskDatabase
import com.wodox.data.home.datasource.local.database.task.dao.AiChatDao
import com.wodox.data.home.datasource.local.database.task.dao.AttachmentDao
import com.wodox.data.home.datasource.local.database.task.dao.CheckListDao
import com.wodox.data.home.datasource.local.database.task.dao.CommentDao
import com.wodox.data.home.datasource.local.database.task.dao.LogDao
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskAssignDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskDao
import com.wodox.data.home.datasource.local.database.task.dao.UserFriendDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideTaskDatabase(
        @ApplicationContext app: Context,
    ) = TaskDatabase.getInstance(app)

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideAttachmentDao(database: TaskDatabase): AttachmentDao = database.attachmentDao()

    @Provides
    fun provideSubTaskDao(database: TaskDatabase): SubTaskDao = database.subTaskDao()

    @Provides
    fun provideCheckListDao(database: TaskDatabase): CheckListDao = database.checkListDao()


    @Provides
    fun provideUserFriendDao(database: TaskDatabase): UserFriendDao = database.userFriendDao()

    @Provides
    fun provideTaskAssignDao(database: TaskDatabase): TaskAssignDao = database.taskAssignDao()

    
    @Provides
    fun provideLogDao(database: TaskDatabase): LogDao = database.logDao()

    @Provides
    fun provideCommentDao(database: TaskDatabase): CommentDao = database.commentDao()

    @Provides
    fun provideAiChatDao(database: TaskDatabase): AiChatDao = database.aiChatDao()
}