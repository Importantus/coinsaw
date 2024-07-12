package digital.fischers.coinsaw.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

private enum class State {
    LOADING,
    FALLBACK,
    READY
}

@Composable
fun ContentWrapperWithFallback(
    vararg keys: Any,
    showCondition: Boolean,
    loading: @Composable (() -> Unit)? = null,
    fallback: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var state by remember { mutableStateOf(State.LOADING) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state == State.LOADING) {
            loading?.invoke()
        }

        SubtleFadeIn(show = state == State.FALLBACK) {
            fallback()
        }

        if(state == State.READY) {
            content()
        }

    }

    LaunchedEffect(keys = keys) {
        if (!showCondition) {
            delay(500)
            state = State.FALLBACK
        } else {
            state = State.READY
        }
    }
}

@Composable
private fun SubtleFadeIn(
    show: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(
            animationSpec = tween(300)
        ),
        exit = ExitTransition.None
    ) {
        content()
    }
}