package digital.fischers.coinsaw.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import digital.fischers.coinsaw.ui.group.GroupScreen
import digital.fischers.coinsaw.ui.group.NewGroupScreen
import digital.fischers.coinsaw.ui.home.HomeScreen
import digital.fischers.coinsaw.ui.members.AddMemberScreen
import digital.fischers.coinsaw.ui.members.EditMemberScreen
import digital.fischers.coinsaw.ui.members.MemberlistScreen
import digital.fischers.wisesplitpocv2.ui.CoinsawAppState
import digital.fischers.wisesplitpocv2.ui.Screen
import digital.fischers.wisesplitpocv2.ui.rememberCoinsawAppState

@Composable
fun CoinsawApp(
    appState: CoinsawAppState = rememberCoinsawAppState()
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = appState.navController,
        startDestination = Screen.Home.route,
//        enterTransition = { EnterTransition.None },
//        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onGroupAddClicked = { appState.navigateToNewGroupName() },
                onGroupJoinClicked = { /*TODO*/ },
                onGroupClicked = { groupId ->
                    appState.navigateToGroup(groupId)
                })
        }

        navigation(
            route = Screen.NewGroup.route,
            startDestination = Screen.NewGroupSettings.route,
        ) {
            composable(Screen.NewGroupSettings.route) {
                NewGroupScreen(onNavigateBack = { appState.navigateHome() },
                    onNavigateForward = { appState.navigateToNewGroupCreateMember(it) })
            }

            composable(Screen.NewGroupCreateMember.route) {
                AddMemberScreen(onBackNavigation = {
                    appState.navController.navigate(route = Screen.Home.route) {
                        popUpTo(route = Screen.Home.route) { inclusive = false }
                    }
                }, onForwardNavigation = {
                    appState.navigateToNewGroupMemberList(it)
                }, isInGroupCreationFlow = true
                )
            }

            composable(Screen.NewGroupEditMember.route) {
                EditMemberScreen(
                    onBackNavigation = {
                        appState.navigateBack()
                    },
                    isInGroupCreationFlow = true
                )
            }

            composable(Screen.NewGroupMemberList.route) {
                MemberlistScreen(onBackNavigation = {
                    appState.navController.navigate(route = Screen.Home.route) {
                        popUpTo(route = Screen.Home.route) { inclusive = false }
                    }
                }, onMemberAddClicked = {
                    appState.navigateToNewGroupCreateMember(it)
                }, onMemberClicked = { groupId, memberId ->
                    appState.navigateToNewGroupEditMember(groupId, memberId)
                }, isInGroupCreationFlow = true, onForwardNavigation = {
                    appState.navController.navigate(route = Screen.Group.createRoute(it)) {
                        popUpTo(route = Screen.Home.route) { inclusive = false }
                    }
                })
            }
        }

        composable(Screen.Group.route) {
            GroupScreen(onBackNavigation = { appState.navigateHome() },
                onGroupSettingsClicked = {},
                onGroupMembersClicked = {
                    appState.navigateToGroupMemberList(it)
                },
                onAddMemberClicked = {
                    appState.navigateToGroupCreateMember(it)
                },
                onBillClicked = {},
                onAddBillClicked = {},
                onAddTransactionClicked = {},
                onSettleUpClicked = {})
        }

        composable(Screen.GroupCreateMember.route) {
            AddMemberScreen(onBackNavigation = { appState.navigateBack() },
                onForwardNavigation = { appState.navigateBack() })
        }

        composable(Screen.GroupMemberList.route) {
            MemberlistScreen(onBackNavigation = { appState.navigateBack() },
                onMemberAddClicked = { appState.navigateToGroupCreateMember(it) },
                onMemberClicked = { groupId, memberId ->
                    appState.navigateToGroupEditMember(
                        groupId,
                        memberId
                    )
                })
        }

        composable(Screen.GroupEditMember.route) {
            EditMemberScreen(onBackNavigation = { appState.navigateBack() })
        }
    }
}