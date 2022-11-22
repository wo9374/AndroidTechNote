package com.example.androidtechnote.recycler.custom_focus.data

import com.example.androidtechnote.BuildConfig
import com.example.androidtechnote.recycler.custom_focus.data.GetFocusItemResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBMoviesApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEYS,
        @Query("page") page : Int,
        @Query("language") language : String = "ko" //"ko,en-US"
    ): Response<GetFocusItemResponse>
}