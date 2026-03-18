package com.samarth.aifinancecoach.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH)
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun getMonthFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar.get(Calendar.MONTH)
    }

    fun getYearFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar.get(Calendar.YEAR)
    }

    fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    }

    fun getGreeting(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    fun formatTime(millis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))
    }

    fun formatFullDateTime(millis: Long): String {
        return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date(millis))
    }

    fun getDateSection(millis: Long): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = millis }

        return when {
            isSameDay(now, date) -> "Today"
            isYesterday(date) -> "Yesterday"
            isThisWeek(date) -> "This Week"
            else -> "Older"
        }
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(date: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, date)
    }

    fun isThisWeek(date: Calendar): Boolean {
        val startOfWeek = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return date.after(startOfWeek)
    }

    fun formatDate(timestamp: Long) {}
}
