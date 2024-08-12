package digital.fischers.coinsaw.ui.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.color.Colors

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    object Error : Result<Nothing>()
    data class Success<T>(val content: T?) : Result<T>()
}

@Composable
fun generateQRCode(content: String): State<Result<ImageBitmap>> {
    return produceState<Result<ImageBitmap>>(initialValue = Result.Loading, content) {
        val qr = QRCode
            .ofRoundedSquares()
            .withColor(Colors.WHITE)
            .withBackgroundColor(Colors.TRANSPARENT)
            .withSize(30).build(content)
        val bytes = qr.renderToBytes()
        val bitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )
        value = Result.Success(bitmap.asImageBitmap())
    }
}