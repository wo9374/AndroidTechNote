package com.example.androidtechnote.exoplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityExoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Util


class ExoActivity : AppCompatActivity() {
    lateinit var binding :ActivityExoBinding
    lateinit var exoPlayer: ExoPlayer

    private var playWhenReady = true // 재생,일시정지 정보
    private var currentWindow = 0 // 현재 윈도우 지수
    private var playbackPosition = 0L // 현재 재생 위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo)
    }

    fun initializePlayer(){
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer

        //1개 영상 실행
        val mediaItem = MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        exoPlayer.setMediaItem(mediaItem)
        //다음영상 추가
        val secondItem = MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        exoPlayer.addMediaItem(secondItem)

        // releasePlayer 에서 저장한 상태 정보를 초기화 중에 플레이어에게 제공 하는 부분
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.seekTo(currentWindow, playbackPosition)
        exoPlayer.prepare()

        /**
         * 모든 Http Url 접근 허용
         * Manifest android:usesCleartextTraffic="true"
         * */

        /**
         * 일부 Http Url 접근 허용
         * network_security xml 파일생성해 예외 사이트들을 넣어주면 됌
         * */
    }


    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    fun releasePlayer(){
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentMediaItemIndex
        playWhenReady = exoPlayer.playWhenReady

        exoPlayer.release()

        binding.playerView.player = null
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24)
            initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24)
            initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT >= 24)
            releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT < 24)
            releasePlayer()
    }
}