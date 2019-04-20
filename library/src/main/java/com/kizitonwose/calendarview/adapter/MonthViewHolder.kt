package com.kizitonwose.calendarview.adapter

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarMonth

data class MonthViews(val header: View?, val body: LinearLayout, val footer: View?)

class MonthViewHolder constructor(
    rootContainer: LinearLayout,
    private var monthViews: MonthViews,
    @LayoutRes dayViewRes: Int,
    dateClickListener: DateClickListener,
    dateViewBinder: DateViewBinder,
    private var monthHeaderBinder: MonthHeaderFooterBinder?,
    private var monthFooterBinder: MonthHeaderFooterBinder?
) : RecyclerView.ViewHolder(rootContainer) {

    private val weekHolders = (1..6).map { WeekHolder(dayViewRes, dateClickListener, dateViewBinder) }

    init {
        weekHolders.forEach {
            val monthBodyLayout = monthViews.body
            monthBodyLayout.addView(it.inflateWeekView(monthBodyLayout))
        }
    }

    fun bindMonth(month: CalendarMonth) {
        monthViews.header?.let { header ->
            monthHeaderBinder?.invoke(header, month)
        }
        monthViews.footer?.let { footer ->
            monthFooterBinder?.invoke(footer, month)
        }
        weekHolders.forEachIndexed { index, week ->
            week.bindWeekView(month.weekDays[index])
        }
    }

}