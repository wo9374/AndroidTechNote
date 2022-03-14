package com.example.androidtechnote.viewpager2

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityViewPager2Binding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.ceil

class DataPage(var color: Int, var title: String)

class ViewPager2Activity : AppCompatActivity() {
    lateinit var binding: ActivityViewPager2Binding

    lateinit var job : Job
    private var bannerPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager2)

        val list: ArrayList<DataPage> = ArrayList<DataPage>().let {
            it.apply {
                add(DataPage(android.R.color.holo_red_light, "1 Page"))
                add(DataPage(android.R.color.holo_orange_dark, "2 Page"))
                add(DataPage(android.R.color.holo_green_dark, "3 Page"))
                add(DataPage(android.R.color.holo_blue_light, "4 Page"))
                add(DataPage(android.R.color.holo_blue_bright, "5 Page"))
                add(DataPage(android.R.color.black, "6 Page"))
            }
        }

        //Int.MAX_VALUE 절반만 해줄시 index 가 중간 data를 보여주므로 - size/2 (소수점 올림)만큼 함
        bannerPosition= Int.MAX_VALUE / 2 - ceil(list.size.toDouble() / 2).toInt()

        viewPagerInit(binding.viewPager, list)

        //Indicator 무한스크롤 사용시엔 disable
        //binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.txtCurrentBanner.text = getString(R.string.viewpager2_banner, 1, list.size)
    }

    private fun viewPagerInit(viewPager: ViewPager2, list: ArrayList<DataPage>){
        viewPager.apply {
            adapter = ViewPagerAdapter(list)
            setCurrentItem(bannerPosition, false)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL //스크롤 manipulate

            setPageTransformer(ZoomOutPageTransformer())
            //setPageTransformer(DepthPageTransformer())

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

                    binding.txtCurrentBanner.text = getString(R.string.viewpager2_banner, (bannerPosition % list.size)+1, list.size)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        //뷰페이저에서 손 뗐을 때, 뷰페이저가 멈춰있을 때 자동 스크롤
                        ViewPager2.SCROLL_STATE_IDLE -> if (!job.isActive) scrollJobCreate()

                        //뷰페이저가 움직이는 중일 때 자동 스크롤 정지
                        ViewPager2.SCROLL_STATE_DRAGGING -> job.cancel()

                        //스크롤이 양쪽 끝가지 간 상태 // 무한스크롤 구현하였으면 안옴
                        ViewPager2.SCROLL_STATE_SETTLING -> {}
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        scrollJobCreate()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

    fun scrollJobCreate() {
        job = lifecycleScope.launchWhenResumed {
            delay(1500)
            binding.viewPager.setCurrentItemWithDuration(++bannerPosition, 1300)
        }
    }

    //Custom setCurrentItem() 애니메이션 시간 지정가능
    private fun ViewPager2.setCurrentItemWithDuration(
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
