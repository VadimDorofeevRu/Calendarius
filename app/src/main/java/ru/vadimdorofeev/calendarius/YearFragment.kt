package ru.vadimdorofeev.calendarius

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment


class YearFragment() : Fragment() {

    companion object {
        fun newInstance(year: Int): YearFragment {
            val args = Bundle()
            args.putInt("year", year)
            val fragment = YearFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var year: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        year = args.getInt("year", 2023)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        val params = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID

        val yearView = YearView(Common.context, year)
        yearView.layoutParams = params

        val root = result.findViewById<ConstraintLayout>(R.id.fragment_root)
        root.addView(yearView)

        return result
    }
}