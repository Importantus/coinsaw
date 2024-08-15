package digital.fischers.coinsaw.ui.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    var showDeleteModal by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        blockingLoading = loading,
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_bill_details_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) },
                menu = {
                    Row() {
                        CustomButton(onClick = { showDeleteModal = true }, icon = R.drawable.icon_delete)
                    }
                }
            )
        },
    ) {
        if (bill != null) {
            Text(
                text = bill!!.name,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(text = getDate(bill!!.created) ?: "")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (bill!!.payer != null) {
                    val payer by bill!!.payer!!.collectAsState()
                    Text(text = payer.name)
                    Text(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        text = "%.2f".format(bill!!.amount) + " " + group.currency
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            for (splitting in bill!!.splitting) {
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
                        text = "%.2f".format(splitting.percentage * bill!!.amount) + " " + group.currency
                    )
                }
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