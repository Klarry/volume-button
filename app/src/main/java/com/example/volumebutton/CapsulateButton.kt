package com.example.volumebutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

class CapsuleButton(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val buttonDefaultPaint: Paint = Paint()
    private val iconPaint: Paint = Paint()
    private val buttonTouchPaint: Paint = Paint()

    private val bottomSemiRect: RectF = RectF()
    private val topSemiRect: RectF = RectF()

    private val path: Path = Path()
    private val textPaint: Paint = Paint()

    private var isTouchTop = false
    private var isTouchBottom = false

    private val centralText = "VOL"

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val length: Float = (width / 2).toFloat()
        val centerTextY = (height / 2).toFloat()

        setPaintProperties()

        bottomSemiRect.set(0f, height - length * 2, length * 2, height.toFloat())
        topSemiRect.set(0f, 0f, length * 2, length * 2)

        if (isTouchBottom) {
            drawHalfOfCapsule(canvas, bottomSemiRect, 0, 0f, length * 2, buttonTouchPaint)
        } else {
            drawHalfOfCapsule(canvas, bottomSemiRect, 0, 0f, length * 2, buttonDefaultPaint)
        }

        if (isTouchTop) {
            drawHalfOfCapsule(canvas, topSemiRect, 180, length * 2, 0f, buttonTouchPaint)
        } else {
            drawHalfOfCapsule(canvas, topSemiRect, 180, length * 2, 0f, buttonDefaultPaint)
        }

        drawTopIcon(canvas, length, length)
        drawBottomIcon(canvas, length, height - length)
        drawCenterText(canvas, length, centerTextY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchY = event.y.toDouble()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchY >= height / 2) {
                    isTouchBottom = true
                } else {
                    isTouchTop = true
                }
                this.invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                isTouchTop = false
                isTouchBottom = false
                this.invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = min((heightSize / 4.5).toInt(), widthSize)
        val height = width * 4

        setMeasuredDimension(width, height)
    }

    private fun setCommonPaintProperties(paint: Paint) {
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
    }

    private fun setPaintProperties() {
        val fontSize = 50
        val defaultButtonColor = ContextCompat.getColor(context, R.color.transparentBlue)
        val gradient = LinearGradient(
            0f, 0f, 0f, (height / 2).toFloat(),
            intArrayOf(Color.YELLOW, defaultButtonColor, defaultButtonColor),
            floatArrayOf(0f, 0.85.toFloat(), 1f), Shader.TileMode.MIRROR
        )

        setCommonPaintProperties(buttonTouchPaint)
        buttonTouchPaint.shader = gradient

        setCommonPaintProperties(iconPaint)
        iconPaint.style = Paint.Style.STROKE
        iconPaint.color = Color.WHITE
        iconPaint.strokeWidth = 5f

        textPaint.color = Color.WHITE
        textPaint.textSize = fontSize.toFloat()
        textPaint.style = Paint.Style.FILL

        buttonDefaultPaint.color = defaultButtonColor
    }

    private fun drawHalfOfCapsule(canvas: Canvas, semiCircle: RectF, startAngle: Int, x1: Float, x2: Float, paint: Paint) {
        path.reset()
        path.arcTo(semiCircle, startAngle.toFloat(), 180f, true)
        path.lineTo(x1, (height / 2).toFloat())
        path.lineTo(x2, (height / 2).toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawTopIcon(canvas: Canvas, x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y - y / 4)
        path.lineTo(x, y + y / 4)
        path.close()

        path.moveTo(x - x / 4, y)
        path.lineTo(x + x / 4, y)
        path.close()

        canvas.drawPath(path, iconPaint)
    }

    private fun drawBottomIcon(canvas: Canvas, x: Float, y: Float) {
        path.reset()
        path.moveTo(x - x / 4, y)
        path.lineTo(x + x / 4, y)
        path.close()

        canvas.drawPath(path, iconPaint)
    }

    private fun drawCenterText(canvas: Canvas, x: Float, y: Float) {
        val centralTextBounds = Rect()

        textPaint.getTextBounds(centralText, 0, centralText.length, centralTextBounds)
        val centralTextHeight = centralTextBounds.height()
        val centralTextWidth = centralTextBounds.width()

        val x1 = x - centralTextWidth / 2
        val y1 = y + centralTextHeight / 2

        canvas.drawText(centralText, x1, y1, textPaint)
    }
}
