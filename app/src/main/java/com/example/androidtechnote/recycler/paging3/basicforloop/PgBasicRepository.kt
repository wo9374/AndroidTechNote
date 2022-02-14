package com.example.androidtechnote.recycler.paging3.basicforloop

import androidx.paging.*
import kotlinx.coroutines.flow.Flow

class PgBasicRepository(private val service : PgForLoopService) {
    fun getPagingData() : Flow<PagingData<String>>{
        return Pager(PagingConfig(pageSize = 10)){
            PgBasicSource(service)
        }.flow
    }
}

class PgBasicSource (private val service: PgForLoopService) : PagingSource<Int, String>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        return try {
            val next = params.key ?: 0
            val response = service.getPagingData(next)

            LoadResult.Page(
                data = response.data,
                prevKey = if (next == 0) null else next - 1,
                nextKey = next + 1
            )
        } catch (e: Exception) { LoadResult.Error(e) }
    }

    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
