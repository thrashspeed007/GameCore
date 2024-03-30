package com.thrashspeed.gamecore.data.access

import android.util.Log
import com.thrashspeed.gamecore.data.model.Game
import com.thrashspeed.gamecore.network.RetrofitService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GamesAccess {
    private val famousGamesRequestBody = "fields name; sort total_rating_count desc; limit 30;"

    private fun createTextRequestBody(body: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), body)
    }

    fun getFamousGames(callback: (List<Game>) -> Unit) {
        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(famousGamesRequestBody))

        call.enqueue(object : Callback<List<Game>> {
            override fun onFailure(call: Call<List<Game>>, t: Throwable) {
                Log.d("GamesAccess", "getFamousGames:onFailure: " + t.message)
            }

            override fun onResponse(call: Call<List<Game>>, response: Response<List<Game>>) {
                val games = response.body()

                if (games != null) {
                    callback.invoke(games)
                }
            }
        })
    }
}