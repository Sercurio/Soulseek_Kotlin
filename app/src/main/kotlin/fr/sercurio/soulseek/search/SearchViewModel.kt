package fr.sercurio.soulseek.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.sercurio.soulseek.SoulseekApi
import fr.sercurio.soulseek.client.peer.messages.SearchReplyMessage
import fr.sercurio.soulseek.entities.SoulFile
import kotlinx.coroutines.launch

class SearchViewModel(private val soulseekApi: SoulseekApi) :
    ViewModel() {
    var searchRepliesState = mutableStateListOf<SearchReplyMessage?>()

    init {
        soulseekApi.onReceiveSearchReply {
            searchRepliesState.add(it)
        }
    }

    fun search(searchRequest: String) {
        viewModelScope.launch {
            soulseekApi.fileSearch(searchRequest)
        }
    }

    fun queueUpload(username: String, soulFile: SoulFile) {
        viewModelScope.launch {
            soulseekApi.queueUpload(username, soulFile)
        }
    }


}