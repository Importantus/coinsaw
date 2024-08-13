package digital.fischers.coinsaw.ui.remote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.TokenDisplay
import digital.fischers.coinsaw.ui.viewModels.ShowRecoveryTokenViewModel

@Composable
fun ShowRecoveryTokenScreen(
    onBackNavigation: () -> Unit,
    onForwardNavigation: (String, String) -> Unit,
    viewModel: ShowRecoveryTokenViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val recoveryToken = viewModel.recoveryToken

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.recovery_token),
                backNavigationText = null,
                backNavigation = { onBackNavigation() }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    onForwardNavigation(groupId, recoveryToken)
                },
                type = CustomFloatingActionButtonType.CONFIRM
            )
        },
        title = stringResource(id = R.string.recovery_token)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.recovery_token_description),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TokenDisplay(token = recoveryToken)
        }
    }
}