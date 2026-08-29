package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class EventRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    fun getAllEvent(): Flow<TemplateResponse<List<EventModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val children = apiService.getAllEvent(token = auth.token)
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

    fun getEventById(id: String): Flow<TemplateResponse<EventModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getEventById(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = EventModel("", "", "", "", "", "", "", "")
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
                    data = EventModel("", "", "", "", "", "", "", "")
                )
            )
        }
    }

    fun getEventByRegion(region: String): Flow<TemplateResponse<List<EventModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getEventByRegion(token = auth.token, region = region)
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

    fun getIncomingEvent(date: String, region: String): Flow<TemplateResponse<List<EventModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getIncomingEvent(token = auth.token, date = date, region = region)
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

    fun getEventToday(date: String): Flow<TemplateResponse<List<EventModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getEventToday(token = auth.token, date = date, region = auth.region)
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

    fun addEvent( title: String, date: String, start_time: String, end_time: String, place: String, description: String, region: String ) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.addEvent( token = auth.token, title = title, date = date, start_time = start_time, end_time = end_time, place = place, description = description, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = EventModel("", "", "", "", "", "", "", "")
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
                data = EventModel("", "", "", "", "", "", "", "")
            )
        )
    }

    fun updateEvent(id: String, title: String, date: String, start_time: String, end_time: String, place: String, description: String, region: String,) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.updateEvent( token = auth.token, id = id, title = title, date = date, start_time = start_time, end_time = end_time, place = place, description = description, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = EventModel("", "", "", "", "", "", "", "")
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
                data = EventModel("", "", "", "", "", "", "", "")
            )
        )
    }

    fun deleteEvent(id: String): Flow<TemplateResponse<EventModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.deleteEvent(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = EventModel("", "", "", "", "", "", "", "")
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
                    data = EventModel("", "", "", "", "", "", "", "")
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
        private var instance: EventRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}