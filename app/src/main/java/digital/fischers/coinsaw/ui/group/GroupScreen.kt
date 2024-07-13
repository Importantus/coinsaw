package digital.fischers.coinsaw.ui.group

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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

/**
 * Group screen.
 * @param onBackNavigation: Navigation back to the previous screen.
 * @param onGroupSettingsClicked: Navigation to the group settings screen.
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
    onGroupMembersClicked: (String) -> Unit,
    onAddMemberClicked: (String) -> Unit,
    onBillClicked: (String) -> Unit,
    onAddBillClicked: (String) -> Unit,
    onAddTransactionClicked: (String) -> Unit,
    onSettleUpClicked: (String) -> Unit,
    groupViewModel: GroupViewModel = hiltViewModel()
) {
    val group by groupViewModel.group.collectAsState()
    val members by groupViewModel.members.collectAsState()
    val bills by groupViewModel.bills.collectAsState()
    val calculatedTransactions by groupViewModel.calculatedTransactions.collectAsState()
    val groupId = groupViewModel.groupId

    var menuExpanded by remember { mutableStateOf(false) }

    BaseScreen(
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
        }
    ) {
        // Title
        Text(
            text = group.name,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        ContentWrapperWithFallback(
            members,
            showCondition = members.isNotEmpty(),
            fallback = {
                NoMembers(onAddMemberClicked = { onAddMemberClicked(groupId) })
            }) {
            Column {
                calculatedTransactions.forEach { transaction ->
                    val payer by transaction.payer.collectAsState()
                    val payee by transaction.payee.collectAsState()
                    CalculatedTransactionElement(
                        currency = group.currency,
                        amount = transaction.amount,
                        payer = payer.name,
                        payee = payee.name,
                        payerIsMe = payer.isMe,
                        onClick = { onAddTransactionClicked(groupId) }
                    )
                }

                members.forEach { member ->
                    Text(
                        text = member.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                bills.forEach { bill ->
                    val payer by bill.payer.collectAsState()
                    Text(
                        text = "Bill: ${bill.name} payed by ${payer.name}",
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
        .clickable { onClick() }
    ) {
        if(payerIsMe) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
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
                    tint = if(highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
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
            text = "$amount $currency",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        )
    }
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