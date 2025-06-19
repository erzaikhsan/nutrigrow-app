package com.project.labs.nutrigrow

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.project.labs.nutrigrow.utils.ReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleDailyReminder(this)
    }

    private fun scheduleDailyReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateInitialDelay(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_MONTH, 1)
        return target.timeInMillis - now.timeInMillis
    }
}