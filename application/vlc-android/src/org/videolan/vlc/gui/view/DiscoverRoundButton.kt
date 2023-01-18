/*
 * ************************************************************************
 *  DiscoverRoundButton.kt
 * *************************************************************************
 * Copyright © 2023 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */


package org.videolan.vlc.gui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Paint.Style
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import org.videolan.tools.dp
import org.videolan.tools.getColorFromAttr
import org.videolan.tools.setGone
import org.videolan.vlc.BuildConfig
import org.videolan.vlc.R


class DiscoverRoundButton : FrameLayout {
    private var iconOnly: Boolean = true
    private var progressColor: Int = Color.WHITE
    private var progressBackgroundColor: Int = Color.WHITE
    private val textView: TextView by lazy {
        findViewById(R.id.text)
    }
    private val imageView: ImageView by lazy {
        findViewById(R.id.image)
    }

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    var progress = 0F
        set(value) {
            if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "new progress: $value")
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
        initAttributes(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
        initAttributes(attrs, defStyle)
    }

    private fun initAttributes(attrs: AttributeSet, defStyle: Int) {
        attrs.let {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DiscoverRoundButton, 0, defStyle)
            try {

                val drawable = a.getDrawable(R.styleable.DiscoverRoundButton_drb_button_icon)
                iconOnly = a.getBoolean(R.styleable.DiscoverRoundButton_drb_icon, true)
                imageView.setImageDrawable(drawable)
                if (iconOnly) {
                    textView.setGone()
                    (imageView.layoutParams as ConstraintLayout.LayoutParams).marginStart = 0
                }

                progressColor = context.getColorFromAttr(R.attr.colorPrimary)
                progressBackgroundColor = context.getColorFromAttr(R.attr.colorControlNormal)

            } catch (e: Exception) {
                Log.w("", e.message, e)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null || !isEnabled) {
            super.onDraw(canvas)
            return
        }

        val offset = 3.dp

        //icon only. We draw the outline as a simple circle
        if (iconOnly) {
            val size = width.toFloat() - (2 * offset)
            val strokeHalfWidth = offset.toFloat() / 2
            paint.isAntiAlias = true
            paint.strokeWidth = strokeHalfWidth * 2
            paint.style = Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = progressBackgroundColor
            canvas.drawCircleOffset(offset.toFloat(),size / 2, size / 2, (size / 2) - strokeHalfWidth, paint)
            paint.color = progressColor
            canvas.drawArcOffset(offset.toFloat(), RectF(strokeHalfWidth, strokeHalfWidth, size - strokeHalfWidth, size - strokeHalfWidth), -90F, 360F * progress, false, paint)
            super.onDraw(canvas)
            return
        }

        val strokeHalfWidth = 1.dp.toFloat() * 1.5F
        paint.strokeWidth = strokeHalfWidth * 2
        val realWidth = width.toFloat() - (offset * 2)
        val progressHeight = height.toFloat() - (offset * 2)
        val halfHeight = progressHeight / 2


        /**
         * Here are the background segment description
         *      1
         * 4 ╭────╮ 3
         *   ╰────╯
         *      2
         * 1: top bar
         * 2: bottom bar
         * 3: right semi circle
         * 4: left semi circle
         */


        paint.color = progressBackgroundColor

        //draw background
        canvas.drawLineOffset(offset.toFloat(), halfHeight + strokeHalfWidth, strokeHalfWidth, realWidth - halfHeight - strokeHalfWidth, strokeHalfWidth, paint)
        canvas.drawLineOffset(offset.toFloat(), halfHeight + strokeHalfWidth, progressHeight - strokeHalfWidth, realWidth - halfHeight - strokeHalfWidth, progressHeight - strokeHalfWidth, paint)

        canvas.drawArcOffset(offset.toFloat(), RectF(strokeHalfWidth, strokeHalfWidth, progressHeight - strokeHalfWidth, progressHeight - strokeHalfWidth), -90F, -180F, false, paint)
        canvas.drawArcOffset(offset.toFloat(), RectF(realWidth - progressHeight, strokeHalfWidth, realWidth - strokeHalfWidth, progressHeight - strokeHalfWidth), -90F, 180F, false, paint)




        paint.color = progressColor

        if (progress > 0F) {
            //draw progress
            val circleLength = progressHeight * Math.PI
            val pathLength = ((realWidth - progressHeight) * 2) + circleLength
            var remainingProgressLength = pathLength * progress

            /**
             * Here are the segment description
             *    5  1
             * 4 ╭────╮ 2
             *   ╰────╯
             *      3
             * 1: first half of the top bar
             * 2: right semi circle
             * 3: full bottom bar
             * 4: left semi circle
             * 5: other half of the top bar
             */

            //first segment
            val firstSegmentLength = ((realWidth / 2) - halfHeight).coerceAtMost(remainingProgressLength.toFloat())
            canvas.drawLineOffset(offset.toFloat(), realWidth / 2, strokeHalfWidth, (realWidth / 2) + firstSegmentLength, strokeHalfWidth, paint)
            remainingProgressLength -= firstSegmentLength

            //second segment (right semi circle)
            if (remainingProgressLength > 1) {
                val secondSegmentLength = (circleLength / 2).coerceAtMost(remainingProgressLength)
                val secondSegmentAngle = 180F * (secondSegmentLength / (circleLength / 2))
                canvas.drawArcOffset(offset.toFloat(), RectF(realWidth - progressHeight, strokeHalfWidth, realWidth - strokeHalfWidth, progressHeight - strokeHalfWidth), -90F, secondSegmentAngle.toFloat(), false, paint)
                remainingProgressLength -= secondSegmentLength
            }

            //third segment : (bottom bar)
            if (remainingProgressLength > 1) {
                val thirdSegmentLength = (realWidth - progressHeight).coerceAtMost(remainingProgressLength.toFloat())
                canvas.drawLineOffset(offset.toFloat(), realWidth - halfHeight - strokeHalfWidth, progressHeight - strokeHalfWidth, realWidth - halfHeight - strokeHalfWidth - thirdSegmentLength, progressHeight - strokeHalfWidth, paint)
                remainingProgressLength -= thirdSegmentLength
            }

            //fourth segment (left semi circle)
            if (remainingProgressLength > 1) {
                val fourthSegmentLength = (circleLength / 2).coerceAtMost(remainingProgressLength)
                val fourthSegmentAngle = 180F * (fourthSegmentLength / (circleLength / 2))
                canvas.drawArcOffset(offset.toFloat(), RectF(strokeHalfWidth, strokeHalfWidth, progressHeight - strokeHalfWidth, progressHeight - strokeHalfWidth), 90F, fourthSegmentAngle.toFloat(), false, paint)
                remainingProgressLength -= fourthSegmentLength
            }

            //fifth (and last) segment
            if (remainingProgressLength > 1) {
                val fifthSegmentLength = ((realWidth / 2) - halfHeight).coerceAtMost(remainingProgressLength.toFloat())
                canvas.drawLineOffset(offset.toFloat(), halfHeight + strokeHalfWidth, strokeHalfWidth, (halfHeight + strokeHalfWidth) + fifthSegmentLength, strokeHalfWidth, paint)

                remainingProgressLength -= fifthSegmentLength
            }
        }
        super.onDraw(canvas)
    }


    private fun initialize() {
        LayoutInflater.from(context).inflate(R.layout.discover_round_button, this, true)
        setWillNotDraw(false)
    }

    fun setText(text: String) {
        textView.text = text
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setColor(paletteColor: Int) {
        progressColor = paletteColor
        val valueOf = ColorStateList.valueOf(paletteColor)
        backgroundTintList = valueOf
    }
}

fun Canvas.drawLineOffset(offset: Float, startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
    drawLine(startX + offset, startY + offset, stopX + offset, stopY + offset, paint)
}

fun Canvas.drawArcOffset(offset: Float, oval: RectF, startAngle: Float, sweepAngle: Float, useCenter: Boolean, paint: Paint) {
    drawArc(RectF(oval.left + offset, oval.top + offset, oval.right + offset, oval.bottom + offset), startAngle, sweepAngle, useCenter, paint)
}

fun Canvas.drawCircleOffset(offset: Float, cx: Float, cy: Float, radius: Float, paint: Paint) {
    drawCircle(cx + offset, cy + offset, radius, paint)
}



@BindingAdapter("app:roundButtonText")
fun setRoundButtonText(view: DiscoverRoundButton, text: String) {
    view.setText(text)
}

@BindingAdapter("app:roundButtonProgress")
fun setRoundButtonProgress(view: DiscoverRoundButton, progress: Float) {
    view.progress = progress
}
