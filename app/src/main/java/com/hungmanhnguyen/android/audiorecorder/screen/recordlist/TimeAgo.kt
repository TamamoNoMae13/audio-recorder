package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo {

    fun getTimeAgo(duration: Long): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - duration)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - duration)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - duration)
        return if (seconds < 60) {
            "recently"
        } else if (minutes == 1L) {
            "a minute ago"
        } else if (minutes > 1 && minutes < 60) {
            "$minutes minutes ago"
        } else if (hours == 1L) {
            "an hour ago"
        } else if (hours > 1 && hours < 24) {
            "$hours hours ago"
        } else if (days == 1L) {
            "a day ago"
        } else {
            "$days days ago"
        }
    }
}