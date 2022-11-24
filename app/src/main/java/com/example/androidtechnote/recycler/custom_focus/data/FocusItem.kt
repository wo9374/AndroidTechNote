package com.example.androidtechnote.recycler.custom_focus.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 2022/12/12 Gson -> Moshi Migration
 * */

@JsonClass(generateAdapter = true)
data class FocusItem(
    @Json(name = "id") val id : Long,
    @Json(name = "title") val title : String,
    @Json(name = "overview") val overview : String,
    @Json(name = "poster_path") val poster_path: String,
    @Json(name = "backdrop_path") val backdrop_path: String,
    @Json(name = "vote_average") val rating: Float,
    @Json(name = "vote_count") val vcount: Long,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "popularity") val prating: Float
) {}

@JsonClass(generateAdapter = true)
data class GetFocusItemResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val movies: List<FocusItem>,
    @Json(name = "total_pages") val pages: Int,
    @Json(name = "total_results") val results: Int
) {}

/*data class FocusItem(
    @SerializedName("id") val id : Long,
    @SerializedName("title") val title : String,
    @SerializedName("overview") val overview : String,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("backdrop_path") val backdrop_path: String,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val vcount: Long,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("popularity") val prating: Float
) {}*/

/*
data class GetFocusItemResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<FocusItem>,
    @SerializedName("total_pages") val pages: Int,
    @SerializedName("total_results") val results: Int
) {}*/
