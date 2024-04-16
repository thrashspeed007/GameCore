package com.thrashspeed.gamecore.network.interfaces

import com.thrashspeed.gamecore.data.model.GameDetailed
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.data.model.PlatformItem
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IgdbApiInterface {

    @POST("games")
    fun getGames(
        @Body requestBody: RequestBody
    ): Call<List<GameItem>>

    @POST("games")
    fun getGame(
        @Body requestBody: RequestBody
    ): Call<List<GameDetailed>>

    @POST("platforms")
    fun getPlatforms(
        @Body requestBody: RequestBody
    ): Call<List<PlatformItem>>
}