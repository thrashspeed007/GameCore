package com.thrashspeed.gamecore.data.access

import android.util.Log
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GamesAccess {
    private val famousGamesRequestBody = "fields name, cover.image_id; sort total_rating_count desc; limit 30;"

    private fun createTextRequestBody(body: String): RequestBody {
        return body.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getFamousGames(callback: (List<GameItem>) -> Unit) {
        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(famousGamesRequestBody))

        call.enqueue(object : Callback<List<GameItem>> {
            override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                Log.d("GamesAccess", "getFamousGames:onFailure: " + t.message)
            }

            override fun onResponse(call: Call<List<GameItem>>, response: Response<List<GameItem>>) {
                val games = response.body()

                if (games != null) {
                    callback.invoke(games)
                }
            }
        })
    }
}