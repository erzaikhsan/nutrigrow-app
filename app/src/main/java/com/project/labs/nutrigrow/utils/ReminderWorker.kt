package com.project.labs.nutrigrow.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.local.preference.dataStore
import com.project.labs.nutrigrow.data.remote.retrofit.ApiConfig
import com.project.labs.nutrigrow.data.repository.EventRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val appContext = context.applicationContext

    override suspend fun doWork(): Result {
        try {
            val userPreference = UserPreference.getInstance(appContext.dataStore)

            val apiService = ApiConfig.getApiService()
            val repository = EventRepository.getInstance(userPreference, apiService)

            val date = getTodayDate()

            val result = repository.getEventToday(date).first()

            if (result.success && !result.data.isNullOrEmpty()) {
                val eventNames = result.data.joinToString(", ") { it.title }
                val eventPlace = result.data.joinToString(", ") { it.place }
                showNotification(
                    context = appContext,
                    title = "Pengingat Kegiatan Posyandu",
                    message = "Hai Bunda! Hari ini ada kegiatan $eventNames di $eventPlace",
                    eventId = result.data.first().id
                )
            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}