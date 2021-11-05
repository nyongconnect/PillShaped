package com.example.pagerpilltab.viewPager

interface Pager {
    val isNotEmpty: Boolean
    val currentItem: Int
    val isEmpty: Boolean
    val count: Int
    fun setCurrentItem(item: Int, smoothScroll: Boolean)
    fun removeOnPageChangeListener()
    fun addOnPageChangeListener(onPageChangeListenerHelper: OnPageChangeListenerHelper)
}
