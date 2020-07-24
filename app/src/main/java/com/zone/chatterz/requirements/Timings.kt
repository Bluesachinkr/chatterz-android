package com.zone.chatterz.requirements

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
    }
}