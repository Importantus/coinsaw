package digital.fischers.coinsaw.ui.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.remote.Session
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomErrorAlert
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.DeleteModal
import digital.fischers.coinsaw.ui.utils.getDate
import digital.fischers.coinsaw.ui.viewModels.SessionsViewModel
import digital.fischers.coinsaw.ui.viewModels.ShareViewModel
import kotlinx.coroutines.launch

@Composable
fun SessionsScreen(
    onBackNavigation: () -> Unit,
    viewModel: SessionsViewModel = hiltViewModel()
) {
    val loading = viewModel.loading

    val loadError = viewModel.loadError
    val deleteError = viewModel.deleteError

    val lifecycleOwner = LocalLifecycleOwner.current

    val sessions by viewModel.sessions.collectAsState()

    val sessionId by viewModel.currentSessionId.collectAsState()

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.load()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_sessions_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation() })
        },
        blockingLoading = loading,
        title = stringResource(id = R.string.screen_sessions_title)
    ) {
        if (loadError != null) {
            CustomErrorAlert(error = loadError, onConfirm = onBackNavigation)
        }

        if (deleteError != null) {
            CustomErrorAlert(error = deleteError, onConfirm = onBackNavigation)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sessions.size) { index ->
                val session = sessions[index]
                SessionItem(
                    onDeleteClicked = { id ->
                        coroutineScope.launch {
                            viewModel.deleteSession(id)
                        }.invokeOnCompletion { onBackNavigation() }
                    },
                    session = session,
                    isOwnSession = sessionId == session.id
                )
            }
        }
    }
}

@Composable
fun SessionItem(
    onDeleteClicked: (id: String) -> Unit,
    isOwnSession: Boolean,
    session: Session
) {
    var showDeleteModal by remember {
        mutableStateOf(false)
    }

    if (showDeleteModal) {
        DeleteModal(
            title = stringResource(id = R.string.delete_session),
            description = stringResource(id = R.string.delete_session_desc),
            onConfirm = {
                onDeleteClicked(session.id)
            },
            onCancel = {
                showDeleteModal = false
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = session.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (session.admin) {
                    Text(
                        text = stringResource(id = R.string.share_admin),
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
            Text(
                text = stringResource(id = R.string.last_active) + ": " + getDate(session.last_active_timestamp.toLong()),
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }
        if (!isOwnSession) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.error)
                    .clickable { showDeleteModal = true }
                    .padding(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}