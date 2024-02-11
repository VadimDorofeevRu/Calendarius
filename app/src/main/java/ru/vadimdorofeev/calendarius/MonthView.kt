package ru.vadimdorofeev.calendarius

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View
import androidx.preference.PreferenceManager
import java.util.Calendar


@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
class MonthView(context: Context?,
                private val year: Int,
                private val month: Int,
                mode: Mode,
                showYear: Boolean = false) : View(context) {

    companion object {
        // Измеренные параметры для разных разрешений
        private val layoutsCache = mutableMapOf<Float, Layout>()

        internal lateinit var onLongPress: (Int, Int) -> Unit
    }

    enum class Mode { Month, Year }

    enum class TextPieceKind { MonthName, DayName, Day, MonthNameBg, TodayBg }

    data class TextPiece(
        var x: Float,
        var y: Float,
        var text: String,
        var kind: TextPieceKind,
        var size: Float) {
        var isPast = false
        var dow = 0
        var day = 0
        var color = 0
        var width = 0f
        var height = 0f
    }

    data class Layout(
        var width: Float = 0f) {
        var height = 0f
        var cellWidth = 0f
        var cellMarginX = 0f
        var cellHeight = 0f
        var cellMarginY = 0f
        var textSize = 0f
        var textSizeMonth = 0f
        var monthNameY = 0f
        var tableTop = 0f
    }

    private var layout = Layout()

    private val textsCache = mutableMapOf<Float, MutableList<TextPiece>>()

    private var texts = mutableListOf<TextPiece>()

    private val monthName by lazy { Common.monthNames[month - 1] +
        if (showYear) " $year" else "" }

    // Параметры разметки календаря
    private val cellMarginXPercent = if (mode == Mode.Month) 0.25f else 0.12f
    private val cellMarginYPercent = if (mode == Mode.Month) 0.7f else 0.4f
    private val headerBottomMarginY = if (mode == Mode.Month) 20 else 7
    private val monthNameBottomMarginYPercent = if (mode == Mode.Month) 0.8f else 0.4f
    private val monthNameTextSizeMultipler = 1.5f

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        typeface = Typeface.MONOSPACE
    }

    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    internal var viewWidth: Float = 0f
        set(value) {
            field = value
            if (!layoutsCache.containsKey(value)) {
                layout = Layout(value)
                prepareLayout()
                layoutsCache[value] = layout
            }
            layout = layoutsCache[value]!!
            if (!textsCache.containsKey(value)) {
                texts = mutableListOf()
                prepareTexts()
                textsCache[value] = texts
            }
            texts = textsCache[value]!!
        }

    internal val viewHeight: Float
        get() = layout.tableTop + 7.5f * layout.cellHeight - layout.cellMarginY + headerBottomMarginY

    private fun getTextWidth(size: Float, text: String): Float {
        paint.textSize = size
        return paint.measureText(text)
    }

    private fun getTextHeight(size: Float, text: String): Float {
        val b = Rect()
        paint.textSize = size
        paint.getTextBounds(text, 0, text.length - 1, b)
        return b.height().toFloat()
    }

    private fun prepareLayout() {
        layout.cellWidth = viewWidth / 7
        layout.cellMarginX = layout.cellWidth * cellMarginXPercent

        // Подбор размера шрифта
        paint.textSize = 10f
        val desiredWidth = layout.cellWidth - layout.cellMarginX * 2
        while (paint.measureText(Common.dayNames[0]) <= desiredWidth)
            paint.textSize++
        paint.textSize--

        layout.textSize = paint.textSize
        layout.textSizeMonth = layout.textSize * monthNameTextSizeMultipler

        val textHeight = getTextHeight(layout.textSize, Common.dayNames[0])
        layout.cellMarginY = textHeight * cellMarginYPercent
        layout.cellHeight = textHeight + layout.cellMarginY * 2

        // Подсчёт максимальной высоты названия месяца
        var maxMonthNameHeight = 0f
        for (i in 0..11) {
            val h = getTextHeight(layout.textSizeMonth, Common.monthNames[i])
            if (h > maxMonthNameHeight)
                maxMonthNameHeight = h
        }

        layout.monthNameY = maxMonthNameHeight
        layout.tableTop = maxMonthNameHeight + maxMonthNameHeight * monthNameBottomMarginYPercent
    }

    private fun prepareTexts() {

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isLightenPast = prefs.getBoolean("lighten_past", false)
        val country = prefs.getString("country_holidays", "none")
        Dating.prepareWeekends()

        texts.clear()
        layout.height = 0f

        texts.add(TextPiece(0f,
            layout.tableTop,
            "",
            TextPieceKind.MonthNameBg,
            0f).apply {
                width = viewWidth
                height = layout.cellHeight
                color = Coloring.getMonthBgColor(month)
            }
        )

        // Название месяца
        val monthNameWidth = getTextWidth(layout.textSizeMonth, monthName)
        texts.add(
            TextPiece(
                (viewWidth - monthNameWidth) / 2,
                layout.monthNameY,
                monthName,
                TextPieceKind.MonthName,
                layout.textSizeMonth
            ).apply { color = Coloring.colorRegularDay })

        // Название дней недели
        val cal = Calendar.getInstance()
        val firstDayOfWeek = cal.firstDayOfWeek
        var n = firstDayOfWeek - 1
        var col = 0
        while (col < 7) {
            addText(1, col, Common.dayNames[n], TextPieceKind.DayName)
                .apply { color = Coloring.getMonthTextColor(month) }
            col++
            n++
            if (n == 7)
                n = 0
        }

        val today = Calendar.getInstance()

        // Дни месяца
        cal.set(year, month - 1, 1)
        while (cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek)
            cal.add(Calendar.DATE, -1)
        col = 0
        var row = 2
        while (true) {
            val isCurrentMonth = cal.get(Calendar.MONTH) == month - 1
            if (isCurrentMonth) {
                if (cal == today) {
                    texts.add(TextPiece(col * layout.cellWidth,
                        layout.tableTop + (row - 1) * layout.cellHeight + headerBottomMarginY,
                        "",
                        TextPieceKind.TodayBg,
                        0f).apply {
                            width = layout.cellWidth
                            height = layout.cellHeight
                            color = Coloring.colorTodayBg
                    })
                }
                val day = cal.get(Calendar.DATE)
                val t = addText(row, col, day.toString(), TextPieceKind.Day)
                t.isPast = cal < today
                t.dow = cal.get(Calendar.DAY_OF_WEEK)
                t.day = day
                if (Dating.isHoliday(country!!, year, month, t.day, t.dow))
                    t.color = if (t.isPast && isLightenPast)
                                  Coloring.colorWeekendPast
                              else
                                  Coloring.colorWeekend
                else
                    t.color = if (t.isPast && isLightenPast)
                                  Coloring.colorRegularDayPast
                              else
                                  Coloring.colorRegularDay
            }
            cal.add(Calendar.DATE, 1)
            col++
            if (col == 7) {
                col = 0
                if (!isCurrentMonth)
                    break
                row++
            }
        }

        layout.height = texts.maxOf { it.y } + layout.cellHeight + 5
    }

    private fun addText(row: Int, col: Int, text: String, kind: TextPieceKind): TextPiece {
        val tp = TextPiece(
            col * layout.cellWidth + (layout.cellWidth - getTextWidth(layout.textSize, text)) / 2,
            layout.tableTop + row * layout.cellHeight - layout.cellMarginY +
                    if (row == 1) 0 else headerBottomMarginY,
            text,
            kind,
            layout.textSize
        )
        texts.add(tp)
        return tp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        viewWidth = w.toFloat()
        setMeasuredDimension(w, viewHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        texts.forEach { t ->
            if (t.kind == TextPieceKind.MonthNameBg || t.kind == TextPieceKind.TodayBg) {
                bgPaint.color = t.color
                canvas.drawRoundRect(t.x, t.y, t.x + t.width, t.y + t.height,
                    5f, 5f,
                    bgPaint)
            }
            else {
                paint.textSize = t.size
                paint.color = t.color
                canvas.drawText(t.text, t.x, t.y, paint)
            }
        }
    }

    init {
        setBackgroundColor(Coloring.colorBg)

        if (mode == Mode.Year)
            setOnLongClickListener {
                onLongPress.invoke(year, month)
                true
            }
    }
}