package ru.vadimdorofeev.calendarius

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.FrameLayout
import java.lang.Integer.max


@SuppressLint("ViewConstructor")
class YearView(context: Context?,
               private val year: Int) : FrameLayout(context!!) {

    // Параметры разметки
    private val space = 20

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private val mvs = mutableListOf<MonthView>()

    init {
        for (i in 1..12) {
            val mv = MonthView(context, year, i, MonthView.Mode.Year)
            mv.id = View.generateViewId()
            mvs.add(mv)
            addView(mv)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(widthMeasureSpec)

        viewWidth = w
        viewHeight = h

        val cols =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3
            else 6

        val rows =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 4
            else 2

        val vw = (w - space * (cols + 1)) / cols
        var vh = 0

        if (year > 0) {
            for (i in 1..12) {
                mvs[i - 1].viewWidth = vw.toFloat()
                vh = max(vh, mvs[i - 1].viewHeight.toInt())
            }

            for (i in 1..12) {
                val row = (i - 1) / cols
                val col = (i - 1) % cols

                val ps = LayoutParams(vw, vh)
                ps.setMargins(
                    space + (vw + space) * col,
                    (vh /*+ space*/) * row,
                    vw, vh
                )

                mvs[i - 1].layoutParams = ps
            }
        }

        setMeasuredDimension(w, vh * rows)
    }
}