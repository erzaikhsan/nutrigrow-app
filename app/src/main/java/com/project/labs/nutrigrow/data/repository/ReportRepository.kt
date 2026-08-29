package com.project.labs.nutrigrow.data.repository

import android.content.Context
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream

class ReportRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun getChildrenReport( month: Number, year: Number, context: Context): File? {
        val auth = userPreference.getAuth().first()
        val response = apiService.getChildrenReport(token = auth.token, month = month, year = year)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "Data Penimbangan Balita.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getParentReport(context: Context): File? {
        val auth = userPreference.getAuth().first()
        val response = apiService.getParentReport(token = auth.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "Data Orang Tua Balita.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getRegionChildrenReport(region : String, month: Number, year: Number, context: Context): File? {
        val auth = userPreference.getAuth().first()
        val response = apiService.getRegionChildrenReport(region = region, month = month, year = year, token = auth.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "Data Penimbangan Balita_${region}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getRegionParentReport(region : String, context: Context): File? {
        val auth = userPreference.getAuth().first()
        val response = apiService.getRegionParentReport(region = region, token = auth.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "Data Orang Tua Balita_${region}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getMonthlyReport(region: String, month: Number, year: Number, context: Context): File? {
        val auth = userPreference.getAuth().first()
        val response = apiService.getMonthlyReport(region = region, token = auth.token, month = month, year = year)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "laporan_penimbangan_${region}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    companion object {
        @Volatile
        private var instance: ReportRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): ReportRepository =
            instance ?: synchronized(this) {
                instance ?: ReportRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}