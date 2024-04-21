package com.thrashspeed.gamecore.data.access.igdb

import android.util.Log
import com.thrashspeed.gamecore.data.model.GameDetailed
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
    private fun createTextRequestBody(body: String): RequestBody {
        return body.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun searchGames(search: String, callback: (List<GameItem>) -> Unit) {
        val searchGamesQuery =
            IgdbQuery()
                .addFields(listOf("name", "cover.image_id", "first_release_date"))
                .addSearch(search)
                .addWhereClause("total_rating_count > 30")
                .addLimit(30)
                .buildQuery()
        Log.d("busca", searchGamesQuery)

        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(searchGamesQuery))

        call.enqueue(object : Callback<List<GameItem>> {
            override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                Log.d("GamesAccess", "searchGames:onFailure: " + t.message)
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

    fun getGameDetails(id: Int, callback: (List<GameDetailed>) -> Unit) {
        val gameDetailsQuery =
            IgdbQuery()
                .addFields(listOf("name", "summary", "cover.image_id", "genres.name", "involved_companies", "first_release_date", "total_rating"))
                .addWhereClause("id = ($id)")
                .buildQuery()

        val call = RetrofitService.tmdbApi.getGame(createTextRequestBody(gameDetailsQuery))

        call.enqueue(object : Callback<List<GameDetailed>> {
            override fun onFailure(call: Call<List<GameDetailed>>, t: Throwable) {
                Log.d("GamesAccess", "getGameDetails:onFailure: " + t.message)
            }

            override fun onResponse(call: Call<List<GameDetailed>>, response: Response<List<GameDetailed>>) {
                val game = response.body()

                if (game != null) {
                    callback.invoke(game)
                }
            }
        })
    }

    fun getFilteredGames(genres: List<Int>, sortOption: IgdbSortOptions, callback: (List<GameItem>) -> Unit) {
        val filteredGamesQuery =
            IgdbQuery()
                .addFields(listOf("name", "cover.image_id", "first_release_date"))
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

    fun getFamousGames(callback: (List<GameItem>) -> Unit) {
        val famousGamesQuery =
            IgdbQuery()
                .addFields(listOf("name", "cover.image_id", "first_release_date"))
                .addSortBy(IgdbSortOptions.MOST_PLAYED)
                .addLimit(30)
                .buildQuery()

        val call = RetrofitService.tmdbApi.getGames(createTextRequestBody(famousGamesQuery))

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