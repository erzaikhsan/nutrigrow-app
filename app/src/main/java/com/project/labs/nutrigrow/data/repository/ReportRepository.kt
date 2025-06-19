package com.project.labs.nutrigrow.data.repository

import android.content.Context
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class ReportRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun getChildrenReport(context: Context): File? {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }
        val response = apiService.getChildrenReport(token = userPreference.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "daftar_balita_posyandu.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getParentReport(context: Context): File? {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }
        val response = apiService.getParentReport(token = userPreference.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "daftar_orang_tua_balita_posyandu.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getRegionChildrenReport(region : String, context: Context): File? {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }
        val response = apiService.getRegionChildrenReport(region = region, token = userPreference.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "daftar_balita_wilayah_${region}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getRegionParentReport(region : String, context: Context): File? {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }
        val response = apiService.getRegionParentReport(region = region, token = userPreference.token)

        if (!response.isSuccessful) {
            return null
        }

        val fileName = "daftar_orang_tua_balita_wilayah_${region}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        response.body()?.byteStream()?.use { inputStream ->
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }

    suspend fun getMonthlyReport(region: String, currentDate: String, context: Context): File? {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }
        val response = apiService.getMonthlyReport(region = region, token = userPreference.token, currentDate = currentDate)

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