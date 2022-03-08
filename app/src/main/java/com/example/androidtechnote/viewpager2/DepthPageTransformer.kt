package com.example.androidtechnote.viewpager2

import android.view.View
import androidx.viewpager2.widget.ViewPager2

private const val MIN_SCALE = 0.75f

class DepthPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // 왼쪽 페이지로 이동할 때 기본 슬라이드 전환 사용
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // 페이지 페이드 아웃
                    alpha = 1 - position

                    // 기본 슬라이드 전환 대응
                    translationX = pageWidth * -position
                    // 왼쪽 페이지 뒤로 이동
                    translationZ = -1f

                    // 페이지 축소(MIN_SCALE과 1 사이)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    alpha = 0f
                }
            }
        }
    }
}