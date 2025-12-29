package com.wodox.core.base.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class TMVVMViewModel(
    application: Application
) : BaseUiStateViewModel<Any, Any, Any>(application) {

    open val app: Application = application

    override fun initialState(): Any = Any()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage



    override fun onCreate() {
    }


    open fun onReceiveData(data: Bundle?) {
    }


    protected fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }


    protected fun setError(error: String?) {
        _error.postValue(error)
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.postValue(null)
    }

    /**
     * Set success message
     */
    protected fun setSuccessMessage(message: String?) {
        _successMessage.postValue(message)
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage.postValue(null)
    }

    /**
     * Handle exception và show error
     */
    protected fun handleError(throwable: Throwable) {
        setLoading(false)
        setError(throwable.message ?: "An unknown error occurred")
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup resources nếu cần
    }
}
