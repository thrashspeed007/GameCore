package com.thrashspeed.gamecore.data.access

import android.util.Log
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.network.RetrofitService
import com.thrashspeed.gamecore.utils.igdb.IgdbQuery
import com.thrashspeed.gamecore.utils.igdb.IgdbSortOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class GamesAccess {
    private val famousGamesRequestBody = "fields name, cover.image_id; sort total_rating_count desc; limit 30;"

    private fun createTextRequestBody(body: String): RequestBody {
        return body.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getFilteredGames(genres: List<Int>, sortOption: IgdbSortOptions, callback: (List<GameItem>) -> Unit) {
        val filteredGamesQuery =
            IgdbQuery()
                .addFields(listOf("name", "cover.image_id"))
                .addWhereClause(if (genres.isNotEmpty()) "genres = [${genres.joinToString(", ")}]" else "", "total_rating_count >= 200")
                .addSortBy(sortOption)
                .addLimit(30)
                .buildQuery()

        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(filteredGamesQuery))

        call.enqueue(object : Callback<List<GameItem>> {
            override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                Log.d("GamesAccess", "getFilteredGames:onFailure: " + t.message)
            }

            override fun onResponse(
                call: Call<List<GameItem>>,
                response: Response<List<GameItem>>
            ) {
                val games = response.body()

                if (games != null) {
                    callback.invoke(games)
                }
            }
        })
    }

    // TODO
    // HACER CON IDGBQUERY EN VEZ DEL STRING ESE CUTRE
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

    fun getTrendingGames(callback: (List<GameItem>) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -3)

        val threeWeeksAgo = calendar.timeInMillis / 1000
        val today = Calendar.getInstance().timeInMillis / 1000

        val trendingGamesQuery =
            IgdbQuery()
                .addFields(listOf("name", "platform_logo.image_id"))
                .addWhereClause(
                    "first_release_date > \"$threeWeeksAgo\"",
                    "first_release_date < \"$today\""
                )
                .addSortBy(IgdbSortOptions.MOST_PLAYED)
                .addLimit(20)
                .buildQuery()

        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(trendingGamesQuery))

        call.enqueue(object : Callback<List<GameItem>> {
            override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                Log.d("GamesAccess", "getTrendingGames:onFailure: " + t.message)
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