package ru.vadimdorofeev.calendarius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment


class MonthFragment : Fragment() {

    companion object {
        fun newInstance(year: Int, month: Int, baseYear: Int): MonthFragment {
            val args = Bundle()
            args.putInt("year", year)
            args.putInt("base_year", baseYear)
            args.putInt("month", month)
            val fragment = MonthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var baseYear: Int = 0
    private var year: Int = 0
    private var month: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        year = args.getInt("year", 2023)
        baseYear = args.getInt("base_year", 2023)
        month = args.getInt("month", 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        val params = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        params.topMargin = 20

        val monthView = MonthView(Common.context, year, month, MonthView.Mode.Month, year != baseYear)
        monthView.layoutParams = params

        val root = result.findViewById<ConstraintLayout>(R.id.fragment_root)
        root.addView(monthView)

        return result
    }
}