package digital.fischers.coinsaw.ui.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.data.database.User
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.MoneyInput
import digital.fischers.coinsaw.ui.components.UserSelection
import digital.fischers.coinsaw.ui.components.getBottomLineShape
import digital.fischers.coinsaw.ui.utils.CreateUiStates
import digital.fischers.coinsaw.ui.viewModels.AddBillViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddBillScreen(
    onBackNavigation: (String) -> Unit,
    onForwardNavigation: (String) -> Unit,
    viewModel: AddBillViewModel = hiltViewModel()
) {
    val horizontalPadding = 16
    val groupId = viewModel.groupId
    val group by viewModel.group.collectAsState()

    val users by viewModel.users.collectAsState()
    val valid = viewModel.valid

    val state by viewModel.newBillState.collectAsState()

    val remainingPercentage = viewModel.percentRemaining

    val loading = viewModel.loading

    val coroutineScope = rememberCoroutineScope()

    BaseScreen(
        contentPaddingEnabled = false,
        horizontalPadding = 16,
        blockingLoading = loading,
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_add_expense_title),
                backNavigationText = null,
                backNavigation = { onBackNavigation(groupId) })
        },
        topColor = MaterialTheme.colorScheme.secondary
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = horizontalPadding.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(230.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        UserSelection(
                            onSelectionChanged = {
                                viewModel.onPayerChanged(it)
                            },
                            users = users,
                            selectedUserName = viewModel.getUserById(state.payerId)?.name ?: ""
                        )
                        Text(
                            text = stringResource(id = R.string.add_expense_user_payed) + "...",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    MoneyInput(onValueChanged = {
                        viewModel.onAmountChanged(it)
                    }, value = state.amount, currency = group?.currency ?: "€")
                    TitleInput(onValueChanged = {
                        viewModel.onNameChanged(it)
                    }, value = state.name)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = horizontalPadding.dp, vertical = 24.dp)
            ) {
                SplittingSection(
                    users = users,
                    splittings = state.splitting,
                    onSplittingChanged = { userId, percentage ->
                        viewModel.onSplittingChanged(userId, percentage)
                    },
                    percentRemaining = remainingPercentage
                )
            }
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    )
                    .fillMaxWidth()
                    .fillMaxHeight(0.15f)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    enabled = valid,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createBill()
                            onForwardNavigation(groupId)
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 34.dp, vertical = 4.dp),
                        text = stringResource(id = R.string.add_expense_save),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

    }
}

@Composable
fun TitleInput(
    onValueChanged: (String) -> Unit,
    value: String
) {
    Box {
        BasicTextField(
            value = value,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 24.sp
            ),
            onValueChange = { onValueChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = getBottomLineShape(2.dp)
                )
                .padding(8.dp)
        )
        if (value.isEmpty()) {
            Text(
                text = stringResource(id = R.string.add_expense_title_placeholder),
                modifier = Modifier.padding(8.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Light,
                    fontSize = 24.sp
                )
            )
        }
    }
}

@Composable
fun SplittingSection(
    users: List<User>,
    splittings: List<CreateUiStates.Splitting>,
    onSplittingChanged: (String, String) -> Unit,
    percentRemaining: Double
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.add_expense_percent_remaining))
            Text(text = "%.1f".format(percentRemaining) + "%")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(splittings.size) { index ->
                val it = splittings[index]
                SplittingElement(
                    users = users,
                    splitting = it,
                    onSplittingChanged = onSplittingChanged
                )
            }
        }
    }
}

@Composable
fun SplittingElement(
    users: List<User>,
    splitting: CreateUiStates.Splitting,
    onSplittingChanged: (String, String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(splitting.percentage.toFloat() / 100)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Spacer(modifier = Modifier.fillMaxSize())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = users.find { it.id == splitting.userId }?.name ?: "",
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .width(60.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = getBottomLineShape(1.dp)
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        focusRequester.requestFocus()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicTextField(
                    value = splitting.percentage,
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    ),
                    onValueChange = { onSplittingChanged(splitting.userId, it) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )

                Text(
                    modifier = Modifier
                        .padding(4.dp),
                    text = "%",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

