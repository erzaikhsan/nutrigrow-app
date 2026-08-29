package com.project.labs.nutrigrow.ui.screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.labs.nutrigrow.AppViewModel
import com.project.labs.nutrigrow.data.di.Injection
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.EventRepository
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.ReportRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.data.repository.VaccineRepository
import com.project.labs.nutrigrow.ui.screen.article.ArticleViewModel
import com.project.labs.nutrigrow.ui.screen.article.detail.DetailArticleViewModel
import com.project.labs.nutrigrow.ui.screen.auth.AuthViewModel
import com.project.labs.nutrigrow.ui.screen.child.ChildProfileViewModel
import com.project.labs.nutrigrow.ui.screen.child.add.AddChildViewModel
import com.project.labs.nutrigrow.ui.screen.child.list.ChildrenViewModel
import com.project.labs.nutrigrow.ui.screen.child.update.UpdateChildViewModel
import com.project.labs.nutrigrow.ui.screen.event.EventViewModel
import com.project.labs.nutrigrow.ui.screen.event.add.AddEventViewModel
import com.project.labs.nutrigrow.ui.screen.event.detail.DetailEventViewModel
import com.project.labs.nutrigrow.ui.screen.event.update.UpdateEventViewModel
import com.project.labs.nutrigrow.ui.screen.growth.GrowthViewModel
import com.project.labs.nutrigrow.ui.screen.growth.add.AddGrowthViewModel
import com.project.labs.nutrigrow.ui.screen.growth.detail.DetailGrowthViewModel
import com.project.labs.nutrigrow.ui.screen.growth.update.UpdateGrowthViewModel
import com.project.labs.nutrigrow.ui.screen.home.HomeViewModel
import com.project.labs.nutrigrow.ui.screen.mpasi.MpasiViewModel
import com.project.labs.nutrigrow.ui.screen.mpasi.detail.DetailMpasiViewModel
import com.project.labs.nutrigrow.ui.screen.officer.OfficerProfileViewModel
import com.project.labs.nutrigrow.ui.screen.officer.add.AddOfficerViewModel
import com.project.labs.nutrigrow.ui.screen.parent.ParentProfileViewModel
import com.project.labs.nutrigrow.ui.screen.parent.list.ParentViewModel
import com.project.labs.nutrigrow.ui.screen.profile.ProfileViewModel
import com.project.labs.nutrigrow.ui.screen.profile.update.ProfileUpdateViewModel
import com.project.labs.nutrigrow.ui.screen.report.ReportViewModel
import com.project.labs.nutrigrow.ui.screen.vaccine.add.AddVaccineViewModel
import com.project.labs.nutrigrow.ui.screen.validation.ValidationViewModel
import com.project.labs.nutrigrow.ui.screen.vaccine.update.UpdateVaccineViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val growthRepository: GrowthRepository,
    private val eventRepository: EventRepository,
    private val vaccineRepository: VaccineRepository,
    private val reportRepository: ReportRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    private val viewModelMap = mapOf(
        HomeViewModel::class.java to { HomeViewModel( userRepository, childRepository, eventRepository ) },
        AuthViewModel::class.java to { AuthViewModel(userRepository) },
        AppViewModel::class.java to { AppViewModel(userRepository) },
        ProfileViewModel::class.java to { ProfileViewModel(userRepository) },
        ChildProfileViewModel::class.java to { ChildProfileViewModel(userRepository, childRepository, growthRepository, vaccineRepository) },
        ChildrenViewModel::class.java to { ChildrenViewModel(userRepository, childRepository) },
        ValidationViewModel::class.java to { ValidationViewModel(userRepository, growthRepository) },
        AddChildViewModel::class.java to { AddChildViewModel(userRepository, childRepository) },
        UpdateChildViewModel::class.java to { UpdateChildViewModel(userRepository, childRepository) },
        EventViewModel::class.java to { EventViewModel(userRepository, eventRepository) },
        DetailEventViewModel::class.java to { DetailEventViewModel(userRepository, eventRepository) },
        ParentProfileViewModel::class.java to { ParentProfileViewModel(userRepository, childRepository) },
        GrowthViewModel::class.java to { GrowthViewModel(userRepository, childRepository) },
        AddEventViewModel::class.java to { AddEventViewModel(userRepository, eventRepository) },
        DetailGrowthViewModel::class.java to { DetailGrowthViewModel(userRepository, childRepository, growthRepository) },
        AddGrowthViewModel::class.java to { AddGrowthViewModel(userRepository, childRepository, growthRepository) },
        UpdateGrowthViewModel::class.java to { UpdateGrowthViewModel(userRepository, childRepository, growthRepository) },
        UpdateEventViewModel::class.java to { UpdateEventViewModel(userRepository, eventRepository) },
        AddVaccineViewModel::class.java to { AddVaccineViewModel(userRepository, childRepository, vaccineRepository) },
        UpdateVaccineViewModel::class.java to { UpdateVaccineViewModel(userRepository, childRepository, vaccineRepository) },
        MpasiViewModel::class.java to { MpasiViewModel(userRepository) },
        DetailMpasiViewModel::class.java to { DetailMpasiViewModel(userRepository) },
        ArticleViewModel::class.java to { ArticleViewModel(userRepository) },
        DetailArticleViewModel::class.java to { DetailArticleViewModel(userRepository) },
        OfficerProfileViewModel::class.java to { OfficerProfileViewModel(userRepository, reportRepository) },
        ProfileUpdateViewModel::class.java to { ProfileUpdateViewModel(userRepository) },
        AddOfficerViewModel::class.java to { AddOfficerViewModel(userRepository) },
        ReportViewModel::class.java to { ReportViewModel(userRepository, reportRepository) },
        ParentViewModel::class.java to { ParentViewModel(userRepository) },
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        viewModelMap[modelClass]?.let {
            return it.invoke() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideChildRepository(context),
                    Injection.provideGrowthRepository(context),
                    Injection.provideEventRepository(context),
                    Injection.provideVaccineRepository(context),
                    Injection.provideReportRepository(context),
                )
            }.also { INSTANCE = it }
    }
}