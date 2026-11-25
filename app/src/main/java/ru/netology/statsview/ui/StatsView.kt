package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {
    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_text_size, textSize)
            lineWidth = getDimension(R.styleable.StatsView_line_width, lineWidth.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color_one, generateRandomColor()),
                getColor(R.styleable.StatsView_color_two, generateRandomColor()),
                getColor(R.styleable.StatsView_color_three, generateRandomColor()),
                getColor(R.styleable.StatsView_color_four, generateRandomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.BUTT
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - AndroidUtils.dp(context, 5)
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius, center.x + radius, center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        val total = data.sum()
        if (total <= 0F) return

        var startAngle = -90F + progress * 360F

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.BUTT
        data.forEachIndexed { index, datum ->
            val fullAngle = (datum / total) * 360F
            val animatedAngle = fullAngle * progress
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, animatedAngle, false, paint)
            startAngle += fullAngle // Сдвигаем startAngle на полный угол следующего сектора
        }

        startAngle = -90F + progress * 360F
        paint.style = Paint.Style.FILL
        data.forEachIndexed { index, datum ->
            val fullAngle = (datum / total) * 360F
            val animatedAngle = fullAngle * progress
            val endAngle = startAngle + animatedAngle
            val endAngleRad = Math.toRadians(endAngle.toDouble())
            val endX = center.x + radius * cos(endAngleRad).toFloat()
            val endY = center.y + radius * sin(endAngleRad).toFloat()
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawCircle(endX, endY, lineWidth / 2f, paint)
            startAngle += fullAngle
        }

        val textY = center.y - (textPaint.descent() + textPaint.ascent()) / 2
        val totalPercent = (data.sum() / total) * 100F
        canvas.drawText(
            "%.2f%%".format(totalPercent),
            center.x,
            textY,
            textPaint
        )
    }

    private fun generateRandomColor(): Int = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}