package com.photobuddy.utils

import android.content.res.Resources
import android.database.Cursor
import android.view.View
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.log10
import kotlin.math.pow

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

// Extensions.kt
fun Long.formatFileSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

fun Int.formatNumber(): String {
    return NumberFormat.getNumberInstance().format(this)
}

fun Int.dp(resources: Resources): Int {
    return (this * resources.displayMetrics.density).toInt()
}

fun Cursor.getStringOrNull(column: String) = getColumnIndex(column).takeIf { it >= 0 }?.let { getString(it) }
fun Cursor.getIntOrNull(column: String) = getColumnIndex(column).takeIf { it >= 0 }?.let { getInt(it) }
fun Cursor.getLongOrNull(column: String) = getColumnIndex(column).takeIf { it >= 0 }?.let { getLong(it) }
