package com.example.androidtechnote.ktor

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestHelper {
    private val client: HttpClient = HttpClient(CIO) {

        //Gson 추가
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        //default Header 설정법
        install(DefaultRequest){
            header("Accept", "application/json")
            header("Content-type", "application/json")
        }
    }

    suspend fun requestKtorIo(): String =
        withContext(Dispatchers.IO) {
            val url = "https://cat-fact.herokuapp.com/facts/random"

            val response: HttpResponse = client.get(url)
            val responseStatus = response.status
            Log.d("HttpRequestHelper", "requestKtorIo: $responseStatus")

            if (responseStatus == HttpStatusCode.OK) {
                Log.d("HttpRequestHelper", "requestKtorResponse: ${response.readText()}") //받아온 json 로그
                response.readText()
            } else {
                "error: $responseStatus"
            }
        }


    /**헤더를 세팅해야 하는 경우 */
    companion object {
        val TAG: String = HttpRequestHelper::class.java.name
        const val APP_ID = "620c69cb21f118bb11be5d1c" //사용한 API는 dummyapi.io에 가면 개인 APP ID를 발급 가능
    }

    suspend fun requestKtorIoInDetail(): String =
        withContext(Dispatchers.IO){
            val response: HttpResponse = client.request("https://dummyapi.io/data/v1/user?limit=10") {
                method = HttpMethod.Get

                headers {
                    append(HttpHeaders.Accept, "text/html")
                    append("app-id", APP_ID)
                    append(HttpHeaders.UserAgent, "ktor client")
                }

                //쿠키 설정,  expire = 날짜를 설정하는 과정까지 포함
                cookie(
                    name = "user_name", value = "jaebum Lee", expires = GMTDate(
                        seconds = 0,
                        minutes = 0,
                        hours = 10,
                        dayOfMonth = 16,
                        month = Month.FEBRUARY,
                        year = 2022
                    )
                )
            }

            val responseStatus = response.status

            if (responseStatus == HttpStatusCode.OK) {
                Log.d("HttpRequestHelper", "requestKtorResponse: ${response.readText()}") //받아온 json 로그
                response.readText()
            } else {
                "error: $responseStatus"
            }
        }
}