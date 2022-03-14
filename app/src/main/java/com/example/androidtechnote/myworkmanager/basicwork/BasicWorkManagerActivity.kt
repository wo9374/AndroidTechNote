package com.example.androidtechnote.myworkmanager.basicwork

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.work.*
import com.example.androidtechnote.R
import java.util.concurrent.TimeUnit

class BasicWorkManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_work_manager)
        val startSimpleWorkerBtn : Button = findViewById(R.id.button1)
        val cancelWorkerBtn : Button = findViewById(R.id.button2)
        val simpleWorkStatusText : TextView = findViewById(R.id.textView)

        //Data 전달가능, inputData
        val inputData = Data.Builder().putInt("input", 5).build()

        //제약조건을 걸어 사용가능 Constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true) // 디바이스가 충전 중일때만 가능
            //.setTriggerContentMaxDelay(9,TimeUnit.MINUTES) //콘텐츠 변경이 처음 감지된 후부터 작업이 예약될 때까지 허용되는 최대 총 지연 시간(밀리초)을 설정
            //.setRequiresDeviceIdle(true)                   //작업 요청을 실행하기 위해 디바이스를 유휴 상태로 둘지 여부 설정
            .build()



        val repeatRequest = PeriodicWorkRequestBuilder<SimpleWorker>(
            1, TimeUnit.HOURS,
            10, TimeUnit.MINUTES
        ).build()

        val workManager = WorkManager.getInstance()    //WorkManager 객체

        startSimpleWorkerBtn.setOnClickListener {
            /*val simpleRequest = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
                .setInputData(inputData)
                //.setConstraints(constraints)
                .build()
            val simpleRequest2 = OneTimeWorkRequest.Builder(SimpleWorker2::class.java).build()
            val simpleRequest3 = OneTimeWorkRequest.Builder(SimpleWorker3::class.java).build()

            val workList = mutableListOf<OneTimeWorkRequest>(simpleRequest2,simpleRequest3)

            workManager
                .beginWith(workList)
                .then(simpleRequest)
                .enqueue()

            //workManager.enqueue(simpleRequest) //WorkManager에 WorkRequest 추가

            //WorkInfo 로 진행상태 확인가능
            val workInfo = workManager.getWorkInfoByIdLiveData(simpleRequest.id)
            workInfo.observe(this, Observer<WorkInfo> { info ->
                val workFinished = info.state.isFinished

                info.outputData // Work에서 온 outputData 사용가능

                simpleWorkStatusText.text = when (info.state) {
                    WorkInfo.State.SUCCEEDED,
                    WorkInfo.State.FAILED -> { "세미나 work 진행상태: ${info.state}\n진행성공유무: $workFinished" }
                    else -> { "세미나 work 진행상태: ${info.state}\n진행성공유무: $workFinished" }
                    //State 태스크의 상태 RUNNING,SUCCEEDED,FAILED,ENQUEUED,BLOCKEDCANCELLED
                }
            })*/
            val simpleRequest = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
                .addTag("test1")
                //.setInputData(inputData)
                .build()

            workManager
                .beginWith(simpleRequest)
                .enqueue()

            val workInfo = workManager.getWorkInfosByTagLiveData("test1").map { it[0] }
            workInfo.observe(this, Observer {
                val workFinished =it.state.isFinished

                Log.d("워크테스트", "${it.state}")

                simpleWorkStatusText.text = when(it.state){
                    WorkInfo.State.SUCCEEDED,
                    WorkInfo.State.FAILED -> { "세미나 work 진행상태: ${it.state}\n진행성공유무: $workFinished" }
                    else -> {
                        "세미나 work 진행상태: ${it.state}\n진행성공유무: $workFinished"
                    }
                }
            })
        }

        cancelWorkerBtn.setOnClickListener {
            //workManager.cancelWorkById(simpleRequest.id)
        }
    }

    class SimpleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            println("\n세미나Task1")
            val result = inputData.getInt("input",0) + 11

            //Data 전달가능, outputData
            val outputData = Data.Builder().putInt("output", result).build()

            SystemClock.sleep(3000)

            return Result.success()
        }
    }

    class SimpleWorker2(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            print("세미나Task2,")
            SystemClock.sleep(3000)
            return Result.success()
        }
    }

    class SimpleWorker3(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            print("세미나Task3")
            SystemClock.sleep(3000)
            return Result.success()
        }
    }
}