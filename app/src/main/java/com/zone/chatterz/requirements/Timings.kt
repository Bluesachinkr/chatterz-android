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

        fun getCurrentTime() : String{
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd z")
            sd.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            val date = Date()
            val cur_date = sd.format(date)
            return cur_date
        }

        private fun getDateFormatted(str: String): String {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd")
            val s = sd.parse(str)
            val sd1 = SimpleDateFormat("yyyy-MM-dd")
            val s1 = sd1.format(s)
            return s1
        }

        private fun getTimeFormatted(str: String): String {
            val sd = SimpleDateFormat("hh:mm yyyy-MM-dd")
            val s = sd.parse(str)
            val sd1 = SimpleDateFormat("hh:mm")
            val s1 = sd1.format(s)
            return s1
        }

        fun getDateOfTime(str : String) : Int{
            val str1 = getDateFormatted(str)
            val dateStr = str1.substring(8,10)
            return Integer.parseInt(dateStr)
        }

        fun getMonthOfTime(str : String) : Int{
            val str1 = getDateFormatted(str)
            val monthStr = str1.substring(5,7)
            return Integer.parseInt(monthStr)
        }

        fun getYearOfTime(str : String) : Int{
            val str1 = getDateFormatted(str)
            val yearStr  =str1.substring(0,4)
            return Integer.parseInt(yearStr)
        }
        
        fun getHourofTime(str : String) : Int{
            val str1 = getTimeFormatted(str)
            val hrstr = str1.substring(0,2)
            return Integer.parseInt(hrstr)
        }

        fun getMinutesofTime(str: String) : Int{
            val str1 = getTimeFormatted(str)
            val hrstr = str1.substring(3,5)
            return Integer.parseInt(hrstr)
        }
    }
}