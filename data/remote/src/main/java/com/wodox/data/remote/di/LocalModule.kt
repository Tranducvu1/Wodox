package com.wodox.data.remote.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wodox.config.model.Config
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.data.remote.model.api.ApiService
import com.wodox.data.remote.model.clienthttp.OkHttpProvider
import com.wodox.data.remote.model.datasource.AIChatDataSourceImpl
import com.wodox.data.remote.model.datasource.AIDataSource
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
        app: Application,
        appSharePrefs: AppSharePrefs
    ): ApiService {
        val okHttpClient = OkHttpProvider.provideOkHttpClient(app, appSharePrefs)

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
    ): AIDataSource {
        return AIChatDataSourceImpl(openAIApi, gson)
    }
}