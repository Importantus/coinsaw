package digital.fischers.coinsaw.ui.remote

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.remote.Share
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomErrorAlert
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.components.SettingCard
import digital.fischers.coinsaw.ui.theme.neutral
import digital.fischers.coinsaw.ui.viewModels.ShareViewModel
import kotlinx.coroutines.launch

@Composable
fun ShareScreen(
    onBackNavigation: () -> Unit,
    navigateToShareDetails: (String, String) -> Unit,
    viewModel: ShareViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val loading = viewModel.loading
    val loadError = viewModel.loadSharesError
    val createError = viewModel.createShareError

    val newShareName by viewModel.newShareName.collectAsState()
    val newShareAdmin by viewModel.newShareAdmin.collectAsState()
    val newShareMaxSessions by viewModel.newShareMaxSessions.collectAsState()

    val existingShares by viewModel.existingShares.collectAsState()


    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_share_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation() })
        },
        blockingLoading = loading
    ) {
        if(loadError != null) {
            CustomErrorAlert(error = loadError, onConfirm = onBackNavigation)
        }

        if(createError != null) {
            CustomErrorAlert(error = createError, onConfirm = {})
        }

        Column {
            ShareForm(
                newShareName = newShareName,
                newShareAdmin = newShareAdmin,
                newShareMaxSessions = newShareMaxSessions,
                onNameChanged = viewModel::onNameChanged,
                onAdminChanged = viewModel::onAdminChanged,
                onMaxSessionsChanged = viewModel::onMaxSessionsChanged,
                onCreateShare = {
                    val share = viewModel.createShare()
                    if (share != null)
                        navigateToShareDetails(groupId, share.id)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            ShareList(
                groupId = groupId,
                existingShares = existingShares,
                navigateToShareDetails = navigateToShareDetails
            )
        }
    }
}

@Composable
fun ShareForm(
    newShareName: String,
    newShareAdmin: Boolean,
    newShareMaxSessions: String,
    onNameChanged: (String) -> Unit,
    onAdminChanged: (Boolean) -> Unit,
    onMaxSessionsChanged: (String) -> Unit,
    onCreateShare: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.create_share_title),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.neutral
            )
        )
        CustomTextField(
            value = newShareName,
            onValueChange = { onNameChanged(it) },
            placeholder = stringResource(id = R.string.share_name_placeholder),
            label = stringResource(id = R.string.share_name)
        )
        SettingCard(
            title = stringResource(id = R.string.share_admin_access),
            description = stringResource(id = R.string.share_admin_access_desc),
            setting = {
                Switch(
                    checked = newShareAdmin, onCheckedChange = { onAdminChanged(it) },
                    colors = SwitchDefaults.colors(
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surface,
                        uncheckedBorderColor = MaterialTheme.colorScheme.background,
                        checkedBorderColor = MaterialTheme.colorScheme.background,
                        checkedThumbColor = MaterialTheme.colorScheme.surface,
                    )
                )
            })
        SettingCard(
            title = stringResource(id = R.string.share_max_sessions),
            description = stringResource(id = R.string.share_max_sessions_desc),
            setting = {
                BasicTextField(
                    value = newShareMaxSessions,
                    onValueChange = { onMaxSessionsChanged(it) },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)
                        .width(40.dp)
                )
            })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
                .clickable {
                    coroutineScope.launch {
                        onCreateShare()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.create_share_title),
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
fun ShareList(
    groupId: String,
    existingShares: List<Share>,
    navigateToShareDetails: (String, String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.existing_shares_title),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.neutral
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        existingShares.forEach { share ->
            ShareListItem(
                share = share,
                onShareClicked = { navigateToShareDetails(groupId, share.id) }
            )
        }
    }
}

@Composable
fun ShareListItem(share: Share, onShareClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onShareClicked() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = share.name)
            if (share.admin) {
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
        Icon(painter = painterResource(id = R.drawable.icon_arrow_right), contentDescription = null)
    }
}