package com.wodox.core.base.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

abstract class BaseUiStateViewModel<S, E, A>(
    application: Application
) : AndroidViewModel(application) {

    var context: WeakReference<Context>? = null

    private var _data: Bundle? = null
    var data: Bundle?
        get() = _data
        set(value) {
            _data = value
            if (value != null && !onCreateCalled) {
                callOnCreate()
            }
        }

    private var onCreateCalled = false

    private val _uiState by lazy { MutableStateFlow(initialState()) }
    val uiState: StateFlow<S> by lazy { _uiState.asStateFlow() }

    private val _uiEvent = Channel<E>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        uiState
    }

    private fun callOnCreate() {
        if (!onCreateCalled) {
            onCreateCalled = true
            onCreate()
        }
    }

    abstract fun initialState(): S

    open fun handleAction(action: A) {

    }

    fun dispatch(action: A) {
        handleAction(action)
    }

    protected fun updateState(update: (S) -> S) {
        _uiState.value = update(_uiState.value)
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    protected val currentState: S
        get() = _uiState.value

    open fun onCreate() {

    }

    fun onViewCreatedReady() {
        if (!onCreateCalled) {
            callOnCreate()
        }
    }

    protected fun applicationContext(): Context {
        return getApplication<Application>().applicationContext
    }

    open fun onDestroy() {

    }
}