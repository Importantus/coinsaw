package digital.fischers.coinsaw.ui

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

    data object GroupMemberList : Screen("group/{$ARG_GROUP_ID}/memberList") {
        fun createRoute(groupId: String) = "group/$groupId/memberList"
    }

    data object GroupCreateMember : Screen("group/{$ARG_GROUP_ID}/createMember") {
        fun createRoute(groupId: String) = "group/$groupId/createMember"
    }

    data object GroupEditMember : Screen("group/{$ARG_GROUP_ID}/editMember/{$ARG_USER_ID}") {
        fun createRoute(groupId: String, userId: String) = "group/$groupId/editMember/$userId"
    }

    data object NewGroup : Screen("newGroup")

    data object NewGroupSettings : Screen("newGroup/settings")

    data object NewGroupCreateMember : Screen("newGroup/{$ARG_GROUP_ID}/createMember") {
        fun createRoute(groupId: String) = "newGroup/$groupId/createMember"
    }

    data object NewGroupMemberList : Screen("newGroup/{$ARG_GROUP_ID}/memberList") {
        fun createRoute(groupId: String) = "newGroup/$groupId/memberList"
    }

    data object NewGroupEditMember : Screen("newGroup/{$ARG_GROUP_ID}/editMember/{$ARG_USER_ID}") {
        fun createRoute(groupId: String, userId: String) = "newGroup/$groupId/editMember/$userId"
    }

    data object AddBill : Screen("group/{$ARG_GROUP_ID}/addItem") {
        fun createRoute(groupId: String) = "group/$groupId/addItem"
    }

    companion object {
        const val ARG_GROUP_ID = "groupId"
        const val ARG_USER_ID = "userId"
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
        if(navController.previousBackStackEntry?.destination?.route != Screen.Home.route) {
            navController.navigate(Screen.Home.route)
        } else {
            navController.popBackStack()
        }
    }

    fun navigateToGroup(groupId: String) {
        navController.navigate(Screen.Group.createRoute(groupId))
    }

    fun navigateToGroupSettings(groupId: String) {
        navController.navigate(Screen.GroupSettings.createRoute(groupId))
    }

    fun navigateToGroupMemberList(groupId: String) {
        navController.navigate(Screen.GroupMemberList.createRoute(groupId))
    }

    fun navigateToGroupCreateMember(groupId: String) {
        navController.navigate(Screen.GroupCreateMember.createRoute(groupId))
    }

    fun navigateToGroupEditMember(groupId: String, userId: String) {
        navController.navigate(Screen.GroupEditMember.createRoute(groupId, userId))
    }

    fun navigateToAddBill(groupId: String) {
        navController.navigate(Screen.AddBill.createRoute(groupId))
    }

    fun navigateToNewGroupName() {
        navController.navigate(Screen.NewGroupSettings.route)
    }

    fun navigateToNewGroupCreateMember(groupId: String) {
        navController.navigate(Screen.NewGroupCreateMember.createRoute(groupId))
    }

    fun navigateToNewGroupMemberList(groupId: String) {
        navController.navigate(Screen.NewGroupMemberList.createRoute(groupId))
    }

    fun navigateToNewGroupEditMember(groupId: String, userId: String) {
        navController.navigate(Screen.NewGroupEditMember.createRoute(groupId, userId))
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}