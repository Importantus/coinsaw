package digital.fischers.coinsaw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.coinsaw.ui.CoinsawApp
import digital.fischers.coinsaw.ui.CoinsawAppState
import digital.fischers.coinsaw.ui.home.HomeScreen
import digital.fischers.coinsaw.ui.rememberCoinsawAppState
import digital.fischers.coinsaw.ui.theme.CoinsawTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoinsawTheme {
                CoinsawApp(intent)
            }
        }
    }
}