package com.example.androidtechnote.viewpager2

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

/**
 * ViewPager2 페이지 축소 변환
 * */
class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // 왼쪽 끝
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // 기본 슬라이드 전환을 수정하여 페이지 축소
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2

                    translationX = if (position < 0) horzMargin - vertMargin / 2 else horzMargin + vertMargin / 2


                    // 페이지 축소(MIN_SCALE과 1 사이)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // 페이지 크기에 따라 페이드
                    alpha = (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // 오른쪽 끝
                    alpha = 0f
                }
            }
        }
    }
}
