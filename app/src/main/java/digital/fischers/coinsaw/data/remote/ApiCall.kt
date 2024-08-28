package digital.fischers.coinsaw.data.remote

import android.util.Log
import retrofit2.Response

suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): APIResult<T> {
        val response: Response<T>

        try {
            response = call.invoke()
        } catch (e: Exception) {
            Log.d("API_CALL", "Exception: $e")
            return APIResult.Error(APIError.NetworkError)
        }

        return if(!response.isSuccessful) {
            val errorBody = response.errorBody()
            APIResult.Error(
                APIError.CustomError(
                    response.message(),
                    response.code(),
                    errorBody?.string() ?: "Unknown error"
                )
            )
        } else {
            return if (response.body() == null) {
                APIResult.Success(Unit as T)
            } else {
                APIResult.Success(response.body()!!)
            }
        }
    }

sealed class APIResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : APIResult<T>()
    data class Error(val exception: APIError) : APIResult<Nothing>()
}

sealed class APIError {
    data class CustomError(val message: String, val code: Int, val errorBody: String) : APIError()
    object NetworkError : APIError()
    object UnknownError : APIError()
}