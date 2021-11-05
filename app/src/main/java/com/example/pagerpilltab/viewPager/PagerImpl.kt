package com.example.pagerpilltab.viewPager

import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.viewpager2.widget.ViewPager2

class PagerImpl(private val viewPager: ViewPager2): Pager {

    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    override val isNotEmpty = viewPager.isNotEmpty()

    override val currentItem = viewPager.currentItem

    override val isEmpty = viewPager.isEmpty()

    override val count = viewPager.adapter?.itemCount ?: 0

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        viewPager.setCurrentItem(item, smoothScroll)
    }

    override fun removeOnPageChangeListener() {
        onPageChangeCallback?.let { viewPager.unregisterOnPageChangeCallback(it) }
    }

    override fun addOnPageChangeListener(
        onPageChangeListenerHelper: OnPageChangeListenerHelper
    ) {
        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onPageChangeListenerHelper.onPageScrolled(position, positionOffset)
            }
        }
        onPageChangeCallback?.let {
            viewPager.registerOnPageChangeCallback(it)
        }
    }
}
