//package com.wodox.domain.home.usecase
//
//import com.wodox.domain.base.BaseParamsUnsafeUseCase
//import com.wodox.domain.home.model.local.Task
//import com.wodox.domain.home.repository.TaskRepository
//import java.time.LocalDate
//
//class GetTaskByDateUseCase(
//    private val repository: TaskRepository,
//) : BaseParamsUnsafeUseCase<LocalDate, Task?>() {
//    override suspend fun execute(params: LocalDate): Task? {
//        return repository.getTaskByDate(params)
//    }
//}