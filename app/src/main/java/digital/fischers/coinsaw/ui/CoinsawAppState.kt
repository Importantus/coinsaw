package digital.fischers.coinsaw.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import digital.fischers.coinsaw.ui.bill.AddTransactionArguments

sealed class Screen(val route: String, val deepLinkPath: String? = null) {
    data object Home : Screen("home")

    data object Group : Screen("group/{$ARG_GROUP_ID}", "/group") {
        fun createRoute(groupId: String) = "group/$groupId"
        fun createDeepLink(groupId: String) = "${DEEP_LINK_HOST}group?$ARG_GROUP_ID=$groupId"
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

    data object AddBill : Screen("group/{$ARG_GROUP_ID}/addItem", "/addBill") {
        fun createRoute(groupId: String) = "group/$groupId/addItem"
        fun createDeepLink(groupId: String) = "${DEEP_LINK_HOST}addBill?$ARG_GROUP_ID=$groupId"
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

    data object EnterShareToken : Screen("enterShareToken/{$ARG_SHARE_TOKEN_ERROR}/{$ARG_GROUP_MISSING_SESSION_ERROR}") {
        fun createRoute(shareToken: String, groupMissingSession: Boolean) = "enterShareToken/$shareToken/$groupMissingSession"
    }

    data object Shares: Screen("group/{$ARG_GROUP_ID}/shares") {
        fun createRoute(groupId: String) = "group/$groupId/shares"
    }

    data object ShareDetails: Screen("group/{$ARG_GROUP_ID}/share/{$ARG_SHARE_ID}") {
        fun createRoute(groupId: String, shareId: String) = "group/$groupId/share/$shareId"
    }

    data object BillDetails: Screen("group/{$ARG_GROUP_ID}/bill/{$ARG_BILL_ID}") {
        fun createRoute(groupId: String, billId: String) = "group/$groupId/bill/$billId"
    }

    data object CreateTransaction: Screen("group/{$ARG_GROUP_ID}/createTransaction/{$ARGS_CREATE_TRANSACTION}") {
        fun createRoute(groupId: String, args: AddTransactionArguments): String {
            return "group/$groupId/createTransaction/" + Gson().toJson(args)
        }
    }

    data object Sessions: Screen("group/{$ARG_GROUP_ID}/sessions") {
        fun createRoute(groupId: String) = "group/$groupId/sessions"
    }

    companion object {
        const val DEEP_LINK_HOST = "coinsaw://coinsaw.fischers.digital/"
        const val ARG_GROUP_ID = "groupId"
        const val ARG_USER_ID = "userId"
        const val ARG_RECOVERY_TOKEN = "recoveryToken"
        const val ARG_SHARE_TOKEN = "shareToken"
        const val ARG_SHARE_ID = "shareId"
        const val ARG_BILL_ID = "billId"
        const val ARG_SHARE_TOKEN_ERROR = "shareTokenError"
        const val ARG_GROUP_MISSING_SESSION_ERROR = "groupMissingSessionError"
        const val ARGS_CREATE_TRANSACTION = "createTransaction"
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
    private fun navigateBackstackAware(route: String, screen: Screen) {
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        if(previousRoute != screen.route) {
            navController.navigate(route)
        } else {
            navController.popBackStack()
        }
    }

    fun navigateHome() {
        // Clear all backstack entries and navigate to home
        navController.popBackStack(navController.graph.startDestinationId, false)
    }

    fun navigateToGroup(groupId: String) {
        navigateBackstackAware(Screen.Group.createRoute(groupId), Screen.Group)
    }

    fun navigateToGroupSettings(groupId: String) {
        navigateBackstackAware(Screen.GroupSettings.createRoute(groupId), Screen.GroupSettings)
    }

    fun navigateToGroupMemberList(groupId: String) {
        navigateBackstackAware(Screen.GroupMemberList.createRoute(groupId), Screen.GroupMemberList)
    }

    fun navigateToGroupCreateMember(groupId: String) {
        navigateBackstackAware(Screen.GroupCreateMember.createRoute(groupId), Screen.GroupCreateMember)
    }

    fun navigateToGroupEditMember(groupId: String, userId: String) {
        navigateBackstackAware(Screen.GroupEditMember.createRoute(groupId, userId), Screen.GroupEditMember)
    }

    fun navigateToAddBill(groupId: String) {
        navigateBackstackAware(Screen.AddBill.createRoute(groupId), Screen.AddBill)
    }

    fun navigateToNewGroupName() {
        navigateBackstackAware(Screen.NewGroupSettings.route, Screen.NewGroupSettings)
    }

    fun navigateToNewGroupCreateMember(groupId: String) {
        navigateBackstackAware(Screen.NewGroupCreateMember.createRoute(groupId), Screen.NewGroupCreateMember)
    }

    fun navigateToNewGroupMemberList(groupId: String) {
        navigateBackstackAware(Screen.NewGroupMemberList.createRoute(groupId), Screen.NewGroupMemberList)
    }

    fun navigateToNewGroupEditMember(groupId: String, userId: String) {
        navigateBackstackAware(Screen.NewGroupEditMember.createRoute(groupId, userId) , Screen.NewGroupEditMember)
    }

    fun navigateToMakeOnline(groupId: String) {
        navigateBackstackAware(Screen.MakeOnline.createRoute(groupId), Screen.MakeOnline)
    }

    fun navigateToShowRecovery(groupId: String, recoveryToken: String) {
        navigateBackstackAware(Screen.ShowRecovery.createRoute(groupId, recoveryToken), Screen.ShowRecovery)
    }

    fun navigateToInitialSync(shareToken: String) {
        navigateBackstackAware(Screen.InitialSync.createRoute(shareToken), Screen.InitialSync)
    }

    fun navigateToEnterShareToken(shareToken: String, groupMissingSession: Boolean) {
        navigateBackstackAware(Screen.EnterShareToken.createRoute(shareToken, groupMissingSession), Screen.EnterShareToken)
    }

    fun navigateToShares(groupId: String) {
        navigateBackstackAware(Screen.Shares.createRoute(groupId), Screen.Shares)
    }

    fun navigateToShareDetails(groupId: String, shareId: String) {
        navigateBackstackAware(Screen.ShareDetails.createRoute(groupId, shareId), Screen.ShareDetails)
    }

    fun navigateToBillDetails(groupId: String, billId: String) {
        navigateBackstackAware(Screen.BillDetails.createRoute(groupId, billId), Screen.BillDetails)
    }

    fun navigateToCreateTransaction(groupId: String, args: AddTransactionArguments) {
        navigateBackstackAware(Screen.CreateTransaction.createRoute(groupId, args), Screen.CreateTransaction)
    }

    fun navigateToSessions(groupId: String) {
        navigateBackstackAware(Screen.Sessions.createRoute(groupId), Screen.Sessions)
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}