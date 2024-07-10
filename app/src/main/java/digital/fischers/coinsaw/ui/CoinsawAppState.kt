package digital.fischers.wisesplitpocv2.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Group : Screen("group/{$ARG_GROUP_ID}") {
        fun createRoute(groupId: String) = "group/$groupId"
    }

    data object GroupSettings : Screen("group/{$ARG_GROUP_ID}/settings") {
        fun createRoute(groupId: String) = "group/$groupId/settings"
    }

    data object NewGroup : Screen("newGroup")

    data object NewGroupSettings : Screen("newGroup/settings")

    data object NewGroupCreateMember : Screen("newGroup/{$ARG_GROUP_ID}/createMember") {
        fun createRoute(groupId: String) = "newGroup/$groupId/createMember"
    }

    data object NewGroupMemberList : Screen("newGroup/{$ARG_GROUP_ID}/memberList") {
        fun createRoute(groupId: String) = "newGroup/$groupId/memberList"
    }

    data object AddBill : Screen("group/{$ARG_GROUP_ID}/addItem") {
        fun createRoute(groupId: String) = "group/$groupId/addItem"
    }

    companion object {
        const val ARG_GROUP_ID = "groupId"
    }
}

@Composable
fun rememberCoinsawAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    CoinsawAppState(navController, context)
}

class CoinsawAppState(
    val navController: NavHostController,
    private val context: Context
) {
    fun navigateHome() {
        navController.navigate(Screen.Home.route)
    }

    fun navigateToGroup(groupId: String) {
        navController.navigate(Screen.Group.createRoute(groupId))
    }

    fun navigateToGroupSettings(groupId: String) {
        navController.navigate(Screen.GroupSettings.createRoute(groupId))
    }

    fun navigateToAddItem(groupId: String) {
        navController.navigate(Screen.AddBill.createRoute(groupId))
    }

    fun navigateToNewGroupName() {
        navController.navigate(Screen.NewGroupSettings.route)
    }

    fun navigateToNewGroupCreateMember(groupId: String) {
        navController.navigate(Screen.NewGroupCreateMember.createRoute(groupId))
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}