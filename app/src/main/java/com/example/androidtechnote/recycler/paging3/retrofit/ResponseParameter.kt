package com.example.androidtechnote.recycler.paging3.retrofit

import com.google.gson.annotations.SerializedName

data class ResponseParameter(
    @SerializedName("page_info")
    val page_info: PageInfo,
    @SerializedName("photo")
    val photo: ArrayList<Photo>
){
    data class PageInfo(
        @SerializedName("page")
        val page: Int,
        @SerializedName("page_size")
        val page_size: String,
        @SerializedName("total_elements")
        val total_elements: Int
    )
    data class Photo(
        @SerializedName("id")
        val id: Int,
        @SerializedName("user_id")
        val user_id: String,
        @SerializedName("user_pw")
        val user_pw: String
    )
}