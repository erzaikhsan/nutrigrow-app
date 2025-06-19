package com.project.labs.nutrigrow.data.di

import android.content.Context
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.local.preference.dataStore
import com.project.labs.nutrigrow.data.remote.retrofit.ApiConfig
import com.project.labs.nutrigrow.data.repository.CheckUpRepository
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.EventRepository
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.ReportRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.data.repository.VaccineRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideChildRepository(context: Context): ChildRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return ChildRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideGrowthRepository(context: Context): GrowthRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return GrowthRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return EventRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideCheckUpRepository(context: Context): CheckUpRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return CheckUpRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideVaccineRepository(context: Context): VaccineRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return VaccineRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideReportRepository(context: Context): ReportRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return ReportRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }
}