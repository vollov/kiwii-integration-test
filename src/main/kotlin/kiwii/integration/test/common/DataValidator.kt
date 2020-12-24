package kiwii.integration.test.common

import java.util.regex.Matcher
import java.util.regex.Pattern

class DataValidator {
    companion object {

        fun isValidEmail(email: String?): Boolean {
            val REGEX = "(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
            val p = Pattern.compile(REGEX)
            val m = p.matcher(email)
            return m.matches()
        }

        /**
         * 9 digits local phone number
         */
        fun isValidLocalPhoneNumber(number: String?): Boolean {
            val REGEX = "^\\d{10}$"
            val p: Pattern = Pattern.compile(REGEX)
            val m: Matcher = p.matcher(number)
            return m.matches()
        }
    }
}