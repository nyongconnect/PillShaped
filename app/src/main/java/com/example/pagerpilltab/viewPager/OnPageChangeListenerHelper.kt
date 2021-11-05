package com.example.pagerpilltab.viewPager

abstract class OnPageChangeListenerHelper {

    abstract val pageCount: Int

    abstract fun onPageScrolled(
        selectedPosition: Int,
        lastPosition: Int,
        nextPosition: Int,
        positionOffset: Float
    )

    fun onPageScrolled(position: Int, positionOffset: Float) {
        var offset = position + positionOffset
        val lastPageIndex = (pageCount - 1).toFloat()

        if (offset == lastPageIndex) {
            offset = lastPageIndex - .0001f
        }

        val lastPosition = position - 1
        val nextPosition = position + 1

        if (nextPosition > lastPageIndex || position == -1) {
            return
        }

        onPageScrolled(position, lastPosition, nextPosition, offset % 1)
    }

}
