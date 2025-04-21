package vn.com.rd.waterreminder.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.min
import kotlin.math.sin

class WaveCircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var onClickListener: OnClickListener? = null
    private var drinkAnimator: ValueAnimator? = null
    private var refillAnimator: ValueAnimator? = null

    private val wavePaint1 = Paint().apply {
        color = Color.parseColor("#B03BB8ED")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val wavePaint2 = Paint().apply {
        color = Color.parseColor("#B850BEFC") // 25% opacity
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val circlePaint = Paint().apply {
        color = Color.parseColor("#ADE5FC")
        style = Paint.Style.STROKE
        strokeWidth = 14f
        isAntiAlias = true
    }

    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val smallTextPaint = TextPaint().apply {
        color = Color.GRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var phase1 = 0f
    private var phase2 = 0f
    private var progress = 0.5f // 0.0 to 1.0 representing fill percentage
    private var valueText = "500ml" // The text to display in the center

    private val maskPath = Path()
    private val waveClipPath = Path()
    private val wavePath1 = Path()
    private val wavePath2 = Path()

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
        duration = 7000
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

        // Make the view clickable
        isClickable = true

        // Set up the default click behavior
        super.setOnClickListener { view ->
            // Call the custom listener if it exists
            onClickListener?.onClick(view)
        }
    }

    fun animateDrinking(
        drinkDuration: Long = 1500,
        refillDuration: Long = 2000,
        delayBetween: Long = 300,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        // Cancel any ongoing animations
        drinkAnimator?.cancel()
        refillAnimator?.cancel()

        val currentProgress = progress

        // Create drain animation
        drinkAnimator = ValueAnimator.ofFloat(currentProgress, 0f).apply {
            duration = drinkDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Start refill animation after a delay
                    postDelayed({
                        startRefillAnimation(currentProgress, refillDuration, onAnimationEnd)
                    }, delayBetween)
                }
            })
        }

        // Start the drink animation
        drinkAnimator?.start()
    }

    private fun startRefillAnimation(targetProgress: Float, duration: Long, onComplete: (() -> Unit)?) {
        refillAnimator = ValueAnimator.ofFloat(0f, targetProgress).apply {
            this.duration = duration
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete?.invoke()
                }
            })
        }
        refillAnimator?.start()
    }

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    fun setValue(value: String) {
        this.valueText = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val size = min(width, height)
        val radius = (size / 2) - 8f

        val centerX = width / 2
        val centerY = height / 2

        // Create the circular mask path
        maskPath.reset()
        maskPath.addCircle(centerX, centerY, radius, Path.Direction.CW)

        // Save canvas state before clipping
        canvas.save()
        canvas.clipPath(maskPath)

        // Draw the background circle
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Calculate wave position based on progress (1.0 - progress because we fill from bottom)
        val waveTop = height - (height * progress)

        // Create the wave paths
        createWavePath(wavePath1, width, height, waveTop, phase1, 40f, 1.5f)
        createWavePath(wavePath2, width, height, waveTop, phase2, 30f, 2.0f)

        // Draw the waves
        canvas.drawPath(wavePath2, wavePaint2)
        canvas.drawPath(wavePath1, wavePaint1)

        // Restore canvas state
        canvas.restore()

        // Draw the circle outline
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Draw the text
        canvas.drawText(valueText, centerX, centerY + textPaint.textSize / 3, textPaint)
    }

    private fun createWavePath(
        path: Path,
        width: Float,
        height: Float,
        waveTop: Float,
        phase: Float,
        amplitude: Float,
        wavelengthFactor: Float
    ) {
        path.reset()

        val waveLength = width / wavelengthFactor

        // Start at the bottom left
        path.moveTo(0f, height)

        // Create the wave
        val step = 5
        for (x in 0..width.toInt() step step) {
            val xFloat = x.toFloat()

            // Primary wave
            val y1 = amplitude * sin(2 * Math.PI * (xFloat / waveLength) + phase).toFloat()

            // Secondary wave
            val y2 = (amplitude * 0.3f) * sin(4 * Math.PI * (xFloat / waveLength) + phase * 1.3f).toFloat()

            // Combined wave
            val y = y1 + y2

            path.lineTo(xFloat, waveTop + y)
        }

        // Complete the path
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()
    }

    override fun onDetachedFromWindow() {
        animator1.cancel()
        animator2.cancel()
        drinkAnimator?.cancel()
        refillAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        // Store the listener
        this.onClickListener = listener
    }
}