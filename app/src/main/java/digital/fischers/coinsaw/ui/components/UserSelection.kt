package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.database.User

@Composable
fun UserSelection(
    onSelectionChanged: (String) -> Unit,
    users: List<User>,
    selectedUserName: String,
    modifier: Modifier = Modifier
) {
    var userSelectPopUpVisible by remember {
        mutableStateOf(false)
    }

    Box {
        Row(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { userSelectPopUpVisible = true }
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = selectedUserName,
                modifier = Modifier
                    .weight(1f, fill = false)
            )
            Icon(
                painter = painterResource(id = R.drawable.icon_arrow_down),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        UserSelectPopup(
            visible = userSelectPopUpVisible,
            onDismissRequest = { userSelectPopUpVisible = false },
            users = users,
            onUserSelected = {
                onSelectionChanged(it.id)
            })
    }
}

@Composable
fun UserSelectPopup(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    users: List<User>,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Popup(
            onDismissRequest = onDismissRequest,
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, 80),
        ) {
            Column(
                modifier = modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
                    .heightIn(0.dp, 100.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                users.forEach {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .clickable {
                                onUserSelected(it)
                                onDismissRequest()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}