package com.example.androidtechnote.myworkmanager.videodown

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownLoadFileWork(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
            FileDownActivity.count += 1

            val url = inputData.getString("inputUrl")!!
            val imagePath = downloadVideoFromURL(url)

            return if (imagePath.isNotEmpty()) {
                Result.success(workDataOf(FileDownActivity.FilePath to imagePath))
            } else {
                Result.failure()
            }
    }

    private suspend fun downloadVideoFromURL(videoUrl : String) : String {
        val pathFile : String
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        val url = URL(videoUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.connect()

        val responseCode = conn.responseCode

        // 200 성공일 때
        if (responseCode == HttpURLConnection.HTTP_OK) {
            var fileName = ""
            val disposition = conn.getHeaderField("Content-Disposition")
            val contentType = conn.contentType

            // 일반적으로 Content-Disposition 헤더에 있지만 없을 경우 url 에서 추출
            if (disposition != null) {
                val target = "filename="
                val index = disposition.indexOf(target)
                if (index != -1) {
                    fileName = disposition.substring(index + target.length + 1)
                }
            } else {
                fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1)
            }

            Log.d("FileDownActivity", "Content-Type = $contentType")
            Log.d("FileDownActivity", "Content-Disposition = $disposition")
            Log.d("FileDownActivity", "fileName = $fileName")

            val pathFolder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() + "/semina"
            pathFile = "${pathFolder}/$fileName"

            val futureStudioIconFile = File(pathFolder)
            if (!futureStudioIconFile.exists()) { //폴더없으면 생성
                futureStudioIconFile.mkdirs()
            }

            val fileLength: Int = conn.contentLength
            Log.d("FileDownActivity", "Content-Length:" + conn.contentLength)

            //다운로드 파일
            inputStream = BufferedInputStream(url.openStream()) //느리기때문에 BufferedInputStream 사용
            outputStream = FileOutputStream(pathFile)

            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            while (inputStream.read(data).also { count = it } != -1) {
                total += count

                //서버에서 Header 값에 content-length 값을 받아와야하는데 아무 서버에서 받아오는지라.. 임시로 받아오는 바이트만 표시
                setProgress(workDataOf(FileDownActivity.Progress to count))
                /*progressUpdate = workDataOf(Progress to (total * 100 / fileLength).toInt())
                setProgress(progressUpdate)
                Log.d("FileDownActivity", "파일스트림 ${(total * 100 / fileLength).toInt()}")*/

                outputStream.write(data, 0, count)
            }

            setProgress(workDataOf(FileDownActivity.Progress to 100))

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            Log.d("FileDownActivity", "파일 다운 성공")

            conn.disconnect()
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        } else {
            Log.d("FileDownActivity", "서버오류 HTTP code: $responseCode")

            pathFile = ""

            conn.disconnect()
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }

        Log.d("SeminaTest", "downTask${FileDownActivity.count}")
        return pathFile
    }
}