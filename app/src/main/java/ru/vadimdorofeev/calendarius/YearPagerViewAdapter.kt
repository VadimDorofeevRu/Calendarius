package ru.vadimdorofeev.calendarius

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class YearPagerViewAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 10000
    }

    override fun createFragment(position: Int): Fragment {
        return(YearFragment.newInstance(position))
    }
}