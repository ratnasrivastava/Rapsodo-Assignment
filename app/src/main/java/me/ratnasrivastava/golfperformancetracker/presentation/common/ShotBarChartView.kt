package me.ratnasrivastava.golfperformancetracker.presentation.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator

class ShotBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Bar(val label: String, val value: Float)

    private var bars: List<Bar> = emptyList()
    private var maxValue: Float = 0f

    private var barGrowFraction: Float = 1f
    private var animator: ValueAnimator? = null

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sp(11f)
        textAlign = Paint.Align.CENTER
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sp(11f)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val barRect = RectF()
    private val cornerRadius = dp(6f)

    init {
        val primary = themeColor(com.google.android.material.R.attr.colorPrimary)
        val track = themeColor(com.google.android.material.R.attr.colorSurfaceVariant)
        val onSurface = themeColor(com.google.android.material.R.attr.colorOnSurface)
        val onSurfaceVariant = themeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)

        barPaint.color = primary
        trackPaint.color = track
        valuePaint.color = onSurface
        labelPaint.color = onSurfaceVariant
    }

    fun setBars(newBars: List<Bar>) {
        bars = newBars
        maxValue = newBars.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f

        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                barGrowFraction = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // Fixed, comfortable chart height.
        val desiredHeight = dp(180f).toInt()
        setMeasuredDimension(width, desiredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bars.isEmpty()) return

        val labelArea = dp(28f)       // space at bottom for labels
        val valueArea = dp(18f)       // space at top for value text
        val chartTop = paddingTop + valueArea
        val chartBottom = height - paddingBottom - labelArea
        val chartHeight = chartBottom - chartTop

        val count = bars.size
        val slot = (width - paddingLeft - paddingRight).toFloat() / count
        val barWidth = slot * 0.5f

        bars.forEachIndexed { index, bar ->
            val centerX = paddingLeft + slot * index + slot / 2f
            val left = centerX - barWidth / 2f
            val right = centerX + barWidth / 2f

            barRect.set(left, chartTop, right, chartBottom)
            canvas.drawRoundRect(barRect, cornerRadius, cornerRadius, trackPaint)

            val ratio = (bar.value / maxValue) * barGrowFraction
            val barTop = chartBottom - chartHeight * ratio
            barRect.set(left, barTop, right, chartBottom)
            canvas.drawRoundRect(barRect, cornerRadius, cornerRadius, barPaint)

            // Value label above the bar.
            val valueText = bar.value.toInt().toString()
            canvas.drawText(valueText, centerX, barTop - dp(5f), valuePaint)

            // Category label below the chart.
            canvas.drawText(bar.label, centerX, chartBottom + dp(18f), labelPaint)
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun themeColor(attr: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(attr, tv, true)
        return tv.data
    }

    private fun dp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

    private fun sp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}