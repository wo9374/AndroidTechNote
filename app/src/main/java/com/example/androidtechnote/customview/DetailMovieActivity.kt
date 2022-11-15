package com.example.androidtechnote.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityDetailMovieBinding

class DetailMovieActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailMovieBinding

    var movieAdapter = DetailMovieAdapter()

    private lateinit var backPath : String


    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        //setInit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_movie)

        binding.viewPager.apply {
            adapter = movieAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        backPath = intent.getStringExtra("data") ?: ""
        setInit()

        binding.image.setOnClickListener{
            supportFinishAfterTransition()
        }
    }

    private fun setInit(){
        Glide.with(binding.image)
            .load(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + backPath)
            .error(R.drawable.ic_launcher_background)
            .override(1400,800)
            .onlyRetrieveFromCache(true) //캐시에 저장된 이미지가 있을때만 불러오기
            .into(binding.image)
    }
}