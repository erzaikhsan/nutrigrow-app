package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.CheckModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class CheckUpRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    fun getCheckUpById(id: String): Flow<TemplateResponse<CheckModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getCheckUpById(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = CheckModel("", "", "", 0, 0.0, "")
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
                    data = CheckModel("", "", "", 0, 0.0, "")
                )
            )
        }
    }

    fun getCheckUpByParentId(id: String): Flow<TemplateResponse<List<CheckModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getCheckUpByParentId(token = userPreference.token, parentId = id)
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

    fun addCheckUp( parents_id: String, gender: String, age: Int, height: Double) = flow {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        val register = apiService.addCheckUp( token = userPreference.token, parents_id, gender, age, height)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = CheckModel("", "", "", 0, 0.0, "")
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
                data = CheckModel("", "", "", 0, 0.0, "")
            )
        )
    }

    suspend fun logOut(): Boolean {
        userPreference.destroyUser()
        return true
    }

    companion object {
        @Volatile
        private var instance: CheckUpRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): CheckUpRepository =
            instance ?: synchronized(this) {
                instance ?: CheckUpRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}