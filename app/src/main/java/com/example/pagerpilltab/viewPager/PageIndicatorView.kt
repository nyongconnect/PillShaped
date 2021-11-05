package com.example.pagerpilltab.viewPager

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.pagerpilltab.R

class PageIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_WIDTH_FACTOR = 2.5f
        const val DEFAULT_POINT_COLOR = Color.WHITE
        const val DEFAULT_DOT_SIZE = 16
        const val DEFAULT_DOT_SPACING = 8
        const val DEFAULT_DOT_CORNER_RADIUS = 8
        const val DEFAULT_ELEVATION = 0f
        const val DEFAULT_PROGRESS_MODE = false
        const val DEFAULT_DOT_CLICKABLE = true
    }

    private lateinit var pager: Pager
    private lateinit var linearLayout: LinearLayout

    private var dotsSize = dpToPx(DEFAULT_DOT_SIZE)
    private var dotsCornerRadius = dpToPx(DEFAULT_DOT_CORNER_RADIUS)
    private var dotsSpacing = dpToPx(DEFAULT_DOT_SPACING)
    private var dotsClickable: Boolean = DEFAULT_DOT_CLICKABLE
    private var dotsElevation: Float = DEFAULT_ELEVATION
    private var progressMode: Boolean = DEFAULT_PROGRESS_MODE
    private var dotsWidthFactor: Float = DEFAULT_ELEVATION
    private val dots = ArrayList<ImageView>()
    private val argbEvaluator = ArgbEvaluator()

    var dotsColor: Int = DEFAULT_POINT_COLOR
        set(value) {
            field = value
            refreshDotsColors()
        }

    var selectedDotColor: Int = 0
        set(value) {
            field = value
            refreshDotsColors()
        }

    fun buildOnPageChangedListener(): OnPageChangeListenerHelper {
        return object : OnPageChangeListenerHelper() {

            override fun onPageScrolled(selectedPosition: Int, lastPosition: Int, nextPosition: Int, positionOffset: Float) {
                val selectedDot = dots[selectedPosition]

                val selectedDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)).toInt()
                selectedDot.setWidthAndUpdate(selectedDotWidth)

                if (dots.isInBounds(nextPosition)) {
                    val nextDot = dots[nextPosition]

                    val nextDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * positionOffset).toInt()
                    nextDot.setWidthAndUpdate(nextDotWidth)

                    val selectedDotBackground = selectedDot.background as DotsGradientDrawable
                    val nextDotBackground = nextDot.background as DotsGradientDrawable

                    if (selectedDotColor != dotsColor) {
                        val selectedColor = argbEvaluator.evaluate(
                            positionOffset,
                            selectedDotColor,
                            dotsColor
                        ) as Int

                        val nextColor = argbEvaluator.evaluate(
                            positionOffset,
                            dotsColor,
                            selectedDotColor
                        ) as Int

                        nextDotBackground.setColor(nextColor)

                        if (progressMode && selectedPosition <= pager.currentItem) {
                            selectedDotBackground.setColor(selectedDotColor)
                        } else {
                            selectedDotBackground.setColor(selectedColor)
                        }
                    }
                }

                if (dots.isInBounds(lastPosition)) {
                    val lastDot = dots[lastPosition]

                    val lastDotBackground = lastDot.background as DotsGradientDrawable
                    lastDotBackground.setColor(dotsColor)
                    lastDotBackground.setStroke(4, selectedDotColor)
                }

                invalidate()
            }

            override val pageCount = dots.size
        }
    }

    init {
        init(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            rotation = 180f
            requestLayout()
        }
    }

    fun refreshDots() {
        post {
            refreshDotsCount()
            refreshDotsSize()
            refreshDotsColors()
            refreshOnPageChangedListener()
        }
    }

    fun setViewPager(viewPager: ViewPager2) {
        viewPager.adapter?.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    refreshDots()
                }
            }
        )
        pager = PagerImpl(viewPager)
        refreshDots()
    }

    private fun init(attrs: AttributeSet?) {
        linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        addView(linearLayout, WRAP_CONTENT, WRAP_CONTENT)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView)

            dotsColor = a.getColor(R.styleable.PageIndicatorView_dotsColor, DEFAULT_POINT_COLOR)
            selectedDotColor = a.getColor(R.styleable.PageIndicatorView_selectedDotColor, DEFAULT_POINT_COLOR)
            dotsSize = a.getDimension(R.styleable.PageIndicatorView_dotsSize, dpToPx(DEFAULT_DOT_SIZE))
            dotsCornerRadius = a.getDimension(R.styleable.PageIndicatorView_dotsCornerRadius, dpToPx(DEFAULT_DOT_CORNER_RADIUS))
            dotsSpacing = a.getDimension(R.styleable.PageIndicatorView_dotsSpacing, dpToPx(DEFAULT_DOT_SPACING))
            dotsClickable = a.getBoolean(R.styleable.PageIndicatorView_dotsClickable, DEFAULT_DOT_CLICKABLE)
            dotsElevation = a.getDimension(R.styleable.PageIndicatorView_dotsElevation, DEFAULT_ELEVATION)
            progressMode = a.getBoolean(R.styleable.PageIndicatorView_progressMode, DEFAULT_PROGRESS_MODE)
            dotsWidthFactor = a.getFloat(R.styleable.PageIndicatorView_dotsWidthFactor, DEFAULT_WIDTH_FACTOR)
            if (dotsWidthFactor < 1) {
                dotsWidthFactor = DEFAULT_WIDTH_FACTOR
            }

            a.recycle()
        }

        if (isInEditMode) {
            addDots(5)
            refreshDots()
        }
    }

    private fun addDots(count: Int) {
        for (i in 0 until count) {
            addDot(i)
        }
    }

    private fun addDot(index: Int) {
        val dot = inflate(R.layout.dot_layout)
        val imageView = dot.findViewById<ImageView>(R.id.dot)

        val params = imageView.layoutParams as LayoutParams
        params.height = dotsSize.toInt()
        params.width = params.height
        params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)

        val background = DotsGradientDrawable()
        background.cornerRadius = dotsCornerRadius
        if (isInEditMode) {
            background.setColor(if (0 == index) selectedDotColor else dotsColor)
        } else {
            background.setColor(if (pager.currentItem == index) selectedDotColor else dotsColor)
        }

        imageView.apply {
            setBackground(background)
            elevation = dotsElevation
        }

        dot.apply {
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            setPaddingHorizontal((dotsElevation * 0.8f).toInt())
            setPaddingVertical((dotsElevation * 2).toInt())
            setOnClickListener {
                if (dotsClickable && index < pager.count) {
                    pager.setCurrentItem(index, true)
                }
            }
        }

        dots.add(imageView)

        linearLayout.addView(dot)
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeDot()
        }
    }

    private fun removeDot() {
        linearLayout.removeViewAt(linearLayout.childCount - 1)
        dots.removeAt(dots.size - 1)
    }

    private fun refreshDotsCount() {
        if (dots.size < pager.count) {
            addDots(pager.count - dots.size)
        } else if (dots.size > pager.count) {
            removeDots(dots.size - pager.count)
        }
    }

    private fun refreshDotsSize() {
        for (i in 0 until pager.currentItem) {
            dots[i].setWidthAndUpdate(dotsSize.toInt())
        }
    }

    private fun refreshDotsColors() {
        for (i in dots.indices) {
            refreshDotColor(i)
        }
    }

    private fun refreshDotColor(index: Int) {
        val elevationItem = dots[index]
        val background = elevationItem.background as? DotsGradientDrawable

        background?.let {
            if (index == pager.currentItem) {
                background.setColor(selectedDotColor)
            } else {
                background.setColor(dotsColor)
                background.setStroke(4, selectedDotColor)
            }
        }

        elevationItem.background = background
        elevationItem.invalidate()
    }

    private fun refreshOnPageChangedListener() {
        if (pager.isNotEmpty) {
            pager.removeOnPageChangeListener()
            val onPageChangeListenerHelper = buildOnPageChangedListener()
            pager.addOnPageChangeListener(onPageChangeListenerHelper)
            onPageChangeListenerHelper.onPageScrolled(pager.currentItem, 0f)
        }
    }

}


fun View.setWidthAndUpdate(width: Int) {
    layoutParams.apply {
        this.width = width
        requestLayout()
    }
}


fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, paddingTop, padding, paddingBottom)
}


fun View.setPaddingVertical(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, padding)
}

fun <T> ArrayList<T>.isInBounds(index: Int) = index in 0 until size

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


fun View.dpToPx(dp: Int): Float {
    return (dp * resources.displayMetrics.density)
}
