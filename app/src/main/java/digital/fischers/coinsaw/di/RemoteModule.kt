package digital.fischers.coinsaw.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import digital.fischers.coinsaw.data.remote.ApiService
import digital.fischers.coinsaw.data.util.ChangelogJSONAdapter
import digital.fischers.coinsaw.domain.changelog.Entry
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {
    @Provides
    @Singleton
    fun provideCoinsawApi(): ApiService {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

        return Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(Entry::class.java, ChangelogJSONAdapter())
                .create()))
            .build()
            .create(ApiService::class.java)
    }
}