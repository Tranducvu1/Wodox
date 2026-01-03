package com.wodox.domain.home.di

import com.wodox.domain.home.repository.AttachmentRepository
import com.wodox.domain.home.repository.CheckListRepository
import com.wodox.domain.home.repository.CommentRepository
import com.wodox.domain.home.repository.LogRepository
import com.wodox.domain.home.repository.SubTaskRepository
import com.wodox.domain.home.repository.TaskAssignRepository
import com.wodox.domain.home.repository.TaskRepository
import com.wodox.domain.home.repository.UserFriendRepository
import com.wodox.domain.home.usecase.AddFriendUseCase
import com.wodox.domain.home.usecase.task.AnalyzeUserTasksUseCase
import com.wodox.domain.home.usecase.log.DeleteAllLogsByTaskIdUseCase
import com.wodox.domain.home.usecase.log.DeleteLogUseCase
import com.wodox.domain.home.usecase.taskassign.AssignUserToTaskUseCase
import com.wodox.domain.home.usecase.FindRelationUseCase
//import com.wodox.domain.home.usecase.GetTaskByDateUseCase
import com.wodox.domain.home.usecase.GetAllItemUseCase
import com.wodox.domain.home.usecase.log.GetAllLogUseCase
import com.wodox.domain.home.usecase.log.GetAllLogsByTaskIdUseCase
import com.wodox.domain.home.usecase.GetAllMenuOptionUseCase
import com.wodox.domain.home.usecase.task.GetAllTaskFavourite
import com.wodox.domain.home.usecase.task.GetAllTaskUseCase
import com.wodox.domain.home.usecase.task.GetAllTasksByUserUseCase
import com.wodox.domain.home.usecase.GetAttachmentUseCase
import com.wodox.domain.home.usecase.GetFriendAcceptUseCase
import com.wodox.domain.home.usecase.GetFriendByUseCase
import com.wodox.domain.home.usecase.GetFriendRequestUseCase
import com.wodox.domain.home.usecase.GetFriendSentUseCase
import com.wodox.domain.home.usecase.GetSuggestedSupportersUseCase
import com.wodox.domain.home.usecase.task.GetTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.task.GetTaskCalendarUseCase
import com.wodox.domain.home.usecase.taskassign.GetTaskAssignByTaskId
import com.wodox.domain.home.usecase.task.GetTaskUseCase
import com.wodox.domain.home.usecase.SaveAttachmentUseCase
import com.wodox.domain.home.usecase.log.SaveLogUseCase
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import com.wodox.domain.home.usecase.aichat.DeleteAllChatsByTaskUseCase
import com.wodox.domain.home.usecase.aichat.DeleteChatHistoryUseCase
import com.wodox.domain.home.usecase.aichat.GetChatsByTaskIdUseCase
import com.wodox.domain.home.usecase.aichat.SaveChatHistoryUseCase
import com.wodox.domain.home.usecase.checklist.GetCheckListByTaskIdUseCase
import com.wodox.domain.home.usecase.checklist.SaveCheckListUseCase
import com.wodox.domain.home.usecase.comment.DeleteCommentByTaskIdUseCase
import com.wodox.domain.home.usecase.comment.DeleteCommentUseCase
import com.wodox.domain.home.usecase.comment.GetAllCommentByTaskIdUseCase
import com.wodox.domain.home.usecase.comment.GetLatestUnreadCommentUseCase
import com.wodox.domain.home.usecase.comment.SaveCommentUseCase
import com.wodox.domain.home.usecase.subtask.GetAllSubTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.subtask.SaveSubTaskUseCase
import com.wodox.domain.home.usecase.task.AskAIUseCase
import com.wodox.domain.home.usecase.task.GetCompletedTasksUseCase
import com.wodox.domain.home.usecase.task.GetOverdueTasksUseCase
import com.wodox.domain.home.usecase.task.GetPendingTasksUseCase
import com.wodox.domain.home.usecase.task.GetTasksByPriorityUseCase
import com.wodox.domain.home.usecase.task.GetTasksByStatusUseCase
import com.wodox.domain.home.usecase.task.GetTasksSortedByNameUseCase
import com.wodox.domain.home.usecase.task.GetTasksSortedByPriorityUseCase
import com.wodox.domain.home.usecase.taskassign.GetTaskAssignByUserIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HomeUseCaseModule {

    @Provides
    @Singleton
    fun provideGetAllTaskUseCase(
        taskRepository: TaskRepository,
    ): GetAllTaskUseCase {
        return GetAllTaskUseCase(taskRepository)
    }

//    @Provides
//    @Singleton
//    fun provideGetTaskByDateUseCase(
//        taskRepository: TaskRepository,
//    ): GetTaskByDateUseCase {
//        return GetTaskByDateUseCase(taskRepository)
//    }

    @Provides
    @Singleton
    fun provideSaveTaskUseCase(
        taskRepository: TaskRepository,
    ): SaveTaskUseCase {
        return SaveTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllTaskFavourite(
        taskRepository: TaskRepository,
    ): GetAllTaskFavourite {
        return GetAllTaskFavourite(taskRepository)
    }


    @Provides
    @Singleton
    fun provideGetAllTasksByUserUseCase(
        taskRepository: TaskRepository,
    ): GetAllTasksByUserUseCase {
        return GetAllTasksByUserUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskUseCase(
        taskRepository: TaskRepository,
    ): GetTaskUseCase {
        return GetTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideAnalyzeUserTasksUseCase(
        taskRepository: TaskRepository,
    ): AnalyzeUserTasksUseCase {
        return AnalyzeUserTasksUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetSuggestedSupportersUseCase(
        taskRepository: TaskRepository,
    ): GetSuggestedSupportersUseCase {
        return GetSuggestedSupportersUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetAttachmentUseCase(
        attachmentRepository: AttachmentRepository,
    ): GetAttachmentUseCase {
        return GetAttachmentUseCase(attachmentRepository)
    }

    @Provides
    @Singleton
    fun provideSaveAttachmentUseCase(
        attachmentRepository: AttachmentRepository,
    ): SaveAttachmentUseCase {
        return SaveAttachmentUseCase(attachmentRepository)
    }

    @Provides
    @Singleton
    fun provideGetSubTaskUseCase(
        taskRepository: SubTaskRepository,
    ): GetAllSubTaskByTaskIdUseCase {
        return GetAllSubTaskByTaskIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveSubTaskUseCase(
        taskRepository: SubTaskRepository,
    ): SaveSubTaskUseCase {
        return SaveSubTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveCheckListUseCase(
        taskRepository: CheckListRepository,
    ): SaveCheckListUseCase {
        return SaveCheckListUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetCheckListByTaskIdUseCase(
        taskRepository: CheckListRepository,
    ): GetCheckListByTaskIdUseCase {
        return GetCheckListByTaskIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllItemUseCase(
    ): GetAllItemUseCase {
        return GetAllItemUseCase()
    }


    @Provides
    @Singleton
    fun provideGetAllMenuOptionUseCase(
    ): GetAllMenuOptionUseCase {
        return GetAllMenuOptionUseCase()
    }

    @Provides
    @Singleton
    fun provideAskAIUseCase(
        taskRepository: TaskRepository,
    ): AskAIUseCase {
        return AskAIUseCase(taskRepository)
    }


    @Provides
    @Singleton
    fun provideAddFriendUseCase(
        userRepository: UserFriendRepository,
    ): AddFriendUseCase {
        return AddFriendUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetFriendByUseCase(
        userRepository: UserFriendRepository,
    ): GetFriendByUseCase {
        return GetFriendByUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetFriendRequestUseCase(
        userRepository: UserFriendRepository,
    ): GetFriendRequestUseCase {
        return GetFriendRequestUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetFriendAcceptUseCase(
        userRepository: UserFriendRepository,
    ): GetFriendAcceptUseCase {
        return GetFriendAcceptUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideFindRelationUseCase(
        userRepository: UserFriendRepository,
    ): FindRelationUseCase {
        return FindRelationUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetFriendSentUseCase(
        userRepository: UserFriendRepository,
    ): GetFriendSentUseCase {
        return GetFriendSentUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideAssignUserToTaskUseCase(
        taskRepository: TaskAssignRepository,
    ): AssignUserToTaskUseCase {
        return AssignUserToTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskAssignByTaskId(
        taskRepository: TaskAssignRepository,
    ): GetTaskAssignByTaskId {
        return GetTaskAssignByTaskId(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskAssignByUserIdUseCase(
        taskRepository: TaskAssignRepository,
    ): GetTaskAssignByUserIdUseCase {
        return GetTaskAssignByUserIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskByTaskIdUseCase(
        taskRepository: TaskRepository,
    ): GetTaskByTaskIdUseCase {
        return GetTaskByTaskIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTaskCalendarUseCase(
        taskRepository: TaskRepository,
    ): GetTaskCalendarUseCase {
        return GetTaskCalendarUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveLogUseCase(
        taskRepository: LogRepository,
    ): SaveLogUseCase {
        return SaveLogUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllLogUseCase(
        taskRepository: LogRepository,
    ): GetAllLogUseCase {
        return GetAllLogUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveCommentUseCase(
        commentRepository: CommentRepository,
    ): SaveCommentUseCase {
        return SaveCommentUseCase(commentRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllCommentByTaskIdUseCase(
        commentRepository: CommentRepository,
    ): GetAllCommentByTaskIdUseCase {
        return GetAllCommentByTaskIdUseCase(commentRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteCommentUseCase(
        commentRepository: CommentRepository,
    ): DeleteCommentUseCase {
        return DeleteCommentUseCase(commentRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteCommentByTaskIdUseCase(
        commentRepository: CommentRepository,
    ): DeleteCommentByTaskIdUseCase {
        return DeleteCommentByTaskIdUseCase(commentRepository)
    }


    @Provides
    @Singleton
    fun provideGetLatestUnreadCommentUseCase(
        commentRepository: CommentRepository,
    ): GetLatestUnreadCommentUseCase {
        return GetLatestUnreadCommentUseCase(commentRepository)
    }

    @Provides
    @Singleton
    fun provideGetChatsByTaskIdUseCase(
        taskRepository: TaskRepository
    ): GetChatsByTaskIdUseCase {
        return GetChatsByTaskIdUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteChatHistoryUseCase(
        taskRepository: TaskRepository
    ): DeleteChatHistoryUseCase {
        return DeleteChatHistoryUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveChatHistoryUseCase(
        taskRepository: TaskRepository
    ): SaveChatHistoryUseCase {
        return SaveChatHistoryUseCase(taskRepository)
    }


    @Provides
    @Singleton
    fun provideDeleteAllChatsByTaskUseCase(
        taskRepository: TaskRepository
    ): DeleteAllChatsByTaskUseCase {
        return DeleteAllChatsByTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllLogsByTaskIdUseCase(
        logRepository: LogRepository,
    ): GetAllLogsByTaskIdUseCase {
        return GetAllLogsByTaskIdUseCase(logRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteLogUseCase(
        logRepository: LogRepository,
    ): DeleteLogUseCase {
        return DeleteLogUseCase(logRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteAllLogsByTaskIdUseCase(
        logRepository: LogRepository,
    ): DeleteAllLogsByTaskIdUseCase {
        return DeleteAllLogsByTaskIdUseCase(logRepository)
    }

    @Provides
    @Singleton
    fun provideGetTasksByStatusUseCase(
        taskRepository: TaskRepository,
    ): GetTasksByStatusUseCase {
        return GetTasksByStatusUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetCompletedTasksUseCase(
        taskRepository: TaskRepository,
    ): GetCompletedTasksUseCase {
        return GetCompletedTasksUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetPendingTasksUseCase(
        taskRepository: TaskRepository,
    ): GetPendingTasksUseCase {
        return GetPendingTasksUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetOverdueTasksUseCase(
        taskRepository: TaskRepository,
    ): GetOverdueTasksUseCase {
        return GetOverdueTasksUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTasksByPriorityUseCase(
        taskRepository: TaskRepository,
    ): GetTasksByPriorityUseCase {
        return GetTasksByPriorityUseCase(taskRepository)
    }



    @Provides
    @Singleton
    fun provideGetTasksSortedByPriorityUseCase(
        taskRepository: TaskRepository,
    ): GetTasksSortedByPriorityUseCase {
        return GetTasksSortedByPriorityUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideGetTasksSortedByNameUseCase(
        taskRepository: TaskRepository,
    ): GetTasksSortedByNameUseCase {
        return GetTasksSortedByNameUseCase(taskRepository)
    }

}