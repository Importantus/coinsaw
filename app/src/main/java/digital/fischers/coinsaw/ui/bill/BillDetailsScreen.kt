package digital.fischers.coinsaw.ui.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomButton
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.DeleteModal
import digital.fischers.coinsaw.ui.components.MoneyInput
import digital.fischers.coinsaw.ui.group.GroupScreenUiStates
import digital.fischers.coinsaw.ui.theme.neutral
import digital.fischers.coinsaw.ui.theme.successText
import digital.fischers.coinsaw.ui.utils.getDate
import digital.fischers.coinsaw.ui.viewModels.BillDetailsViewModel
import kotlinx.coroutines.launch

@Composable
fun BillDetailsScreen(
    onBackNavigation: (String) -> Unit,
    onEditNavigation: (groupId: String, billId: String) -> Unit,
    viewModel: BillDetailsViewModel = hiltViewModel()
) {
    val groupId = viewModel.groupId
    val billId = viewModel.billId
    val loading = viewModel.loading
    val bill by viewModel.bill.collectAsState()
    val group by viewModel.group.collectAsState()

    val isTransaction = (bill?.splitting?.size ?: 0) == 1 && bill?.name?.isBlank() == true

    var showDeleteModal by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        blockingLoading = loading,
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = if (isTransaction) R.string.screen_bill_details_title else R.string.transaction),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) },
                menu = {
                    Row() {
                        CustomButton(
                            onClick = { showDeleteModal = true },
                            icon = R.drawable.icon_delete
                        )
                    }
                }
            )
        },
    ) {
        if (bill != null) {

            if (!isTransaction) {
                BillDetails(bill!!, group.currency)
            } else {
                TransactionDetails(bill!!, group.currency)
            }

            if (showDeleteModal) {
                DeleteModal(
                    title = stringResource(id = R.string.delete_expense),
                    description = stringResource(id = R.string.delete_expense_desc),
                    onConfirm = {
                        coroutineScope.launch {
                            viewModel.deleteBill()
                        }.invokeOnCompletion {
                            showDeleteModal = false
                            onBackNavigation(groupId)
                        }
                    },
                    onCancel = {
                        showDeleteModal = false
                    }
                )
            }
        }
    }
}

@Composable
fun BillDetails(
    bill: GroupScreenUiStates.Bill,
    currency: String
) {
    Text(
        text = bill.name,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.neutral
        )
    )

    Text(text = getDate(bill.created) ?: "")

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (bill.payer != null) {
            val payer by bill.payer.collectAsState()
            Text(text = payer.name)
            Text(
                color = MaterialTheme.colorScheme.successText,
                text = "%.2f".format(bill.amount) + " " + currency
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    for (splitting in bill.splitting) {
        val user by splitting.user.collectAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = user.name)
            Text(text = Math.round(splitting.percentage * 100).toString() + "%")
            Text(
                color = MaterialTheme.colorScheme.error,
                text = "%.2f".format(splitting.percentage * bill.amount) + " " + currency
            )
        }
    }
}

@Composable
fun TransactionDetails(
    bill: GroupScreenUiStates.Bill,
    currency: String
) {
    val payer by bill.splitting[0].user.collectAsState()
    val payee by bill.payer!!.collectAsState()

    Text(
        text = stringResource(id = R.string.transaction),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.neutral
        )
    )

    Text(text = getDate(bill.created) ?: "")

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = payee.name,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.neutral
            )
        )

        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.long_arrow),
                contentDescription = null,
                Modifier
                    .size(130.dp)
                    .rotate(-90f),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "%.2f".format(bill.amount) + " " + currency,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        Text(
            text = payer.name,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.neutral
            )
        )
    }
    }
}