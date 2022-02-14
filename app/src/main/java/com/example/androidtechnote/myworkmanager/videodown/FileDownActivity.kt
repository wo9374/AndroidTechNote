package com.example.androidtechnote.myworkmanager.videodown

import android.Manifest.permission
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityFileDownBinding

class FileDownActivity : AppCompatActivity() {
    lateinit var binding: ActivityFileDownBinding

    val urlList = arrayListOf(
        "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4",
        "https://filesamples.com/samples/video/mp4/sample_1280x720.mp4",
        "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
    )

    val workManager = WorkManager.getInstance()

    companion object {
        const val REQUEST_PERMISSION_CODE = 100
        const val Progress = "Progress"
        const val FilePath = "FilePath"

        var count = 0
    }

    lateinit var downTask1 : OneTimeWorkRequest
    lateinit var downTask2 : OneTimeWorkRequest
    lateinit var downTask3 : OneTimeWorkRequest

    val playTask1 = OneTimeWorkRequestBuilder<PlayVideoWork>().build()
    val playTask2 = OneTimeWorkRequestBuilder<PlayVideoWork>().build()
    val playTask3 = OneTimeWorkRequestBuilder<PlayVideoWork>().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_down)

        checkPermission()

        binding.allBtn.setOnClickListener {
            setWorkManager()
        }
    }

    fun setWorkManager(){
        /*val chain1 = workManager.beginWith(downTask1).then(playTask)
        val chain2 = workManager.beginWith(downTask2).then(playTask)
        val chain3 = WorkContinuation.combine(listOf(chain1,chain2))
        chain3.enqueue()*/

        val inputData = Data.Builder().putString("inputUrl", urlList[0]).build()
        val inputData1 = Data.Builder().putString("inputUrl", urlList[1]).build()
        val inputData2 = Data.Builder().putString("inputUrl", urlList[2]).build()

        downTask1 = OneTimeWorkRequestBuilder<DownLoadFileWork>()
            .setInputData(inputData)
            .build()

        downTask2 = OneTimeWorkRequestBuilder<DownLoadFileWork>()
            .setInputData(inputData1)
            .build()

        downTask3 = OneTimeWorkRequestBuilder<DownLoadFileWork>()
            .setInputData(inputData2)
            .build()

        workManager
            .beginWith(downTask1)
            //.then(playTask1)
            .then(downTask2)
            //.then(playTask2)
            .then(downTask3)
            .enqueue()

        //workManager.cancelWorkById(downTask1.id)


        /*workManager.beginUniqueWork("sync", ExistingWorkPolicy.KEEP, downTask1).enqueue()*/


        workManager.getWorkInfoByIdLiveData(downTask1.id).observe(this, downWork1Observer)
        workManager.getWorkInfoByIdLiveData(downTask2.id).observe(this, downWork2Observer)
        workManager.getWorkInfoByIdLiveData(downTask3.id).observe(this, downWork3Observer)
    }

    private var downWork1Observer =  Observer<WorkInfo> {
        it?.let {
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    binding.txt1.text = it.state.toString()

                    val progressValue = it.progress.getInt(Progress, 0)
                    binding.progress1.progress = progressValue
                }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("SeminaTest", "work_info1 - SUCCEEDED")

                    //workManager.beginUniqueWork("sync", ExistingWorkPolicy.KEEP, downTask2).enqueue()
                    workManager.beginUniqueWork("sync", ExistingWorkPolicy.REPLACE, downTask2).enqueue()

                    binding.txt1.text = it.state.toString()

                    val uri = Uri.parse(it.outputData.getString(FilePath).toString())
                    binding.vv.setVideoURI(uri)
                    binding.vv.start()
                    binding.vv.setOnCompletionListener {
                        /*val inputData2 = Data.Builder().putString("inputUrl", urlList[2]).build()
                        downTask3 = OneTimeWorkRequestBuilder<DownLoadFileWork>()
                            .setInputData(inputData2)
                            .build()
                        workManager.enqueue(downTask3)*/
                    }
                    //workManager.cancelWorkById(downTask2.id)
                }
                else -> { //ENQUEUED,CANCELLED,FAILED,BLOCKED
                    binding.txt1.text = it.state.toString()
                }
            }
        }
    }

    private var downWork2Observer =  Observer<WorkInfo> {
        it?.let {
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    binding.txt2.text = it.state.toString()

                    val progressValue = it.progress.getInt(Progress, 0)
                    binding.progress2.progress = progressValue
                }
                WorkInfo.State.SUCCEEDED -> {
                    binding.txt2.text = it.state.toString()
                    Log.d("SeminaTest", "work_info2 - SUCCEEDED")

                    val uri = Uri.parse(it.outputData.getString(FilePath).toString())
                    binding.vv.setVideoURI(uri)
                    binding.vv.start()
                }
                else -> { //ENQUEUED,CANCELLED,FAILED,BLOCKED
                    binding.txt2.text = it.state.toString()
                }
            }
        }
    }

    private var downWork3Observer =  Observer<WorkInfo> {
        it?.let {
            when (it.state) {
                WorkInfo.State.RUNNING -> {
                    binding.txt3.text = it.state.toString()

                    val progressValue = it.progress.getInt(Progress, 0)
                    binding.progress3.progress = progressValue
                }
                WorkInfo.State.SUCCEEDED -> {
                    binding.txt3.text = it.state.toString()
                    Log.d("SeminaTest", "work_info3 - SUCCEEDED")

                    val uri = Uri.parse(it.outputData.getString(FilePath).toString())
                    binding.vv.setVideoURI(uri)
                    binding.vv.start()
                }
                else -> { //ENQUEUED,CANCELLED,FAILED,BLOCKED
                    binding.txt3.text = it.state.toString()
                }
            }
        }
    }

    private var playWorkObserver =  Observer<WorkInfo> {
        it?.let {
            when (it.state) {
                WorkInfo.State.SUCCEEDED -> {
                   Log.d("PlayWork","Succeeded")
                }
                else -> { //RUNNING,ENQUEUED,CANCELLED,FAILED,BLOCKED

                }
            }
        }
    }

    fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showToast("권한 동의되었습니다.")
        } else {
            requestPermission()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission.WRITE_EXTERNAL_STORAGE
                )) {
                //거부했을 경우
                showToast("기능 사용을 위한 동의가 필요합니다.1")
            } else {
                showToast("기능 사용을 위한 동의가 필요합니다.2")
            }
        }
    }

    //Intent i = new Intent(Intent.ACTION_VIEW)
    //    Uri uri = Uri.parse("https://www.youtube.com/embed/EyR9lx92x9A?rel=0")
    //    i.setDataAndType(uri, "video/*"); startActivity(i)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
    }

    fun showToast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_LONG)
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.cancelAllWork()
    }

    interface WorkerObserver{
        fun onSuccess(filePath: String)
        fun onFail()
    }
}

