package com.example.androidtechnote.viewpager2

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityViewPager2Binding
import kotlin.math.ceil

class DataPage(var color: Int, var title: String)

class ViewPager2Activity : AppCompatActivity() {
    lateinit var binding: ActivityViewPager2Binding

    private var myHandler = MyHandler()
    private var bannerPosition = 0
    private val intervalTime = 2000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager2)

        val list: ArrayList<DataPage> = ArrayList<DataPage>().let {
            it.apply {
                add(DataPage(android.R.color.black, "1 Page"))
                add(DataPage(android.R.color.holo_red_light, "2 Page"))
                add(DataPage(android.R.color.holo_green_dark, "3 Page"))
                add(DataPage(android.R.color.holo_orange_dark, "4 Page"))
                add(DataPage(android.R.color.holo_blue_light, "5 Page"))
                add(DataPage(android.R.color.holo_blue_bright, "6 Page"))
            }
        }

        //Int.MAX_VALUE 절반만 해줄시 index 가 중간 data를 보여주므로 - size/2 (소수점 올림)만큼 함
        bannerPosition.run {
            Int.MAX_VALUE / 2 - ceil(list.size.toDouble() / 2).toInt()
        }

        viewPagerInit(binding.viewPager, list)
        //binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun viewPagerInit(viewPager: ViewPager2, list: ArrayList<DataPage>){
        viewPager.apply {
            adapter = ViewPagerAdapter(list)
            setCurrentItem(bannerPosition, false)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL //스크롤 manipulate

            //setPageTransformer(ZoomOutPageTransformer())
            setPageTransformer(DepthPageTransformer())

            /*offscreenPageLimit = 3
            setPageTransformer(SliderTransformer(3))
            binding.container.setPadding(30,30,30,30)
            binding.viewPager.apply {
                setPadding(0,0,120,0)
                clipChildren = false
                clipToPadding = false
            }*/

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                //사용자가 스크롤 했을때 position 수정
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bannerPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        //뷰페이저에서 손 뗐을 때, 뷰페이저가 멈춰있을 때 자동 스크롤
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)

                        //뷰페이저가 움직이는 중일 때 자동 스크롤 정지
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == 0) {
                binding.viewPager.setCurrentItemWithDuration(++bannerPosition, 1500) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 진행
            }
        }
    }

    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0)                        // 기존 핸들러 메시지 삭제
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop() {
        myHandler.removeMessages(0)    // 핸들러를 중지시킴
    }

    //Custom setCurrentItem() 애니메이션 시간 지정가능
    fun ViewPager2.setCurrentItemWithDuration(
        item: Int, duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width // ViewPager2 View 의 getWidth()에서 가져온 기본값
    ) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0

        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) { beginFakeDrag() }
            override fun onAnimationEnd(animation: Animator?) { endFakeDrag() }
            override fun onAnimationCancel(animation: Animator?) { /* Ignored */ }
            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */ }
        })

        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }
}



