package fr.sercurio.soulseek.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import fr.sercurio.soulseek.rooms.RoomsViewModel
import kotlinx.coroutines.launch

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    roomsViewModel: RoomsViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var mExpanded by remember { mutableStateOf(false) }
    val roomsName = roomsViewModel.roomsListState?.rooms?.map { it.name } ?: emptyList()
    var mSelectedText by remember { mutableStateOf("") }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    OutlinedTextField(
        value = mSelectedText,
        onValueChange = { mSelectedText = it },
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                mTextFieldSize = coordinates.size.toSize()
            },
        label = { Text("Rooms List") },
        trailingIcon = {
            Icon(icon, "contentDescription",
                modifier.clickable { mExpanded = !mExpanded })
        }
    )

    DropdownMenu(
        expanded = mExpanded,
        onDismissRequest = { mExpanded = false },
        modifier = modifier
            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
    ) {
        roomsName.forEach { roomName ->
            DropdownMenuItem(
                text = { Text(text = roomName) },
                onClick = {
                    mSelectedText = roomName
                    mExpanded = false
                    coroutineScope.launch {
                        roomsViewModel.joinRoom(roomName)
                    }
                    roomsViewModel.currentRoomState.value = roomName
                })
        }
    }
}