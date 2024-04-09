package com.thrashspeed.gamecore.data.access

import android.util.Log
import com.thrashspeed.gamecore.data.model.PlatformItem
import com.thrashspeed.gamecore.network.RetrofitService
import com.thrashspeed.gamecore.utils.igdb.IgdbPlatformCategories
import com.thrashspeed.gamecore.utils.igdb.IgdbQuery
import com.thrashspeed.gamecore.utils.igdb.IgdbSortOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlatformsAccess {
    private fun createTextRequestBody(body: String): RequestBody {
        return body.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getLatestPlatforms(callback: (List<PlatformItem>) -> Unit) {
        val latestPlatformsQuery =
            IgdbQuery()
                .addFields(listOf("name", "platform_logo.image_id"))
                .addWhereClause("category = ${IgdbPlatformCategories.CONSOLE.id}")
                .addSortBy(IgdbSortOptions.GENERATION)
                .addLimit(28)
                .buildQuery()
        Log.d("llega", latestPlatformsQuery)

        val call = RetrofitService.tmdbApi.getPlatforms(createTextRequestBody(latestPlatformsQuery))

        call.enqueue(object : Callback<List<PlatformItem>> {
            override fun onFailure(call: Call<List<PlatformItem>>, t: Throwable) {
                Log.d("PlatformsAccess", "getLatestPlatforms:onFailure: " + t.message)
            }

            override fun onResponse(call: Call<List<PlatformItem>>, response: Response<List<PlatformItem>>) {
                val platforms = response.body()

                if (platforms != null) {
                    callback.invoke(platforms)
                }
            }
        })
    }

}