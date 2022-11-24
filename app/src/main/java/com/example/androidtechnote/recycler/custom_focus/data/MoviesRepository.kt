package com.example.androidtechnote.recycler.custom_focus.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class MoviesRepository {
    private val api: TMDBMoviesApi //인터페이스 구현

    companion object{
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        const val TMDB_POPULAR_MOVIE_IMG_ORIGINAL = "https://image.tmdb.org/t/p/original"
        const val TMDB_POPULAR_MOVIE_IMG_W500 = "https://image.tmdb.org/t/p/w500"
    }

    init {
        /**
         * 2022/12/12 Gson -> Moshi Migration
         * */
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        /*val retrofit = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()*/

        api = retrofit.create(TMDBMoviesApi::class.java)
    }

    suspend fun getPopularMovies(page: Int = 1) : NetworkState<GetFocusItemResponse> {
        return try {
            val response = api.getPopularMovies(page = page)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                NetworkState.Success(body)
            } else {
                NetworkState.Error(response.code(), response.message())
            }
        }catch (e: HttpException){
            NetworkState.Error(e.code(), e.message ?: "")
        }catch (throwable: Throwable){
            NetworkState.Exception(throwable)
        }
    }
}

sealed class NetworkState<out T> {
    data class Success<out T>(val data: T): NetworkState<T>()
    data class Error(val code: Int, val message: String): NetworkState<Nothing>()
    data class Exception(val throwable: Throwable): NetworkState<Nothing>()
}