package com.wodox.main.ui.main

sealed class MainUiEvent {
    data class SearchEvent(val query: String)
}