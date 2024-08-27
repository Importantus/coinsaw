package digital.fischers.coinsaw.ui.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.viewModels.MemberlistViewModel

@Composable
fun MemberlistScreen(
    onBackNavigation: (String) -> Unit,
    onMemberAddClicked: (String) -> Unit,
    onMemberClicked: (String, String) -> Unit,
    onForwardNavigation: ((String) -> Unit)? = null,
    isInGroupCreationFlow: Boolean = false,
    viewModel: MemberlistViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val memberList by viewModel.users.collectAsState()

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_memberlist_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) }
            )
        },
        floatingActionButton = {
            if (isInGroupCreationFlow && onForwardNavigation != null) {
                CustomFloatingActionButton(
                    onClick = { onForwardNavigation(groupId) },
                    type = CustomFloatingActionButtonType.CONFIRM
                )
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(count = memberList.size) { i ->
                val member = memberList[i]
                MemberListItem(
                    member = member,
                    onMemberClicked = { onMemberClicked(groupId, member.id) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable { onMemberAddClicked(groupId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.add_member),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun MemberListItem(member: User, onMemberClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onMemberClicked() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = member.name)
            if (member.isMe) {
                Text(
                    text = stringResource(id = R.string.you),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(4.dp, 2.dp)
                )
            }
        }
        Icon(painter = painterResource(id = R.drawable.icon_arrow_right), contentDescription = null)
    }
}
