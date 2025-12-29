package com.wodox.data.home.di

import com.wodox.data.home.datasource.local.database.task.mapper.AttachmentMapper
import com.wodox.data.home.datasource.local.database.task.mapper.CheckListMapper
import com.wodox.data.home.datasource.local.database.task.mapper.CommentMapper
import com.wodox.data.home.datasource.local.database.task.mapper.LogMapper
import com.wodox.data.home.datasource.local.database.task.mapper.SubTaskMapper
import com.wodox.data.home.datasource.local.database.task.mapper.TaskAssigneeMapper
import com.wodox.data.home.datasource.local.database.task.mapper.TaskMapper
import com.wodox.data.home.datasource.local.database.task.mapper.UserFriendMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun providerTaskMapper(
    ): TaskMapper {
        return TaskMapper()
    }

    @Singleton
    @Provides
    fun providerAttachmentMapper(
    ): AttachmentMapper {
        return AttachmentMapper()
    }

    @Singleton
    @Provides
    fun providerSubTaskMapper(
    ): SubTaskMapper {
        return SubTaskMapper()
    }

    @Singleton
    @Provides
    fun providerCheckListMapper(
    ): CheckListMapper {
        return CheckListMapper()
    }

    @Singleton
    @Provides
    fun providerUserFriendMapper(
    ): UserFriendMapper {
        return UserFriendMapper()
    }

    @Singleton
    @Provides
    fun providerTaskAssigneeMapper(
    ): TaskAssigneeMapper {
        return TaskAssigneeMapper()
    }
    
    @Singleton
    @Provides
    fun providerLogMapper(
    ): LogMapper {
        return LogMapper()
    }

    @Singleton
    @Provides
    fun providerCommentMapper(
    ): CommentMapper {
        return CommentMapper()
    }
}