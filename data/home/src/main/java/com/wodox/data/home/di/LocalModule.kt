package com.wodox.data.home.di


import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wodox.config.model.Config
import com.wodox.data.home.datasource.remote.api.ApiService
import com.wodox.data.home.datasource.remote.clienthttp.OkHttpProvider
import com.wodox.data.home.datasource.remote.datasource.AIChatDataSource
import com.wodox.data.home.datasource.remote.datasource.AIDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun providerApiService(
        app: Application
    ): ApiService {
        val okHttpClient = OkHttpProvider.provideOkHttpClient(app)

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.API.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun providerAIChatDataSource(
        openAIApi: ApiService,
        gson: Gson
    ): AIChatDataSource {
        return AIDataSourceImpl(openAIApi, gson)
    }
}