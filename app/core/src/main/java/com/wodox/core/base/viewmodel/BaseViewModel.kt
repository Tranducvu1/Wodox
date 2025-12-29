package com.wodox.core.base.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

open class BaseViewModel(
    val app: Application
) : AndroidViewModel(app) {

    // ======================== Lifecycle ========================
    /**
     * Gọi khi ViewModel được tạo
     * Override để thực hiện initialization
     */
    open fun onCreate() {
        // Override in subclass
    }

    /**
     * Gọi khi ViewModel bị destroy
     * Override để cleanup resources
     */
    open fun onDestroy() {
        // Override in subclass
    }

    override fun onCleared() {
        onDestroy()
        super.onCleared()
    }

    fun applicationContext(): Context = app.applicationContext

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)
    val successMessage = MutableLiveData<String?>(null)


    fun showLoading() {
        isLoading.postValue(true)
    }

    fun hideLoading() {
        isLoading.postValue(false)
    }


    fun showError(message: String) {
        errorMessage.postValue(message)
        hideLoading()
    }


    fun showSuccess(message: String) {
        successMessage.postValue(message)
    }


    fun clearMessages() {
        errorMessage.postValue(null)
        successMessage.postValue(null)
    }

    private val jobs = mutableSetOf<Job>()

    fun launchIO(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                handleException(e)
            }
        }.also { jobs.add(it) }
    }

    /**
     * Launch coroutine trên Main thread
     */
    protected fun launchMain(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            try {
                block()
            } catch (e: Exception) {
                handleException(e)
            }
        }.also { jobs.add(it) }
    }

    /**
     * Launch coroutine với custom dispatcher
     */
    protected fun launch(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Unit
    ): Job {
        return viewModelScope.launch(context) {
            try {
                block()
            } catch (e: Exception) {
                handleException(e)
            }
        }.also { jobs.add(it) }
    }

    /**
     * Cancel tất cả jobs
     */
    protected fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    // ======================== Exception Handling ========================
    /**
     * Handle exception - override để custom behavior
     */
    protected open fun handleException(exception: Exception) {
        exception.printStackTrace()
        showError(exception.message ?: "Có lỗi xảy ra")
    }

    // ======================== Utility Methods ========================
    /**
     * Check nếu string không null và không empty
     */
    protected fun String?.isValidText(): Boolean = !this.isNullOrBlank()

    /**
     * Check nếu list không null và không empty
     */
    protected fun <T> List<T>?.isValidList(): Boolean = !this.isNullOrEmpty()

    /**
     * Safe action với try-catch
     */
    protected fun <T> tryAction(block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            handleException(e)
            null
        }
    }

    /**
     * Safe action với callback
     */
    protected fun trySafe(
        block: () -> Unit,
        onError: (Exception) -> Unit = { handleException(it) }
    ) {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}

// ======================== Extension Functions ========================

/**
 * Launch task với loading state
 */
inline fun <reified T : BaseViewModel> T.launchWithLoading(
    crossinline block: suspend () -> Unit
) {
    this.launchIO {
        showLoading()
        try {
            block()
        } finally {
            hideLoading()
        }
    }
}

/**
 * Launch task với error handling
 */
inline fun <reified T : BaseViewModel> T.launchSafely(
    crossinline block: suspend () -> Unit,
    noinline onError: (Exception) -> Unit = { this.showError(it.message ?: "") }
) {
    this.launchIO {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}