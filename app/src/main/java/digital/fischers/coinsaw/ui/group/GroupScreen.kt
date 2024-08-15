package digital.fischers.coinsaw.ui.group

import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.ContentWrapperWithFallback
import digital.fischers.coinsaw.ui.components.CustomButton
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.viewModels.GroupViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * Group screen.
 * @param onBackNavigation: Navigation back to the previous screen.
 * @param onGroupSettingsClicked: Navigation to the group settings screen.
 * @param onSharesClicked: Navigation to the shares screen.
 * @param onMakeOnlineClicked: Navigation to the make online screen.
 * @param onGroupMembersClicked: Navigation to the group members screen.
 * @param onAddMemberClicked: Navigation to the add member screen. This is only available if the group has no members yet.
 * @param onBillClicked: Navigation to the bill details screen.
 * @param onAddBillClicked: Navigation to the add bill screen.
 * @param onAddTransactionClicked: Navigation to the add transaction screen.
 * @param onSettleUpClicked: Navigation to the settle up screen.
 * @param groupViewModel: Group view model.
 */
@Composable
fun GroupScreen(
    onBackNavigation: () -> Unit,
    onGroupSettingsClicked: (String) -> Unit,
    onSharesClicked: (String) -> Unit,
    onMakeOnlineClicked: (String) -> Unit,
    onGroupMembersClicked: (String) -> Unit,
    onAddMemberClicked: (String) -> Unit,
    onBillClicked: (groupId: String, billId: String) -> Unit,
    onAddBillClicked: (groupId: String) -> Unit,
    onAddTransactionClicked: (groupId: String, amount: String?, payer: String?, payee: String?) -> Unit,
    onTransactionClicked: (groupId: String, billId: String) -> Unit,
    onSettleUpClicked: (String) -> Unit,
    onNoSessionNavigation: () -> Unit,
    groupViewModel: GroupViewModel = hiltViewModel()
) {
    val group by groupViewModel.group.collectAsState()
    val members by groupViewModel.members.collectAsState()
    val bills by groupViewModel.bills.collectAsState()
    val calculatedTransactions by groupViewModel.calculatedTransactions.collectAsState()
    val groupId = groupViewModel.groupId

    val syncing = groupViewModel.syncing

    var menuExpanded by remember { mutableStateOf(false) }

    BaseScreen(
        refreshing = syncing,
        onRefresh = if(group.isOnline && group.hasSession) { { groupViewModel.syncGroup(group) } } else {
            null
        },
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_group_title),
                backNavigationText = stringResource(id = R.string.screen_title_groups),
                backNavigation = { onBackNavigation() },
                menu = {
                    CustomButton(
                        onClick = { menuExpanded = true },
                        icon = R.drawable.icon_dot_menu
                    )
                    DropdownMenu(
                        offset = DpOffset(0.dp, 5.dp),
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_edit),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            text = {
                                Text(text = stringResource(id = R.string.edit_group_menu_item))
                            },
                            onClick = {
                                onGroupSettingsClicked(groupId)
                                menuExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_users),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            text = {
                                Text(text = stringResource(id = R.string.screen_memberlist_title))
                            },
                            onClick = {
                                onGroupMembersClicked(groupId)
                                menuExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        if (!group.isOnline) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_upload),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                text = {
                                    Text(text = stringResource(id = R.string.screen_make_online_title))
                                },
                                onClick = {
                                    onMakeOnlineClicked(groupId)
                                    menuExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }

                        if (group.isOnline && group.isAdmin) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_share),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                text = {
                                    Text(text = stringResource(id = R.string.screen_share_title))
                                },
                                onClick = {
                                    onSharesClicked(groupId)
                                    menuExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (members.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        onAddBillClicked(groupId)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add),
                        contentDescription = stringResource(
                            id = R.string.add_bill
                        )
                    )
                }
            }
        },
        title = group.name
    ) {
        if (group.isOnline && !group.hasSession) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onNoSessionNavigation() }
                    .background(MaterialTheme.colorScheme.error)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.group_restart_initial_sync),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.group_restart_initial_sync_desc),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.icon_refresh),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        ContentWrapperWithFallback(
            members,
            showCondition = members.isNotEmpty(),
            fallback = {
                NoMembers(onAddMemberClicked = { onAddMemberClicked(groupId) })
            }) {
            ContentWrapperWithFallback(bills, showCondition = bills.isNotEmpty(), fallback = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { onAddBillClicked(groupId) }) {
                        Text(
                            text = stringResource(id = R.string.add_first_bill), style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
            }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    ContentWrapperWithFallback(calculatedTransactions, showCondition = calculatedTransactions.isNotEmpty(),
                        loading = {
                            Text(text = stringResource(id = R.string.loading))
                        },
                        fallback = { Text(
                        text = "🎉 " + stringResource(id = R.string.settled_up)
                    ) }) {
                    calculatedTransactions.forEach { transaction ->
                        val payer by transaction.payer.collectAsState()
                        if (payer.isMe) {
                            val payee by transaction.payee.collectAsState()
                            CalculatedTransactionElement(
                                currency = group.currency,
                                amount = transaction.amount,
                                payer = payer.name,
                                payee = payee.name,
                                payerIsMe = true,
                                onClick = {
                                    onAddTransactionClicked(
                                        groupId,
                                        transaction.id,
                                        payer.id,
                                        payee.id
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        calculatedTransactions.forEach { transaction ->
                            val payer by transaction.payer.collectAsState()
                            if (!payer.isMe) {
                                val payee by transaction.payee.collectAsState()
                                CalculatedTransactionElement(
                                    currency = group.currency,
                                    amount = transaction.amount,
                                    payer = payer.name,
                                    payee = payee.name,
                                    payerIsMe = false,
                                    onClick = {
                                        onAddTransactionClicked(
                                            groupId,
                                            transaction.id,
                                            payer.id,
                                            payee.id
                                        )
                                    }
                                )
                            }
                        }
                    }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(count = bills.size) { index ->
                    val bill = bills[index]
                    BaseTimeLineElement(
                        bill.created
                    ) {
                        if (bill.name.isBlank() && bill.splitting.size == 1) {
                            TransactionElement(
                                currency = group.currency,
                                payer = bill.payer,
                                amount = bill.amount,
                                payee = bill.splitting.first().user,
                                onClick = { onTransactionClicked(groupId, bill.id) }
                            )
                        } else {
                            BillElement(
                                currency = group.currency,
                                name = bill.name,
                                amount = bill.amount,
                                payer = bill.payer,
                                myShare = bill.myShare,
                                onClick = { onBillClicked(groupId, bill.id) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(42.dp))
                }
            }
            }
        }
    }
}

@Composable
fun CalculatedTransactionElement(
    currency: String,
    amount: Double,
    payer: String,
    payee: String,
    payerIsMe: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.small)
        .clickable { onClick() }
    ) {
        if (payerIsMe) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxWidth(),
            ) {
                BaseCalculatedTransactionElement(
                    currency = currency,
                    amount = amount,
                    payer = payer,
                    payee = payee,
                    highlighted = true,
                    iconBackground = MaterialTheme.colorScheme.secondary
                )
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .padding(3.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.settle_up_specifically),
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.icon_double_arrow_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            BaseCalculatedTransactionElement(
                currency = currency,
                amount = amount,
                payer = payer,
                payee = payee,
                highlighted = false,
                iconBackground = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun BaseCalculatedTransactionElement(
    currency: String,
    amount: Double,
    payer: String,
    payee: String,
    highlighted: Boolean,
    iconBackground: Color,
    modifier: Modifier = Modifier
) {
    val textColor = if (highlighted) Color.White else MaterialTheme.colorScheme.onBackground
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(iconBackground)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_money),
                    contentDescription = null,
                    tint = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = payer, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = textColor
                    )
                )
                Icon(
                    painter = painterResource(id = R.drawable.icon_double_arrow_right),
                    contentDescription = null,
                    tint = textColor
                )
                Text(
                    text = payee, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = textColor
                    )
                )
            }
        }
        Text(
            text = "%.2f".format(amount) + " $currency",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        )
    }
}

@Composable
fun BaseTimeLineElement(
    timestamp: Long,
    content: @Composable () -> Unit
) {
    val date = Date(timestamp)
    val verticalPadding = 6

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .width(24.dp)
                .padding(vertical = verticalPadding.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var month = DateFormat.format("MMM", date).toString().uppercase(Locale.getDefault())

            if (month.length > 4) {
                month = month.take(3) + "."
            }

            Text(
                text = month,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = DateFormat.format("dd", date).toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
            ) {
                Divider(
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = verticalPadding.dp)
        ) {
            content()
        }
    }
}

@Composable
fun BillElement(
    currency: String,
    name: String,
    amount: Double,
    payer: StateFlow<GroupScreenUiStates.User>,
    myShare: Double?,
    onClick: () -> Unit
) {
    val payerState by payer.collectAsState()

    val byline = if (myShare != null) {
        "%.2f".format(amount) + " $currency ${stringResource(id = R.string.by)} ${payerState.name}"
    } else {
        payerState.name
    }

    val displayedAmount = myShare ?: amount
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name, style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = byline,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        if(displayedAmount != 0.0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (myShare != null) {
                    Text(
                        text = if (displayedAmount > 0) stringResource(id = R.string.you_lent) else stringResource(
                            id = R.string.you_borrowed
                        ), style = TextStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                Text(
                    text = "%.2f".format(abs(displayedAmount)) + " " + currency,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = if (displayedAmount > 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionElement(
    currency: String,
    payer: StateFlow<GroupScreenUiStates.User>,
    amount: Double,
    payee: StateFlow<GroupScreenUiStates.User>,
    onClick: () -> Unit
) {
    Text(text = "TransactionElement yet to be implemented")
}

@Composable
fun NoMembers(onAddMemberClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .size(300.dp),
            painter = painterResource(
                id = R.drawable.sprintingdoodle
            ),
            contentDescription = stringResource(
                id = R.string.no_groups_yet
            )
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(0.6f),
            text = stringResource(id = R.string.group_no_members_error),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { onAddMemberClicked() }) {
            Text(
                text = stringResource(id = R.string.add_member), style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}