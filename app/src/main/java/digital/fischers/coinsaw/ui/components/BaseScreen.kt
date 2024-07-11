package digital.fischers.coinsaw.ui.components;

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import digital.fischers.coinsaw.R

@Composable
fun BaseScreen(
    floatingActionButton: @Composable (() -> Unit)? = null,
    horizontalPadding: Int = 16,
    nonBlockingLoading: Boolean = false,
    blockingLoading: Boolean = false,
    title: String? = null,
    appBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                floatingActionButton?.invoke()
            },
            topBar = {
                Box(
                    modifier = Modifier
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
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = horizontalPadding.dp)
            ) {
                if(title != null) {
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
        }

        if (blockingLoading) {
            BlockingLoading()
        }
    }
}
