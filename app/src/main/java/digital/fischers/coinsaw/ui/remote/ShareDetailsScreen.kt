package digital.fischers.coinsaw.ui.remote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.media.session.MediaSession.Token
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.coinsaw.R
import digital.fischers.coinsaw.ui.components.BaseScreen
import digital.fischers.coinsaw.ui.components.CustomButton
import digital.fischers.coinsaw.ui.components.CustomNavigationBar
import digital.fischers.coinsaw.ui.components.Result
import digital.fischers.coinsaw.ui.components.TokenDisplay
import digital.fischers.coinsaw.ui.components.generateQRCode
import digital.fischers.coinsaw.ui.viewModels.ShareDetailsViewModel
import kotlinx.coroutines.launch
import qrcode.QRCode
import qrcode.color.Colors
import java.io.FileOutputStream

@Composable
fun ShareDetailsScreen(
    onBackNavigation: () -> Unit,
    viewModel: ShareDetailsViewModel = hiltViewModel()
) {
    val share = viewModel.share
    val loading = viewModel.loading

    BaseScreen(
        appBar = {
            CustomNavigationBar(
                title = stringResource(id = R.string.screen_share_details_title),
                backNavigationText = null,
                backNavigation = {
                    onBackNavigation()
                })

        },
        blockingLoading = loading,
        title = share?.name ?: "",
    ) {
        if (share != null) {
            Text(text = stringResource(id = R.string.share_details_sessions_created) + ": ${share.sessions.size}/${share.maxSessions}")
            Spacer(modifier = Modifier.height(32.dp))
            QrBox(string = share.token)
            Spacer(modifier = Modifier.height(16.dp))
            TokenDisplay(token = share.token)
        }
    }
}

@Composable
fun QrBox(string: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        QrCode(string = string, modifier = Modifier.clip(MaterialTheme.shapes.small))
    }
}

@Composable
fun QrCode(string: String, modifier: Modifier = Modifier, color: Int = Colors.WHITE) {
    val bitmap = generateQRCode(content = string).value.let {
        when (it) {
            is Result.Success -> it.content
            else -> null
        }
    }
    if(bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}