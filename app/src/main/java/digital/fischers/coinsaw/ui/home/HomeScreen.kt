package digital.fischers.coinsaw.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.viewModels.HomeViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    onGroupAddClicked: () -> Unit,
    onGroupJoinClicked: () -> Unit,
    onGroupClicked: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val groups by viewModel.groups.collectAsState()

    BaseScreen(appBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.screen_title_groups),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Icon(
                painterResource(id = R.drawable.icon_settings), contentDescription = stringResource(
                    id = R.string.screen_settings_title
                )
            )
        }
    }) {
        Column {
            if (groups.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groups.forEach { group ->
                        GroupCard(
                            groupUiState = group,
                            onClick = { onGroupClicked(group.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ActionButton(
                    left = false,
                    icon = R.drawable.icon_join,
                    text = stringResource(id = R.string.join_group),
                    onClick = onGroupJoinClicked,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )
                ActionButton(
                    left = true,
                    icon = R.drawable.icon_add,
                    text = stringResource(id = R.string.add_group),
                    onClick = onGroupAddClicked,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                )
            }

            if (groups.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .size(300.dp),
                        painter = painterResource(
                            id = R.drawable.unboxingdoodle
                        ),
                        contentDescription = stringResource(
                            id = R.string.no_groups_yet
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.no_groups_yet),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    left: Boolean,
    icon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 16
) {
    Row(
        modifier = modifier
            .clip(
                if (left) RoundedCornerShape(
                    topEnd = cornerRadius.dp,
                    bottomEnd = cornerRadius.dp
                ) else RoundedCornerShape(topStart = cornerRadius.dp, bottomStart = cornerRadius.dp)
            )

            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            modifier = Modifier.padding(end = 8.dp),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun GroupCard(
    groupUiState: HomeGroupUiState,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick(groupUiState.id) }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = groupUiState.name,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${groupUiState.members} ${
                        if (groupUiState.members == 1) stringResource(
                            id = R.string.member_singular
                        ) else stringResource(id = R.string.member_plural)
                    } | ${
                        if (groupUiState.online) stringResource(
                            id = R.string.online
                        ) else stringResource(
                            id = R.string.offline
                        )
                    }",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        when {
                            groupUiState.balance > 0.0 -> MaterialTheme.colorScheme.secondaryContainer
                            groupUiState.balance < 0.0 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "${
                        String.format(
                            Locale.getDefault(),
                            "%.2f",
                            groupUiState.balance
                        )
                    } ${groupUiState.currency}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}