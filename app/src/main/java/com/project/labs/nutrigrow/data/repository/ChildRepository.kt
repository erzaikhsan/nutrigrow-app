package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class ChildRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    fun getChildrenByParent(id: String): Flow<TemplateResponse<List<ChildrenModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val children = apiService.getChildrenByParent(token = userPreference.token, id = id)
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

    fun getChildProfile(id: String): Flow<TemplateResponse<ChildrenModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getChildrenProfile(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = ChildrenModel("", "", "", "", "","", "","","", 0.0, "", 0.0, "", "",0.0)
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
                    data = ChildrenModel("", "", "", "", "","", "","","", 0.0, "", 0.0, "", "",0.0)
                )
            )
        }
    }

    fun getChildrenByRegion(region: String): Flow<TemplateResponse<List<ChildrenModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val children = apiService.getChildrenByRegion(token = userPreference.token, region = region)
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

    fun getChildrenByName(name: String): Flow<TemplateResponse<List<ChildrenModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val children = apiService.getChildrenByName(token = userPreference.token, name = name)
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

    fun getChildrenByNameAndRegion(name: String, region: String): Flow<TemplateResponse<List<ChildrenModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val children = apiService.getChildrenByNameAndRegion(token = userPreference.token, name = name, region = region)
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

    fun getAllChildren(): Flow<TemplateResponse<List<ChildrenModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val children = apiService.getAllChildren(token = userPreference.token)
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

    fun addChildren( full_name: String, gender: String, place_of_birth: String, date_of_birth: String, father: String, mother: String, region: String, birth_weight: Number, birth_height: Number, birth_head_circum: Number) = flow {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        val register = apiService.addChildren( token = userPreference.token, parents_id = userPreference.id, full_name = full_name, gender = gender, place_of_birth = place_of_birth, date_of_birth = date_of_birth, father = father, mother = mother, region = region, birth_weight = birth_weight, birth_height = birth_height, birth_head_circum = birth_head_circum)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = ChildrenModel("", "", "", "", "","", "", "", "", 0.0, "", 0.0, "", "", 0.0)
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
                data = ChildrenModel("", "", "", "", "","", "","","", 0.0, "", 0.0, "", "",0.0)
            )
        )
    }

    fun updateChildren( id: String, full_name: String, gender: String, place_of_birth: String, date_of_birth: String, father: String, mother: String, region: String, birth_weight: Number, birth_height: Number,  birth_head_circum: Number) = flow {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        val register = apiService.updateChildren( token = userPreference.token, id = id, parents_id = userPreference.id, full_name = full_name, gender = gender, place_of_birth = place_of_birth, date_of_birth = date_of_birth, father = father, mother = mother, region = region, birth_weight = birth_weight, birth_height = birth_height, birth_head_circum = birth_head_circum)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = ChildrenModel("", "", "", "", "","", "","","", 0.0, "", 0.0, "", "",0.0)
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
                data = ChildrenModel("", "", "", "", "", "", "","","", 0.0, "", 0.0, "", "",0.0)
            )
        )
    }

    suspend fun logOut(): Boolean {
        userPreference.destroyUser()
        return true
    }

    companion object {
        @Volatile
        private var instance: ChildRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): ChildRepository =
            instance ?: synchronized(this) {
                instance ?: ChildRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}