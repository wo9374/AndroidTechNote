package com.example.androidtechnote.recycler.custom_focus.data

import com.google.gson.annotations.SerializedName

data class FocusItem(
    @SerializedName("id") val id : Long,
    @SerializedName("title") val title : String,
    @SerializedName("overview") val overview : String,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("backdrop_path") val backdrop_path: String,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val vcount: Long,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("popularity") val prating: Float
) {}

data class GetFocusItemResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<FocusItem>,
    @SerializedName("totla_pages") val pages: Int,
    @SerializedName("totla_results") val results: Int
) {}