package com.zone.chatterz.requirements

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class Timings {

    companion object {

        fun showTime(time: String): String {
            val date = curentDate(time)
            val sd1 = SimpleDateFormat("hh:mm")
            return sd1.format(date)
        }

        fun todayDate(): Date {
            val date = Date()
            val sd = SimpleDateFormat("yyyy-MM-dd")
            val date1 = sd.parse(sd.format(date))
            return date
        }

        fun previousDate(): Date {
            val date = todayDate()
            val cd: Calendar = Calendar.getInstance()
            cd.time = date
            cd.add(Calendar.DAY_OF_YEAR, -1)
            val d1 = cd.time
            return d1
        }

        fun curentDate(time: String): Date {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd")
            return sd.parse(time)
        }

        fun chatHistory(time: String): String {
            val date = curentDate(time)
            if (date.equals(todayDate())) {
                return "Today"
            } else if (date.equals(previousDate())) {
                return "Yesterday"
            } else {
                return ""
            }
        }

        fun getCurrentTime(): String {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd z")
            sd.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            val date = Date()
            val cur_date = sd.format(date)
            return cur_date
        }

        fun getDateFormatted(str: String): String {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd")
            val s = sd.parse(str)
            val sd1 = SimpleDateFormat("yyyy-MM-dd")
            val s1 = sd1.format(s)
            return s1
        }

        fun getTimeFormatted(str: String): String {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd")
            val s = sd.parse(str)
            val sd1 = SimpleDateFormat("hh:mm")
            val s1 = sd1.format(s)
            return s1
        }

        fun getDateOfTime(str: String): Int {
            val str1 = getDateFormatted(str)
            val dateStr = str1.substring(8, 10)
            return Integer.parseInt(dateStr)
        }

        fun getMonthOfTime(str: String): Int {
            val str1 = getDateFormatted(str)
            val monthStr = str1.substring(5, 7)
            return Integer.parseInt(monthStr)
        }

        fun getYearOfTime(str: String): Int {
            val str1 = getDateFormatted(str)
            val yearStr = str1.substring(0, 4)
            return Integer.parseInt(yearStr)
        }

        fun getHourofTime(str: String): Int {
            val str1 = getTimeFormatted(str)
            val hrstr = str1.substring(0, 2)
            return Integer.parseInt(hrstr)
        }

        fun getMinutesofTime(str: String): Int {
            val str1 = getTimeFormatted(str)
            val hrstr = str1.substring(3, 5)
            return Integer.parseInt(hrstr)
        }

        fun timeUploadPost(postTime: String): String {
            val time = getCurrentTime()
            val yearPost = getYearOfTime(postTime).toString()
            val yearTime = getYearOfTime(time).toString()
            val builder = StringBuilder("")
            if (yearPost.equals(yearTime)) {
                val MonthPost = getMonthOfTime(postTime).toString()
                val MonthTime = getMonthOfTime(time).toString()
                if (MonthPost.equals(MonthTime)) {
                    val dayPost = getDateOfTime(postTime).toString()
                    val dayTime = getDateOfTime(time).toString()
                    if (dayPost.equals(dayTime)) {
                        val hourPost = getHourofTime(postTime).toString()
                        val hourTime = getHourofTime(time).toString()
                        if (hourPost.equals(hourTime)) {
                            val minutesPost = getMinutesofTime(postTime).toString()
                            val minutesTime = getMinutesofTime(time).toString()
                            if (minutesPost.equals(minutesTime)) {
                                builder.append("now")
                            } else {
                                val y1 = Integer.parseInt(minutesPost)
                                val y2 = Integer.parseInt(minutesTime)
                                builder.append(y2 - y1)
                                builder.append(" minutes ago")
                            }
                        } else {
                            val y1 = Integer.parseInt(hourPost)
                            val y2 = Integer.parseInt(hourTime)
                            builder.append(y2 - y1)
                            builder.append(" hours ago")
                        }
                    } else {
                        val y1 = Integer.parseInt(dayPost)
                        val y2 = Integer.parseInt(dayTime)
                        builder.append(y2 - y1)
                        builder.append(" days ago")
                    }
                } else {
                    val y1 = Integer.parseInt(MonthPost)
                    val y2 = Integer.parseInt(MonthTime)
                    builder.append(y2 - y1)
                    builder.append(" months ago")
                }
            } else {
                val y1 = Integer.parseInt(yearPost)
                val y2 = Integer.parseInt(yearTime)
                builder.append(y2 - y1)
                builder.append(" years ago")
            }
            return builder.toString()
        }
    }
}