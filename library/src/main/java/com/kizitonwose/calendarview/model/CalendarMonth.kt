package com.kizitonwose.calendarview.model

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.io.Serializable


class CalendarMonth private constructor(val yearMonth: YearMonth) : Comparable<CalendarMonth>, Serializable {

    val year: Int = yearMonth.year
    val month: Int = yearMonth.month.value

    val days: List<CalendarDay> by lazy {
        weekDays.flatten().filter { it.owner == DayOwner.THIS_MONTH }
    }

    val weekDays: List<List<CalendarDay>> by lazy {
        val thisMonthDays = (1..yearMonth.lengthOfMonth()).map {
            CalendarDay(it, month, year, DayOwner.THIS_MONTH)
        }

        // Group days by week of month
        val weekOfMonthField = WeekFields.SUNDAY_START.weekOfMonth()
        val weekDaysGroup = thisMonthDays.groupBy { it.date.get(weekOfMonthField) }.values.toMutableList()

        // Add in-dates if necessary
        val firstWeek = weekDaysGroup.first()
        if (firstWeek.size < 7) {
            val previousMonth = yearMonth.minusMonths(1)
            val inDates = (1..previousMonth.lengthOfMonth()).toList()
                .takeLast(7 - firstWeek.size).map {
                    CalendarDay(it, previous.month, previous.year, DayOwner.PREVIOUS_MONTH)
                }
            weekDaysGroup[0] = inDates + firstWeek
        }

        // Add out-dates if necessary.
        val lastWeek = weekDaysGroup.last()
        if (lastWeek.size < 7) {
            val outDates = (1..7 - lastWeek.size).map {
                CalendarDay(it, next.month, next.year, DayOwner.NEXT_MONTH)
            }
            weekDaysGroup[weekDaysGroup.lastIndex] = lastWeek + outDates
        }

        // Ensure we have a representation of all 6 week rows
        while (weekDaysGroup.size < 6) {
            weekDaysGroup.add(emptyList())
        }
        weekDaysGroup
    }

    val dates: List<LocalDate>
        get() = days.map { it.date }

    val previous: CalendarMonth
        get() = CalendarMonth(yearMonth.minusMonths(1))

    val next: CalendarMonth
        get() = CalendarMonth(yearMonth.plusMonths(1))


    override fun compareTo(other: CalendarMonth): Int {
        return yearMonth.compareTo(other.yearMonth)
    }

    companion object {
        fun now(): CalendarMonth {
            return CalendarMonth(YearMonth.now())
        }
    }
}