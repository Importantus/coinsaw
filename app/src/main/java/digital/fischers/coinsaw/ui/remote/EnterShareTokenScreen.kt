package digital.fischers.coinsaw.ui.remote

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.CustomTextField
import digital.fischers.coinsaw.ui.viewModels.EnterShareTokenViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

@Composable
fun EnterShareTokenScreen(
    onBackNavigation: () -> Unit,
    onForwardNavigation: (String) -> Unit,
    viewModel: EnterShareTokenViewModel = hiltViewModel()
) {
    val shareToken by viewModel.shareToken.collectAsState()
    val wrongShareTokenError = viewModel.shareTokenDefault.isNotBlank()
    val groupMissingSessionError = viewModel.missingSessionError

    val title = if (groupMissingSessionError) {
        stringResource(id = R.string.screen_missing_token_title)
    } else {
        stringResource(id = R.string.join_group)
    }

    val desc = if (groupMissingSessionError) {
        stringResource(id = R.string.enter_missing_token_desc)
    } else {
        stringResource(id = R.string.enter_share_token_desc)
    }

    BaseScreen(
        title = if (groupMissingSessionError) {
            stringResource(id = R.string.screen_missing_token_title)
        } else {
            stringResource(id = R.string.screen_join_group_big_title)
        },
        appBar = {
            CustomNavigationBar(
                title = title,
                backNavigationText = null,
                backNavigation = { onBackNavigation() })
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    onForwardNavigation(shareToken)
                }, type = CustomFloatingActionButtonType.NEXT
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
                when (result) {
                    is QRResult.QRError -> {

                    }

                    QRResult.QRMissingPermission -> {

                    }

                    is QRResult.QRSuccess -> {
                        result.content.rawValue?.let { onForwardNavigation(it) }
                    }

                    QRResult.QRUserCanceled -> {

                    }
                }
            }

            Text(text = desc)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = shareToken,
                onValueChange = { viewModel.onShareTokenChanged(it) },
                label = stringResource(id = R.string.share_token),
                isError = wrongShareTokenError,
                placeholder = stringResource(id = R.string.paste_key_here)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    "OR",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { scanQRCodeLauncher.launch(null) }
                .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scan QR Code",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}