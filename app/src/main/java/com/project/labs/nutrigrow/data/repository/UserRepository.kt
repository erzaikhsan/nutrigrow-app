package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.RememberedCredential
import com.project.labs.nutrigrow.data.model.SendOtpModel
import com.project.labs.nutrigrow.data.model.VerifyModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    //Auth
    fun login(email: String, password: String) = flow {
        val login = apiService.login(email, password)
        if (!login.isSuccessful) {
            val message = login.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = AuthModel("", "", "", "")
                )
            )
            return@flow
        }

        login.body()?.let {
            saveAuth(it.data.id, it.data.role, it.data.region, it.data.token)
            emit(it)
        }
    }.catch { e ->
        emit(
            TemplateResponse(
                success = false,
                message = e.message.toString(),
                data = AuthModel("", "", "", "")
            )
        )
    }

    fun createAccount( email: String, password: String ) = flow {
        val register = apiService.createAccount( email = email, password = password)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = SendOtpModel(
                        email = "",
                        password = ""
                    )
                )
            )
            return@flow
        }

        register.body()?.apply {
            saveVerify(email, password)
            emit(this)
        }
    }.catch { e ->
        emit(
            TemplateResponse(
                success = false,
                message = e.message.toString(),
                data = SendOtpModel(
                    email = "",
                    password = ""
                )
            )
        )
    }

    fun verifyOtp( otpCode: String) = flow {
        val otp = userPreference.getOTP().first()
        val register = apiService.verifyOtp( email = otp.email, otp = otpCode)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = VerifyModel("", "", "", "", "", "")
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
                data = VerifyModel("", "", "", "", "", "")
            )
        )
    }

    fun registerParent(  full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) = flow {
        val otp = userPreference.getOTP().first()
        val register = apiService.registerParent( email = otp.email, password = otp.password, full_name = full_name, gender = gender, date_of_birth = date_of_birth, phone_number = phone_number, address = address, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "", false, "")
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
                data = UserModel("", "", "", "", "", "", "", false, "")
            )
        )
    }

    fun registerOfficer( email: String, password: String, full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) = flow {
        val auth = userPreference.getAuth().first()

        val register = apiService.registerOfficer( token = auth.token, email = email, password = password, full_name = full_name, gender = gender, date_of_birth = date_of_birth, phone_number = phone_number, address = address, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "", false, "")
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
                data = UserModel("", "", "", "", "", "", "", false, "")
            )
        )
    }

    fun deactivateAccount(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.deactivateAccount(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun activateAccount(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.activateAccount(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    //Parent
    fun getParentAccount(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentAccount(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getParentProfile(): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentProfile(token = auth.token, id = auth.id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getParentById(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentProfile(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getAllParent(): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getAllParent(token = auth.token)
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

    fun getParentByRegion(region: String): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentByRegion(token = auth.token, region = region)
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

    fun getParentByName(name: String): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentByName(token = auth.token, name = name)
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

    fun getParentByNameAndRegion(name: String, region: String): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getParentByNameAndRegion(token = auth.token, name = name, region = region)
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

    fun updateParent( full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.updateParent(token = auth.token ,full_name, gender, date_of_birth, phone_number, address, region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "", false, "")
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
                data = UserModel("", "", "", "", "", "", "", false, "")
            )
        )
    }

    //Officer
    fun getOfficerProfile(): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getOfficerProfile(token = auth.token, id = auth.id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getOfficerAccount(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getOfficerAccount(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getOfficerById(id: String): Flow<TemplateResponse<UserModel>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getOfficerProfile(token = auth.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "", false, "")
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
                    data = UserModel("", "", "", "", "", "", "", false, "")
                )
            )
        }
    }

    fun getAllOfficer(): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getAllOfficer(token = auth.token)
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

    fun getOfficerByRegion(region: String): Flow<TemplateResponse<List<UserModel>>> {
        return flow {
            val auth = userPreference.getAuth().first()

            val user = apiService.getOfficerByRegion(token = auth.token, region = region)
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

    fun updateOfficer( full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) = flow {
        val auth = userPreference.getAuth().first()
        val register = apiService.updateOfficer(token = auth.token ,full_name, gender, date_of_birth, phone_number, address, region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "", false, "")
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
                data = UserModel("", "", "", "", "", "", "", false, "")
            )
        )
    }

    //Preference
    private suspend fun saveAuth(id: String, role: String, region: String, token: String) {
        userPreference.saveAuth(AuthModel(id, role, region, token))
    }

    private suspend fun saveVerify( email: String, password: String) {
        userPreference.saveOTP(SendOtpModel(email, password))
    }

    suspend fun getVerified(): AuthModel = userPreference.getAuth().first()

    suspend fun saveRememberedCredential(email: String, password: String) {
        userPreference.saveRememberedCredential(email, password)
    }

    suspend fun clearRememberedCredential() {
        userPreference.clearRememberedCredential()
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        userPreference.setReminderEnabled(enabled)
    }

    suspend fun isReminderEnabled(): Boolean = userPreference.getReminderEnabled().first()

    suspend fun getRememberedCredential(): RememberedCredential =
        userPreference.getRememberedCredential().first()

//    fun saveDummyUserLogin(role: String)= flow {
//        val user = if (role == "user.buyer@gmail.com"){
//            UserModel("user01", "user.buyer@gmail.com", "Mr. Vincent", "081234567890", "Jl. Merdeka No 7, Semarang, Jawa Tengah", "buyer", "https://freeimghost.net/images/2024/12/10/man53661f0ddd4d0648.md.png", true)
//        } else {
//            UserModel("user02", "user.seller@gmail.com", "Panda Farm", "081234567890", "Jl. Merauke No 9, Kediri, Jawa Timur", "seller", "https://hips.hearstapps.com/hbu.h-cdn.co/assets/16/15/1460404413-1460150763-1460127682-oregon-farm.jpg?crop=0.665xw:1.00xh;0.0442xw,0&resize=980:*", true)
//        }
//        saveUser(user.uid, user.email, user.nama, user.phone, user.address, user.role, user.imageUrl, user.isVerified)
//        emit(user)
//    }

    suspend fun logOut(): Boolean {
        userPreference.destroyUser()
        return true
    }

    fun forgotPassword(email: String) = flow {
        val response = apiService.forgotPassword(email = email)
        if (!response.isSuccessful) {
            emit(TemplateResponse(success = false, message = response.processError(), data = ""))
            return@flow
        }

        emit(
            TemplateResponse(
                success = true,
                message = response.body()?.message ?: "Kode pemulihan telah dikirim",
                data = ""
            )
        )
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = ""))
    }

    fun resetPassword(email: String, otp: String, password: String) = flow {
        val response = apiService.resetPassword(email = email, otp = otp, password = password)
        if (!response.isSuccessful) {
            emit(TemplateResponse(success = false, message = response.processError(), data = ""))
            return@flow
        }

        emit(
            TemplateResponse(
                success = true,
                message = response.body()?.message ?: "Kata sandi berhasil diperbarui",
                data = ""
            )
        )
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = ""))
    }

    fun deleteParent(id: String) = flow {
        val auth = userPreference.getAuth().first()

        val response = apiService.deleteParent(token = auth.token, id = id)
        if (!response.isSuccessful) {
            val message = response.processError()
            if (message == "Unauthorized") {
                logOut()
            }

            emit(TemplateResponse(success = false, message = message, data = ""))
            return@flow
        }

        emit(
            TemplateResponse(
                success = true,
                message = response.body()?.message ?: "Akun orang tua berhasil dihapus",
                data = ""
            )
        )
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = ""))
    }

    fun deleteOfficer(id: String) = flow {
        val auth = userPreference.getAuth().first()

        val response = apiService.deleteOfficer(token = auth.token, id = id)
        if (!response.isSuccessful) {
            val message = response.processError()
            if (message == "Unauthorized") {
                logOut()
            }

            emit(TemplateResponse(success = false, message = message, data = ""))
            return@flow
        }

        emit(
            TemplateResponse(
                success = true,
                message = response.body()?.message ?: "Akun kader berhasil dihapus",
                data = ""
            )
        )
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = ""))
    }

    fun getOfficerByName(name: String) = flow {
        val auth = userPreference.getAuth().first()

        val officers = apiService.getOfficerByName(token = auth.token, name = name)
        if (!officers.isSuccessful) {
            val message = officers.processError()
            if (message == "Unauthorized") {
                logOut()
            }

            emit(TemplateResponse(success = false, message = message, data = emptyList<UserModel>()))
            return@flow
        }

        officers.body()?.apply {
            emit(this)
        }
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = emptyList<UserModel>()))
    }

    fun getOfficerByNameAndRegion(region: String, name: String) = flow {
        val auth = userPreference.getAuth().first()

        val officers = apiService.getOfficerByNameAndRegion(token = auth.token, region = region, name = name)
        if (!officers.isSuccessful) {
            val message = officers.processError()
            if (message == "Unauthorized") {
                logOut()
            }

            emit(TemplateResponse(success = false, message = message, data = emptyList<UserModel>()))
            return@flow
        }

        officers.body()?.apply {
            emit(this)
        }
    }.catch { e ->
        emit(TemplateResponse(success = false, message = e.message.toString(), data = emptyList<UserModel>()))
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also {
                instance = it
            }
    }
}