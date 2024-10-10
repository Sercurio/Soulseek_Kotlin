package fr.sercurio.soulseek.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.sercurio.soulseek.SoulseekApi
import java.io.File
import java.io.FileOutputStream

@Composable
fun SearchScreen(soulseekApi: SoulseekApi) {
    var searchRequest by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val searchViewModel: SearchViewModel = viewModel {
        SearchViewModel(soulseekApi)
    }

    soulseekApi.onDownloadComplete { message ->
        val dir = File(
            context.getExternalFilesDir(null),
            "downloads/${message.username}",
        )

        dir.mkdirs()
        val file = File(dir, message.filepath)

        FileOutputStream(file).use { output ->
            output.write(message.file)
        }
    }

    Column {
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            value = searchRequest,
            onValueChange = { searchRequest = it },
            label = { Text("Search request") },
            shape = RoundedCornerShape(percent = 20),
            trailingIcon = {
                IconButton(onClick = { searchViewModel.search(searchRequest) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "search_request"
                    )
                }
            })

        ExpandableList(
            modifier = Modifier.padding(top = 10.dp),
            downloadCallback = { username, soulfile ->
                searchViewModel.queueUpload(
                    username,
                    soulfile
                )
            },
            sections = searchViewModel.searchRepliesState.map { searchReplyMessage ->
                SectionData(
                    searchReplyMessage?.username ?: "",
                    searchReplyMessage?.soulFiles
                        ?: emptyList()
                )
            }
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(SoulseekApi())
}