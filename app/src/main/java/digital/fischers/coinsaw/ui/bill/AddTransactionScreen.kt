package digital.fischers.coinsaw.ui.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButton
import digital.fischers.coinsaw.ui.components.CustomFloatingActionButtonType
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.ErrorBox
import digital.fischers.coinsaw.ui.components.MoneyInput
import digital.fischers.coinsaw.ui.components.UserSelection
import digital.fischers.coinsaw.ui.viewModels.AddTransactionViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@Composable
fun AddTransactionScreen(
    onNavigateBack: (groupId: String) -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val loading = viewModel.loading
    val error = viewModel.error
    val users by viewModel.users.collectAsState()
    val newTransactionSate by viewModel.newTransactionState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(appBar = {
        CustomNavigationBar(
            title = stringResource(id = R.string.screen_add_transaction_title),
            backNavigationText = null,
            backNavigation = {
                onNavigateBack(groupId)
            })
    },
        blockingLoading = loading,
        floatingActionButton = {
            CustomFloatingActionButton(onClick = {
                coroutineScope.launch {
                    val success = viewModel.createTransaction()
                    if (!error && success) {
                        onNavigateBack(groupId)
                    }
                }
            }, type = CustomFloatingActionButtonType.CONFIRM)
        }
    ) {
        if (error) {
            ErrorBox(
                message = stringResource(id = R.string.error_transaction_create),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            UserSelection(
                onSelectionChanged = {
                    viewModel.setPayeeId(it)
                },
                users = users,
                selectedUserName = viewModel.getPayer()?.name ?: ""

            )

            Box(modifier = Modifier.fillMaxWidth(0.45f), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.long_arrow),
                    contentDescription = null,
                    Modifier.size(200.dp),
                    tint = MaterialTheme.colorScheme.surface
                )
                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                ) {
                    MoneyInput(
                        onValueChanged = viewModel::setAmount,
                        value = newTransactionSate.amount
                    )
                }
            }

            UserSelection(
                onSelectionChanged = {
                    viewModel.setPayerId(it)
                },
                users = users,
                selectedUserName = viewModel.getPayee()?.name ?: ""
            )
        }
    }
}