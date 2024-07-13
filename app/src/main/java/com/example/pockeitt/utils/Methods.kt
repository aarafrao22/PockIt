package com.example.pockeitt.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Methods {

    fun convertStringToDate(dateString: String): Date? {
        // Define the date format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            // Parse the string date into a Date object
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}