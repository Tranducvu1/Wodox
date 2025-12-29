package com.wodox.data.home.datasource.remote.clienthttp

import android.content.Context
import com.wodox.config.model.Config
import com.wodox.data.home.BuildConfig
import com.wodox.data.home.datasource.remote.interceptor.RetryInterceptor
import com.wodox.data.home.extension.appSharePrefs
import com.wodox.data.home.utils.SignatureUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

object OkHttpProvider {
    private fun provideOkHttpCache(context: Context): Cache {
        val cacheSize: Long = 10 * 1024 * 1024
        return Cache(context.cacheDir, cacheSize)
    }

    private fun providerX509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun providerSslSocketFactory(trust: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(trust), SecureRandom())
        return sslContext.socketFactory
    }

    fun provideOkHttpClient(context: Context): OkHttpClient {
        val deviceId = context.appSharePrefs.deviceId ?: ""
        val httpClientBuilder = OkHttpClient.Builder()
            .addNetworkInterceptor(
                Interceptor { chain ->
                    val signature = SignatureUtils.hash("sha256", Config.API.KID, Config.API.KID)
                    var request: Request? = null
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader(
                            "Authorization",
                            "Signature $signature"
                        )
                        .addHeader(
                            "device-id",
                            deviceId
                        )
                        .addHeader(
                            "package-id",
                            "com.starnest.ai.chipi"
                        )
                        .addHeader(
                            "isUserInAdMode",
                            "true"
                        )
                    request = requestBuilder.build()
                    chain.proceed(request)
                })
        httpClientBuilder.cache(provideOkHttpCache(context))

        httpClientBuilder.readTimeout(
            READ_TIMEOUT, TimeUnit.SECONDS
        )
        httpClientBuilder.writeTimeout(
            WRITE_TIMEOUT, TimeUnit.SECONDS
        )
        httpClientBuilder.connectTimeout(
            CONNECTION_TIMEOUT, TimeUnit.SECONDS
        )

        val trustManager = providerX509TrustManager()
        httpClientBuilder.sslSocketFactory(providerSslSocketFactory(trustManager), trustManager)
        httpClientBuilder.hostnameVerifier { _, _ -> true }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            httpClientBuilder.addInterceptor(logging)
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        httpClientBuilder.addInterceptor(RetryInterceptor(context, Config.API.BASE_BACKUP_URL))

        return httpClientBuilder.build()
    }

    private const val READ_TIMEOUT: Long = 60 * 2
    private const val WRITE_TIMEOUT: Long = 60 * 2
    private const val CONNECTION_TIMEOUT: Long = 60 * 2
}