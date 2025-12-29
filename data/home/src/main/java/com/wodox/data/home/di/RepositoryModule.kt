package com.wodox.data.home.di

import com.google.gson.Gson
import com.wodox.data.home.datasource.local.database.task.dao.AiChatDao
import com.wodox.data.home.datasource.local.database.task.dao.AttachmentDao
import com.wodox.data.home.datasource.local.database.task.dao.CheckListDao
import com.wodox.data.home.datasource.local.database.task.dao.CommentDao
import com.wodox.data.home.datasource.local.database.task.dao.LogDao
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskAssignDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskDao
import com.wodox.data.home.datasource.local.database.task.dao.UserFriendDao
import com.wodox.data.home.datasource.local.database.task.mapper.AiChatMapper
import com.wodox.data.home.datasource.local.database.task.mapper.AttachmentMapper
import com.wodox.data.home.datasource.local.database.task.mapper.CheckListMapper
import com.wodox.data.home.datasource.local.database.task.mapper.CommentMapper
import com.wodox.data.home.datasource.local.database.task.mapper.LogMapper
import com.wodox.data.home.datasource.local.database.task.mapper.SubTaskMapper
import com.wodox.data.home.datasource.local.database.task.mapper.TaskAssigneeMapper
import com.wodox.data.home.datasource.local.database.task.mapper.TaskMapper
import com.wodox.data.home.datasource.local.database.task.mapper.UserFriendMapper
import com.wodox.data.home.datasource.remote.datasource.AIChatDataSource
import com.wodox.data.home.repository.AttachmentRepositoryImpl
import com.wodox.data.home.repository.CheckListRepositoryImpl
import com.wodox.data.home.repository.CommentRepositoryImpl
import com.wodox.data.home.repository.LogRepositoryImpl
import com.wodox.data.home.repository.SubTaskRepositoryImpl
import com.wodox.data.home.repository.TaskAssignRepositoryImpl
import com.wodox.domain.home.repository.TaskRepository
import com.wodox.data.home.repository.TaskRepositoryImpl
import com.wodox.data.home.repository.UserFriendRepositoryImpl
import com.wodox.domain.home.repository.AttachmentRepository
import com.wodox.domain.home.repository.CheckListRepository
import com.wodox.domain.home.repository.CommentRepository
import com.wodox.domain.home.repository.LogRepository
import com.wodox.domain.home.repository.SubTaskRepository
import com.wodox.domain.home.repository.TaskAssignRepository
import com.wodox.domain.home.repository.UserFriendRepository
import com.wodox.domain.user.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.sql.DataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTaskRepository(
        mapper: TaskMapper,
        dao: TaskDao,
        aiDataSource: AIChatDataSource,
        userRepository: UserRepository,
        aiChatMapper: AiChatMapper,
        aiChatDao: AiChatDao,
        userFriendRepository: UserFriendRepository,
        gson: Gson,
    ): TaskRepository {
        return TaskRepositoryImpl(
            dao,
            mapper,
            aiChatMapper,
            aiChatDao,
            aiDataSource,
            userRepository,
            userFriendRepository,
            gson,
        )
    }

    @Singleton
    @Provides
    fun provideAttachmentRepository(
        mapper: AttachmentMapper,
        dao: AttachmentDao,
    ): AttachmentRepository {
        return AttachmentRepositoryImpl(
            dao,
            mapper
        )
    }

    @Singleton
    @Provides
    fun provideSubTaskRepository(
        mapper: SubTaskMapper,
        dao: SubTaskDao,
    ): SubTaskRepository {
        return SubTaskRepositoryImpl(
            dao,
            mapper
        )
    }

    @Singleton
    @Provides
    fun provideCheckListRepository(
        mapper: CheckListMapper,
        dao: CheckListDao,
    ): CheckListRepository {
        return CheckListRepositoryImpl(
            dao,
            mapper
        )
    }

    @Singleton
    @Provides
    fun provideUserFriendRepository(
        mapper: UserFriendMapper,
        dao: UserFriendDao,
    ): UserFriendRepository {
        return UserFriendRepositoryImpl(
            dao,
            mapper
        )
    }


    @Singleton
    @Provides
    fun provideTaskAssignRepository(
        mapper: TaskAssigneeMapper,
        dao: TaskAssignDao,
    ): TaskAssignRepository {
        return TaskAssignRepositoryImpl(
            dao,
            mapper
        )
    }


    @Singleton
    @Provides
    fun provideLogRepository(
        mapper: LogMapper,
        dao: LogDao,
    ): LogRepository {
        return LogRepositoryImpl(
            dao,
            mapper
        )
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        dao: CommentDao,
        mapper: CommentMapper,
    ): CommentRepository {
        return CommentRepositoryImpl(dao, mapper)
    }
}
