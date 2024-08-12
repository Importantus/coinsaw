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

    data object MakeOnline : Screen("group/{$ARG_GROUP_ID}/makeOnline") {
        fun createRoute(groupId: String) = "group/$groupId/makeOnline"
    }

    data object ShowRecovery : Screen("group/{$ARG_GROUP_ID}/showRecovery/{$ARG_RECOVERY_TOKEN}") {
        fun createRoute(groupId: String, recoveryToken: String) = "group/$groupId/showRecovery/$recoveryToken"
    }

    data object InitialSync : Screen("initialSync/{$ARG_SHARE_TOKEN}") {
        fun createRoute(shareToken: String) = "initialSync/$shareToken"
    }

    data object EnterShareToken : Screen("enterShareToken") {
        fun createRoute() = "enterShareToken"
    }

    data object Shares: Screen("group/{$ARG_GROUP_ID}/shares") {
        fun createRoute(groupId: String) = "group/$groupId/shares"
    }

    data object ShareDetails: Screen("group/{$ARG_GROUP_ID}/share/{$ARG_SHARE_ID}") {
        fun createRoute(groupId: String, shareId: String) = "group/$groupId/share/$shareId"
    }

    companion object {
        const val ARG_GROUP_ID = "groupId"
        const val ARG_USER_ID = "userId"
        const val ARG_RECOVERY_TOKEN = "recoveryToken"
        const val ARG_SHARE_TOKEN = "shareToken"
        const val ARG_SHARE_ID = "shareId"
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
    private fun navigateBackstackAware(route: String) {
        if(navController.previousBackStackEntry?.destination?.route != route) {
            navController.navigate(route)
        } else {
            navController.popBackStack()
        }
    }

    fun navigateHome() {
        navigateBackstackAware(Screen.Home.route)
    }

    fun navigateToGroup(groupId: String) {
        navigateBackstackAware(Screen.Group.createRoute(groupId))
    }

    fun navigateToGroupSettings(groupId: String) {
        navigateBackstackAware(Screen.GroupSettings.createRoute(groupId))
    }

    fun navigateToGroupMemberList(groupId: String) {
        navigateBackstackAware(Screen.GroupMemberList.createRoute(groupId))
    }

    fun navigateToGroupCreateMember(groupId: String) {
        navigateBackstackAware(Screen.GroupCreateMember.createRoute(groupId))
    }

    fun navigateToGroupEditMember(groupId: String, userId: String) {
        navigateBackstackAware(Screen.GroupEditMember.createRoute(groupId, userId))
    }

    fun navigateToAddBill(groupId: String) {
        navigateBackstackAware(Screen.AddBill.createRoute(groupId))
    }

    fun navigateToNewGroupName() {
        navigateBackstackAware(Screen.NewGroupSettings.route)
    }

    fun navigateToNewGroupCreateMember(groupId: String) {
        navigateBackstackAware(Screen.NewGroupCreateMember.createRoute(groupId))
    }

    fun navigateToNewGroupMemberList(groupId: String) {
        navigateBackstackAware(Screen.NewGroupMemberList.createRoute(groupId))
    }

    fun navigateToNewGroupEditMember(groupId: String, userId: String) {
        navigateBackstackAware(Screen.NewGroupEditMember.createRoute(groupId, userId))
    }

    fun navigateToMakeOnline(groupId: String) {
        navigateBackstackAware(Screen.MakeOnline.createRoute(groupId))
    }

    fun navigateToShowRecovery(groupId: String, recoveryToken: String) {
        navigateBackstackAware(Screen.ShowRecovery.createRoute(groupId, recoveryToken))
    }

    fun navigateToInitialSync(shareToken: String) {
        navigateBackstackAware(Screen.InitialSync.createRoute(shareToken))
    }

    fun navigateToEnterShareToken() {
        navigateBackstackAware(Screen.EnterShareToken.createRoute())
    }

    fun navigateToShares(groupId: String) {
        navigateBackstackAware(Screen.Shares.createRoute(groupId))
    }

    fun navigateToShareDetails(groupId: String, shareId: String) {
        navigateBackstackAware(Screen.ShareDetails.createRoute(groupId, shareId))
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}