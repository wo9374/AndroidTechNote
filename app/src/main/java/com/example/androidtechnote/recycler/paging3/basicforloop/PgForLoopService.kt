package com.example.androidtechnote.recycler.paging3.basicforloop

//임시 For 문 사용 Data,Service Class
data class ExamSample(
    val data: List<String>,
    val page: Int
)

class PgForLoopService {
    fun getPagingData(page: Int) : ExamSample {
        val result = mutableListOf<String>()

        val start = page * 10

        for (i in start until start + 10) {
            result.add("$i item")
        }

        return ExamSample(data = result, page = page + 1)
    }
}