package com.example.androidtechnote.calendar

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ChineseCalendar
import android.icu.util.GregorianCalendar
import android.icu.util.TimeZone
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.BuildConfig
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCalendarBinding
import com.usingsky.calendar.KoreanLunarCalendar
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class CalendarActivity : AppCompatActivity() {
    lateinit var binding : ActivityCalendarBinding

    companion object{
        const val baseUrl = "https://www.googleapis.com"
        const val endPoint = "calendar/v3/calendars/ko.south_korea.official%23holiday%40group.v.calendar.google.com/events"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)

        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()) //Gson 라이브러리 사용
            .client(client)
            .build()

        val service = retrofit.create(GoogleCalendarService::class.java)
        lifecycleScope.launch {
            val result = service.getHoliday(key = BuildConfig.GOOGLE_API_KEY, timeMin = "2023-01-01T00:00:00Z", timeMax = "2024-01-01T00:00:00Z")

            if (result.isSuccessful){
                var str =""

                val calendar = KoreanLunarCalendar.getInstance()
                result.body()?.items?.forEach {
                    val temp = it.summary
                    if (temp == "쉬는 날") temp.replace("쉬는 날", "대체 공휴일")

                    val day = it.startDate.date.split("-").map { trimStr ->
                        trimStr.toInt()
                    }
                    calendar.apply {
                        setSolarDate(day[0],day[1],day[2])
                        str += "$temp\n양력:${it.startDate.date} 음력:$lunarYear-$lunarMonth-$lunarDay\n"

                        Log.e("Google Calendar Api", "${it.summary}\t\t 양력:${it.startDate.date} 음력:$lunarYear-$lunarMonth-$lunarDay")
                    }
                }


                binding.title.text = str
            }
        }
    }

    fun convertSolarToLunar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val solar = Calendar.getInstance(timeZone)
            val lunar = ChineseCalendar(timeZone)
            lunar.timeInMillis = solar.timeInMillis

            val year = lunar.get(ChineseCalendar.YEAR)
            val month = lunar.get(ChineseCalendar.MONTH)
            val day = lunar.get(ChineseCalendar.DAY_OF_MONTH)

            // 음력 날짜 출력
            Log.d("Lunar Date", "${year}년 ${month + 1}월 ${day}일")
        } else {

        }

    }
}

interface GoogleCalendarService{
    @GET(CalendarActivity.endPoint)
    suspend fun getHoliday(
        @Query("key") key : String,
        @Query("orderBy") orderBy: String = "startTime",
        @Query("singleEvents") singleEvents: String = "true",
        @Query("timeMin") timeMin: String, //"2022-03-17T00:00:00Z"
        @Query("timeMax") timeMax: String, //"2023-03-17T00:00:00Z"
    ): Response<Holiday>
}