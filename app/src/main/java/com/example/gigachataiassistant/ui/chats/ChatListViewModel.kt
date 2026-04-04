package com.example.gigachataiassistant.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gigachataiassistant.data.chat.ChatRepository
import com.example.gigachataiassistant.data.local.ChatEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ChatListViewModel(
    private val userId: String,
    private val repository: ChatRepository,
) : ViewModel() {

    private val searchInputInternal = MutableStateFlow("")
    val searchInput: StateFlow<String> = searchInputInternal.asStateFlow()

    private val appliedQueryInternal = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchInputInternal
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { text ->
                    appliedQueryInternal.value = text.trim()
                }
        }
    }

    fun onSearchInputChange(text: String) {
        searchInputInternal.value = text
    }

    fun applySearch() {
        appliedQueryInternal.value = searchInputInternal.value.trim()
    }

    val chats: Flow<PagingData<ChatEntity>> = appliedQueryInternal
        .flatMapLatest { query -> repository.observeChats(userId, query) }
        .cachedIn(viewModelScope)

    suspend fun createNewChat(title: String = "Новый чат"): String =
        repository.createChat(userId, title)

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}
