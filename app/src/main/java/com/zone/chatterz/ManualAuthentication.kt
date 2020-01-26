package com.zone.chatterz

import java.util.regex.Matcher
import java.util.regex.Pattern

class ManualAuthentication {

    companion object {
        val EMAIL_PATTERN = "(^[a-zA-Z0-9_.+-]+\\@[a-zA-Z]+\\.[a-zA-Z0-9-.]+$)"

        val PASSWORD_PATTERN = "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-zA-Z])" +
                "(?=.*\\d)" +
                "[A-Za-z\\d!@#\$%^&*()_+]" +
                "{8,20}"

        fun validateEmail(email: String): String {
            var isValidMessage = ""
            val pattern =Pattern.compile(EMAIL_PATTERN)
            val matcher = pattern.matcher(email)
            if (email.isEmpty()) {
                isValidMessage = "Email is Empty"
            } else if (!matcher.matches()) {
                isValidMessage = "Email is invalid"
            } else {
                isValidMessage = "Valid"
            }
            return isValidMessage
        }

        fun validatePassword(password: String): String {
            var isPasswordValid= ""
            var pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
            var matcher: Matcher = pattern.matcher(password)
            if(password.isEmpty()){
                isPasswordValid = "Password is Empty"
            }else if(!matcher.matches()){
                isPasswordValid = "Password is Invalid"
            }else{
                isPasswordValid = "Valid"
            }
            return isPasswordValid
        }

    }
}