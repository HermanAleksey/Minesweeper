package com.example.sapper.controller.network

import com.example.sapper.controller.network.api.AuthApi
import com.example.sapper.controller.network.api.SaperApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


object NetworkService {

    private val BASE_URL = "https://192.168.1.7:8080/"
//    private val BASE_URL = "https://26.36.145.54:8080/"
    private var mRetrofit: Retrofit? = null
    private var api: SaperApi
    private var authApi: AuthApi

    init {
        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = mRetrofit!!.create(SaperApi::class.java)
        authApi = mRetrofit!!.create(AuthApi::class.java)
    }

    fun getSaperApi(): SaperApi {
        return api
    }

    fun getAuthApi(): AuthApi {
        return authApi
    }

    fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                                chain: Array<X509Certificate?>?,
                                authType: String?
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                                chain: Array<X509Certificate?>?,
                                authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                            return arrayOf()
                        }
                    }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                    trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                    trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}