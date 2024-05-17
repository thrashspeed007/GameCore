package com.thrashspeed.gamecore.network

import com.thrashspeed.gamecore.config.IgdbConectionData
import com.thrashspeed.gamecore.network.interfaces.IgdbApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Clase singleton que proporciona un servicio Retrofit para interactuar con la API de IGDB (Internet Game Database).
 */
object RetrofitService {

    var accessToken: String? = "21mzkzasaszxvr8egsnepzpk9u1bo7"

    /**
     * Objeto Retrofit para realizar llamadas a la API de IGDB.
     */
    private val igdbRetrofit: Retrofit by lazy {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(AuthInterceptor())

        Retrofit.Builder()
            .baseUrl(IgdbConectionData.IGDB_BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Interfaz que define los puntos finales (endpoints) de la API de IGDB.
     */
    val igdbApi: IgdbApiInterface by lazy {
        igdbRetrofit.create(IgdbApiInterface::class.java)
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Client-ID", IgdbConectionData.IGDB_CLIENT_ID)
            .addHeader("Authorization", "Bearer ${RetrofitService.accessToken}")
            .build()
        return chain.proceed(request)
    }
}
