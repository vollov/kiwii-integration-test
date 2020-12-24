package kiwii.integration.test.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtils {
    companion object {
        fun dateToString(date: LocalDate, format: String): String{
            var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format)
            return date.format(formatter)
        }

        fun stringToDate(dateString: String): LocalDate{
            return LocalDate.parse(dateString)
        }

        fun currentMonth(): Int{
            val currentdate:LocalDate  = LocalDate.now()
            return currentdate.monthValue
        }

        fun currentYear(): Int{
            val currentdate:LocalDate  = LocalDate.now()
            return currentdate.year
        }

        fun currentDate(): LocalDate{
            return LocalDate.now()
        }

    }
}