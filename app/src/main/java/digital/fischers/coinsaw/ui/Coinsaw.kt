package digital.fischers.coinsaw.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import digital.fischers.coinsaw.ui.group.NewGroupScreen
import digital.fischers.coinsaw.ui.home.HomeScreen
import digital.fischers.wisesplitpocv2.ui.CoinsawAppState
import digital.fischers.wisesplitpocv2.ui.Screen
import digital.fischers.wisesplitpocv2.ui.rememberCoinsawAppState

@Composable
fun CoinsawApp(
    appState: CoinsawAppState = rememberCoinsawAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onGroupAddClicked = { appState.navigateToNewGroupName() },
                onGroupJoinClicked = { /*TODO*/ },
                onGroupClicked = { groupId ->
                    appState.navigateToGroup(groupId)
                }
            )
        }

        navigation(
            route = Screen.NewGroup.route,
            startDestination = Screen.NewGroupSettings.route,
        ) {
            composable(Screen.NewGroupSettings.route) {
                NewGroupScreen(
                    onNavigateBack = { appState.navigateHome() },
                    onNavigateForward = { appState.navigateToNewGroupCreateMember(it) })
            }

            composable(Screen.NewGroupCreateMember.route) {
                // NewGroupCreateMemberScreen()
            }

            composable(Screen.NewGroupMemberList.route) {
                // NewGroupMemberListScreen()
            }
        }


    }
}