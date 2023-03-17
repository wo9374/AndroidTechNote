package com.example.androidtechnote.calendar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.BuildConfig
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCalendarBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
                result.body()?.items?.forEach {
                    Log.e("Google Calendar Api", "${it.summary} ${it.startDate}")
                    val temp = it.summary
                    if (temp == "쉬는 날") temp.replace("쉬는 날", "대채 공휴일")
                    str += "$temp ${it.startDate}\n"
                }
                binding.title.text = str
            }
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