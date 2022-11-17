package com.example.focusablelistview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes

class RoundedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    val path = Path()
    var cornerRadius: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.RoundedImageView){
            cornerRadius = getDimension(R.styleable.RoundedImageView_cornerRadius, 0F)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val corners = floatArrayOf(
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius
        )

        path.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            corners,
            Path.Direction.CW
        )

        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}