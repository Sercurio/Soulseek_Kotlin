package fr.sercurio.soulseek.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.sercurio.soulseek.SoulseekApi
import fr.sercurio.soulseek.components.Dropdown
import kotlinx.coroutines.launch


@Composable
fun RoomsScreen(soulseekApi: SoulseekApi) {
    val roomsViewModel: RoomsViewModel = viewModel {
        RoomsViewModel(soulseekApi)
    }

    var message by rememberSaveable { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        Dropdown(modifier = Modifier.padding(20.dp), roomsViewModel)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            items(roomsViewModel.roomsMessagesState) {
                Text("${it?.username}: ${it?.message}")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(5 / 6f),
                singleLine = true,
                value = message,
                onValueChange = { message = it },
                label = { Text("Type something") },
                shape = RoundedCornerShape(percent = 20),
            )
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1 / 6f),
                onClick = {
                    coroutineScope.launch {
                        roomsViewModel.currentRoomState.value?.let {
                            roomsViewModel.sayInRoom(
                                it,
                                message
                            )
                        }
                        message = ""
                        keyboardController?.hide()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "",
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview
@Composable
fun RoomsScreenPreview() {
    RoomsScreen(SoulseekApi())
}