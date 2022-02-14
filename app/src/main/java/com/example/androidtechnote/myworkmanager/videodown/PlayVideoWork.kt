package com.example.androidtechnote.myworkmanager.videodown

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class PlayVideoWork(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        val str = inputData.getString(FileDownActivity.FilePath).toString()
        if (!str.isNullOrEmpty()){
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(str)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInmillisec = time!!.toLong()

            delay(timeInmillisec)

            return Result.success()
            Log.d("playWork","Success")
        }else{
            return Result.failure()
            Log.d("playWork","failure")
        }
        /*val retriever = MediaMetadataRetriever()
        retriever.setDataSource(inputData.getString(DownLoadFileWork1.FilePath).toString())
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInmillisec = time.toLong()
        val duration = timeInmillisec / 1000
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = duration - (hours * 3600 + minutes * 60)*/
    }
}