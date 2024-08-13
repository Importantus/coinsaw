package digital.fischers.coinsaw.ui.components;

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseScreen(
    floatingActionButton: @Composable (() -> Unit)? = null,
    horizontalPadding: Int = 16,
    contentPaddingEnabled: Boolean = true,
    topColor: Color? = null,
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    blockingLoading: Boolean = false,
    title: String? = null,
    appBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val pullRefreshState = if(onRefresh != null) rememberPullRefreshState(refreshing, {onRefresh()}) else null

    Box(modifier = if(pullRefreshState != null) {
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    } else Modifier.fillMaxSize()
    ) {

        Scaffold(
            floatingActionButton = {
                floatingActionButton?.invoke()
            },
            topBar = {
                Box(
                    modifier = Modifier
                        .background(topColor ?: MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .fillMaxWidth()
                        .padding(
                            start = horizontalPadding.dp,
                            end = horizontalPadding.dp,
                            top = horizontalPadding.dp,
                            bottom = 24.dp
                        )
                ) {
                    appBar()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                Column(
                    modifier = Modifier

                        .padding(horizontal = if (contentPaddingEnabled) horizontalPadding.dp else 0.dp)
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    content()
                }
                if(pullRefreshState != null) {
                    Log.d("BaseScreen", "pullRefreshState: $pullRefreshState")
                    PullRefreshIndicator(
                        state = pullRefreshState,
                        refreshing = refreshing,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }

        if (blockingLoading) {
            BlockingLoading()
        }
    }
}
