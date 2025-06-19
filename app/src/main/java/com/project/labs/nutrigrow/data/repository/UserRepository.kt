package com.project.labs.nutrigrow.data.repository

import com.project.labs.nutrigrow.data.local.preference.UserPreference
import com.project.labs.nutrigrow.data.model.AccountModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.SendOtpModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.model.VerifyModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import com.project.labs.nutrigrow.data.remote.retrofit.ApiService
import com.project.labs.nutrigrow.utils.processError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

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
        val userPreference = runBlocking {
            userPreference.getOTP().first()
        }

        val register = apiService.verifyOtp( email = userPreference.email, password = userPreference.password, otp = otpCode)
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
        val userPreference = runBlocking {
            userPreference.getOTP().first()
        }

        val register = apiService.registerParent( email = userPreference.email, full_name = full_name, gender = gender, date_of_birth = date_of_birth, phone_number = phone_number, address = address, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "")
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
                data = UserModel("", "", "", "", "", "", "")
            )
        )
    }

    fun registerOfficer( email: String, password: String, full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) = flow {
        val register = apiService.registerOfficer( email = email, password = password, full_name = full_name, gender = gender, date_of_birth = date_of_birth, phone_number = phone_number, address = address, region = region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "")
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
                data = UserModel("", "", "", "", "", "", "")
            )
        )
    }

    fun deactivateAccount(id: String): Flow<TemplateResponse<AccountModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.deactivateAccount(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = AccountModel("", "", "", "", "", "", "", false)
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
                    data = AccountModel("", "", "", "", "", "", "", false)
                )
            )
        }
    }

    fun activateAccount(id: String): Flow<TemplateResponse<AccountModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.activateAccount(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = AccountModel("", "", "", "", "", "", "", false)
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
                    data = AccountModel("", "", "", "", "", "", "", false)
                )
            )
        }
    }

    //Parent
    fun getParentAccount(id: String): Flow<TemplateResponse<AccountModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getParentAccount(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = AccountModel("", "", "", "", "", "", "", false)
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
                    data = AccountModel("", "", "", "", "", "", "", false)
                )
            )
        }
    }

    fun getParentProfile(): Flow<TemplateResponse<UserModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getParentProfile(token = userPreference.token, id = userPreference.id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "")
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
                    data = UserModel("", "", "", "", "", "", "")
                )
            )
        }
    }

    fun getParentById(id: String): Flow<TemplateResponse<UserModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getParentProfile(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "")
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
                    data = UserModel("", "", "", "", "", "", "")
                )
            )
        }
    }

    fun getAllParent(): Flow<TemplateResponse<List<UserModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getAllParent(token = userPreference.token)
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
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getParentByRegion(token = userPreference.token, region = region)
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
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getParentByName(token = userPreference.token, name = name)
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
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        val register = apiService.updateParent(token = userPreference.token ,full_name, gender, date_of_birth, phone_number, address, region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "")
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
                data = UserModel("", "", "", "", "", "", "")
            )
        )
    }

    //Officer
    fun getOfficerProfile(): Flow<TemplateResponse<UserModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getOfficerProfile(token = userPreference.token, id = userPreference.id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "")
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
                    data = UserModel("", "", "", "", "", "", "")
                )
            )
        }
    }

    fun getOfficerAccount(id: String): Flow<TemplateResponse<AccountModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getOfficerAccount(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = AccountModel("", "", "", "", "", "", "", false)
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
                    data = AccountModel("", "", "", "", "", "", "", false)
                )
            )
        }
    }

    fun getOfficerById(id: String): Flow<TemplateResponse<UserModel>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getOfficerProfile(token = userPreference.token, id = id)
            if (!user.isSuccessful) {
                val message = user.processError()
                if (message == "Unauthorized") {
                    logOut()
                }

                emit(
                    TemplateResponse(
                        success = false,
                        message = message,
                        data = UserModel("", "", "", "", "", "", "")
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
                    data = UserModel("", "", "", "", "", "", "")
                )
            )
        }
    }

    fun getAllOfficer(): Flow<TemplateResponse<List<UserModel>>> {
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getAllOfficer(token = userPreference.token)
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
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        return flow {
            val user = apiService.getOfficerByRegion(token = userPreference.token, region = region)
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
        val userPreference = runBlocking {
            userPreference.getAuth().first()
        }

        val register = apiService.updateOfficer(token = userPreference.token ,full_name, gender, date_of_birth, phone_number, address, region)
        if (!register.isSuccessful) {
            val message = register.processError()

            emit(
                TemplateResponse(
                    success = false,
                    message = message,
                    data = UserModel("", "", "", "", "", "", "")
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
                data = UserModel("", "", "", "", "", "", "")
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