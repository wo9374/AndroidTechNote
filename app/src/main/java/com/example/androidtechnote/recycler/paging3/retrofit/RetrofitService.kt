package com.example.androidtechnote.recycler.paging3.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface RetrofitService {
    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("login/photo_info")
    suspend fun stringCall(@QueryMap requestParameter: HashMap<String, String>): Response<ResponseParameter>
    //suspend 지정시 서버 response 시 처리해야하는 Adapter Data Set 을 구현할 필요X
}