package com.project.labs.nutrigrow.data.di

import android.content.Context
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.local.preference.dataStore
import com.project.labs.nutrigrow.data.remote.retrofit.ApiConfig
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.EventRepository
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.ReportRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.data.repository.VaccineRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return UserRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideChildRepository(context: Context): ChildRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return ChildRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideGrowthRepository(context: Context): GrowthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return GrowthRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideEventRepository(context: Context): EventRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return EventRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideVaccineRepository(context: Context): VaccineRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return VaccineRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }

    fun provideReportRepository(context: Context): ReportRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(userPreference)
        return ReportRepository.getInstance(userPreference = userPreference, apiService = apiService)
    }
}