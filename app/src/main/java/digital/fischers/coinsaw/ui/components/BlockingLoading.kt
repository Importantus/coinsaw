package digital.fischers.coinsaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BlockingLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x77000000))
        ,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}