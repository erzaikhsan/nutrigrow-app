package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.data.model.ZScoreBatchRequest
import com.project.labs.nutrigrow.data.model.ZScoreCheckModel
import com.project.labs.nutrigrow.data.model.ZScoreSample
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GrowthRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    fun getlastGrowth(id: String): Flow<TemplateResponse<GrowthModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getLastGrowth(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
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
                    data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
                )
            )
        }
    }

    fun getAllGrowth(): Flow<TemplateResponse<List<GrowthModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val children = apiService.getAllGrowth(token = auth.token)
            if (!children.isSuccessful) {
                val message = children.processError()
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

            children.body()?.apply {
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

    fun getGrowthById(id: String): Flow<TemplateResponse<GrowthModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getGrowthById(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
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
                    data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
                )
            )
        }
    }

    fun getGrowthByChildId(id: String): Flow<TemplateResponse<List<GrowthModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getGrowthByChildId(token = auth.token, id = id)
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

    fun getGrowthByChildIdInYear(id: String, year: Number): Flow<TemplateResponse<List<GrowthModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getGrowthByChildIdInYear(token = auth.token, id = id, year = year)
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

    fun getGrowthByDateAndChildId(id: String, date: String): Flow<TemplateResponse<List<GrowthModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getGrowthByDateAndChildId(token = auth.token, id = id, date = date)
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

    fun getGrowthByDate(date: String): Flow<TemplateResponse<List<GrowthModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getGrowthByDate(token = auth.token, date = date)
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

    fun addGrowth( children_id: String, date: String, weight: Double, height: Double, head_circum: Double, arm_circum: Double, note: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.addGrowth( token = auth.token, children_id, date, weight, height, head_circum, arm_circum, note)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
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
                data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
            )
        )
    }

    fun updateGrowth(id: String, children_id: String, date: String, weight: Double, height: Double, head_circum: Double, arm_circum: Double, note: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.updateGrowth( token = auth.token, id = id, children_id, date, weight, height, head_circum, arm_circum, note)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
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
                data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
            )
        )
    }

    fun deleteGrowth(id: String): Flow<TemplateResponse<GrowthModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.deleteGrowth(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
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
                    data = GrowthModel("", "", "", 0, 0.0, "", 0.0, "", "", 0.0,0.0, "")
                )
            )
        }
    }

    fun checkZScoreBatch(samples: List<ZScoreSample>): Flow<TemplateResponse<List<ZScoreCheckModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val response = apiService.checkZScoreBatch(
                token = auth.token,
                request = ZScoreBatchRequest(samples = samples),
            )
            if (!response.isSuccessful) {
                val message = response.processError()
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

            response.body()?.apply {
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

    suspend fun logOut(): Boolean {
        userPreference.destroyUser()
        return true
    }

    companion object {
        @Volatile
        private var instance: GrowthRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): GrowthRepository =
            instance ?: synchronized(this) {
                instance ?: GrowthRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}