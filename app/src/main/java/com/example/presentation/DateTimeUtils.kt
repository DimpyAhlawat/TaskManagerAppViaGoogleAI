package com.example.presentation

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatEpochDate(epoch: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(epoch))
}
