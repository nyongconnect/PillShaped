package com.example.pagerpilltab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.pagerpilltab.viewPager.PageIndicatorView

class MainActivity : AppCompatActivity() {

    private val adapter = CarouselAdapter()
    private lateinit var benefitsPager: ViewPager2
    private lateinit var pageIndicator: PageIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        benefitsPager = findViewById(R.id.benefitPager)
        pageIndicator = findViewById(R.id.pageIndicator)
        benefitsPager.adapter = adapter
        adapter.items = CarouselAdapter.Model.values().asList()
        pageIndicator.setViewPager(benefitsPager)


    }
}