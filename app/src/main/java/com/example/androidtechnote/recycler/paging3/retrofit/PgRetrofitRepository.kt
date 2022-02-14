package com.example.androidtechnote.recycler.paging3.retrofit

import androidx.paging.*
import kotlinx.coroutines.flow.Flow

class PgRetrofitRepository {
    val retrofit = RetrofitServiceImplementation

    fun getPagingRetrofit(): Flow<PagingData<ResponseParameter.Photo>> {
        val pageSize = 10

        return Pager(PagingConfig(pageSize = pageSize, enablePlaceholders = true)) {
            PgRetrofitSource(retrofit.service)
        }.flow
    }
}

class PgRetrofitSource(val retrofitService: RetrofitService) : PagingSource<Int, ResponseParameter.Photo>() {
    var count = 0
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResponseParameter.Photo> {
        try {
            //키 없을시 1로 지정해 1페이지 요청
            val next = params.key ?: 1
            val hashMap = HashMap<String, String>()
            hashMap["page"] = next.toString()
            hashMap["size"] = params.loadSize.toString()


            val call = retrofitService.stringCall(hashMap)

            count += params.loadSize
            return LoadResult.Page(
                data = call.body()?.photo!!,
                prevKey = null, //null 시  forward 전용
                nextKey = if (call.body()?.page_info?.total_elements!! < count) null else next + 1
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResponseParameter.Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
