package vn.com.rd.waterreminder.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val wavePaint1 = Paint().apply {
        color = Color.parseColor("#695DCCFC") // 41% opacity
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val wavePaint2 = Paint().apply {
        color = Color.parseColor("#415DCCFC") // 25% opacity
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var phase1 = 0f
    private var phase2 = 0f

    // Wave baseline position (where the waves are centered)
    private var waveBaselinePosition = 0.45f // This means the waves will fill ~55% of the view from bottom

    private val animator1 = ValueAnimator.ofFloat(0f, 2 * Math.PI.toFloat()).apply {
        duration = 5000
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            phase1 = it.animatedValue as Float
            invalidate()
        }
    }

    private val animator2 = ValueAnimator.ofFloat(0f, 2 * Math.PI.toFloat()).apply {
        duration = 7000 // Different duration creates more natural effect
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            phase2 = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        animator1.start()
        animator2.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background wave (higher and slower)
        drawWave(canvas, wavePaint2, 40f, phase2, 1.8f)

        // Draw the foreground wave (lower and faster)
        drawWave(canvas, wavePaint1, 60f, phase1, 1.5f)
    }

    private fun drawWave(
        canvas: Canvas,
        paint: Paint,
        waveHeight: Float,
        phase: Float,
        wavelengthFactor: Float
    ) {
        val width = width.toFloat()
        val height = height.toFloat()

        if (width <= 0 || height <= 0) return

        val path = Path()

        // Calculate baseline position (where the waves will be centered)
        val baseline = height * waveBaselinePosition

        // Start at the bottom left corner
        path.moveTo(0f, height)

        val waveLength = width / wavelengthFactor

        // Add points along the wave path
        val step = 5  // smaller step for smoother curve
        for (x in 0..width.toInt() step step) {
            val xFloat = x.toFloat()

            // Primary wave - using full cycle for smooth looping
            val y1 = waveHeight * sin(2 * Math.PI * (xFloat / waveLength) + phase).toFloat()

            // Secondary smaller wave with different frequency
            val y2 = (waveHeight * 0.3f) *
                    sin(4 * Math.PI * (xFloat / waveLength) + phase * 1.3f).toFloat()

            // Combine waves
            val y = y1 + y2

            // Position the wave relative to the baseline
            path.lineTo(xFloat, baseline + y)
        }

        // Complete the path by connecting to bottom corners
        path.lineTo(width, height)
        path.close()

        canvas.drawPath(path, paint)
    }

    override fun onDetachedFromWindow() {
        animator1.cancel()
        animator2.cancel()
        super.onDetachedFromWindow()
    }
}