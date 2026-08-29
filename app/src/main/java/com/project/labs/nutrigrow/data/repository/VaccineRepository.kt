package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.VaccineModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class VaccineRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    fun getVaccineById(id: String): Flow<TemplateResponse<VaccineModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getVaccineById(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = VaccineModel("", "", "", "", "")
                    )
                )
                return@flow
            }

            user.body()?.apply {
                emit(this)
            }
        }.catch { e ->
            emit(
                TemplateResponse(
                    success = false,
                    message = e.message.toString(),
                    data =  VaccineModel("", "", "", "", "")
                )
            )
        }
    }

    fun getVaccineByChildId(id: String): Flow<TemplateResponse<List<VaccineModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getVaccineByChildId(token = auth.token, childId = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = emptyList()
                    )
                )
                return@flow
            }

            user.body()?.apply {
                emit(this)
            }
        }.catch { e ->
            emit(
                TemplateResponse(
                    success = false,
                    message = e.message.toString(),
                    data = emptyList()
                )
            )
        }
    }

    fun addVaccine( children_id: String, date: String, vaccine_name: String, place: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.addVaccine( token = auth.token, children_id, date, vaccine_name, place)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data =  VaccineModel("", "", "", "", "")
                )
            )
            return@flow
        }

        register.body()?.apply {
            emit(this)
        }
    }.catch { e ->
        emit(
            TemplateResponse(
                success = false,
                message = e.message.toString(),
                data =  VaccineModel("", "", "", "", "")
            )
        )
    }

    fun updateVaccine(id: String, children_id: String, date: String, vaccine_name: String, place: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.updateVaccine( token = auth.token, id = id, children_id, date, vaccine_name, place)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = VaccineModel("", "", "", "", "")
                )
            )
            return@flow
        }

        register.body()?.apply {
            emit(this)
        }
    }.catch { e ->
        emit(
            TemplateResponse(
                success = false,
                message = e.message.toString(),
                data = VaccineModel("", "", "", "", "")
            )
        )
    }

    fun deleteVaccine(id: String): Flow<TemplateResponse<VaccineModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.deleteVaccine(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = VaccineModel("", "", "", "", "")
                    )
                )
                return@flow
            }

            user.body()?.apply {
                emit(this)
            }
        }.catch { e ->
            emit(
                TemplateResponse(
                    success = false,
                    message = e.message.toString(),
                    data = VaccineModel("", "", "", "", "")
                )
            )
        }
    }

    suspend fun logOut(): Boolean {
        userPreference.destroyUser()
        return true
    }

    companion object {
        @Volatile
        private var instance: VaccineRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): VaccineRepository =
            instance ?: synchronized(this) {
                instance ?: VaccineRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}