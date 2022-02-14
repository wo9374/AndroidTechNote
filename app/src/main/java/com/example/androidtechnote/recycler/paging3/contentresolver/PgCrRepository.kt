package com.example.androidtechnote.recycler.paging3.contentresolver

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.flow.Flow

data class PhotoItem(
    val imgDataPath : String,
    val index : Int,
    var onOffCheck: Boolean,    //롱 클릭 판별 변수
    var checkBoolean: Boolean   //선택 판별 변수
    )

class PgCrRepository(context: Context) {
    val contentResolver = context.contentResolver

    fun getPagingData(): Flow<PagingData<PhotoItem>> {
        return Pager(PagingConfig(pageSize = 10, initialLoadSize = 20)) {
            PagingDataSource(contentResolver)
        }.flow
    }
}

class PagingDataSource(val contentResolver: ContentResolver) : PagingSource<Int, PhotoItem>() {
    var initialLoadSize: Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        return try {
            val next = params.key ?: 1  //키 없을시 1로 지정해 1페이지 요청

            if (params.key == null) initialLoadSize = params.loadSize

            val offsetCalc = {
                if (next == 2) initialLoadSize
                else ((next - 1) * params.loadSize) + (initialLoadSize - params.loadSize)
            }
            val offset = offsetCalc.invoke()

            val response = getImages(params.loadSize, offset)
            val count = response.size

            LoadResult.Page(
                data = response,
                prevKey = null, //null 시  forward 전용
                nextKey = if (count < params.loadSize) null else next + 1
                //페이지가 로드되지 않으면 데이터의 끝을 의미한다고 가정
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            Log.d("세미나 refresh키", "${anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)}")
            //Adapter refresh 하니 호출
        }
    }

    private fun getImages(limit: Int, offset: Int): MutableList<PhotoItem> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )

        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val sortOrder = "$orderBy DESC LIMIT $limit OFFSET $offset"

        val imageCursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,// scheme 방식의 원하는 데이터를 가져오기 위한 정해진 주소
            projection,                                  // 가져올 컬럼 이름 목록, null이면 모든 컬럼
            null,                               // where 절에 해당하는 내용
            null,                            // selecton 에서 ?로 표시한 곳에 들어갈 데이터
            sortOrder                                    // 정렬을 위한 order by 구문
        )

        val photoList: MutableList<PhotoItem> = mutableListOf()

        if (imageCursor != null) {
            imageCursor.moveToFirst()

            var count = 0
            while (!imageCursor.isAfterLast) {
                val imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val imgDataPath = imageCursor.getString(imgData)
                photoList.add(PhotoItem(imgDataPath, count, PgCrActivity.checkBoxOnOff, false))
                imageCursor.moveToNext()
                count++
            }
            imageCursor.close()
        }
        return photoList
    }
}
