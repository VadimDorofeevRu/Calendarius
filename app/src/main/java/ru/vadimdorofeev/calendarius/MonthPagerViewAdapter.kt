package ru.vadimdorofeev.calendarius

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MonthPagerViewAdapter(fragment: FragmentActivity, private val year: Int) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 1000000
    }

    override fun createFragment(position: Int): Fragment {
        val year = position / 12
        var month = position % 12
        month++
        return(MonthFragment.newInstance(year, month, this.year))
    }
}