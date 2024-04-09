package com.thrashspeed.gamecore.network.interfaces

import com.thrashspeed.gamecore.data.model.Game
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.data.model.PlatformItem
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface IgdbApiInterface {

    @POST("games")
    fun searchGames(
        @Query("search") query: String,
        @Query("fields") fields: String = "*"
    ): Call<List<Game>>

    @POST("games")
    fun getGames(
        @Body requestBody: RequestBody
    ): Call<List<GameItem>>

    @POST("platforms")
    fun getPlatforms(
        @Body requestBody: RequestBody
    ): Call<List<PlatformItem>>
}