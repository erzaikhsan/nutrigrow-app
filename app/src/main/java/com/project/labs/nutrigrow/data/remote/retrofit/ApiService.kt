package com.project.labs.nutrigrow.data.remote.retrofit

import com.project.labs.nutrigrow.data.model.AccountModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.data.model.SendOtpModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.model.VaccineModel
import com.project.labs.nutrigrow.data.model.VerifyModel
import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {
    //Auth
    @FormUrlEncoded
    @POST("auth/register/otp-request")
    @Headers("Accept: application/json")
    suspend fun createAccount(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<TemplateResponse<SendOtpModel>>

    @FormUrlEncoded
    @POST("auth/register/account")
    @Headers("Accept: application/json")
    suspend fun verifyOtp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("otp") otp: String,
    ): Response<TemplateResponse<VerifyModel>>

    @FormUrlEncoded
    @POST("auth/register/parent")
    @Headers("Accept: application/json")
    suspend fun registerParent(
        @Field("email") email: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("phone_number") phone_number: String,
        @Field("address") address: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<UserModel>>

    @FormUrlEncoded
    @POST("auth/register/officer")
    @Headers("Accept: application/json")
    suspend fun registerOfficer(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("phone_number") phone_number: String,
        @Field("address") address: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<UserModel>>

    @FormUrlEncoded
    @POST("auth/login")
    @Headers("Accept: application/json")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<TemplateResponse<AuthModel>>

    @DELETE("auth/deactivate/account/{id}")
    @Headers("Accept: application/json")
    suspend fun deactivateAccount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<AccountModel>>

    @DELETE("auth/activate/account/{id}")
    @Headers("Accept: application/json")
    suspend fun activateAccount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<AccountModel>>

    //Parent
    @GET("parent/account/{id}")
    @Headers("Accept: application/json")
    suspend fun getParentAccount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<AccountModel>>

    @GET("parent/{id}")
    @Headers("Accept: application/json")
    suspend fun getParentProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<UserModel>>

    @GET("parent")
    @Headers("Accept: application/json")
    suspend fun getAllParent(
        @Header("Authorization") token: String,
    ): Response<TemplateResponse<List<UserModel>>>

    @GET("parent/region/{region}")
    @Headers("Accept: application/json")
    suspend fun getParentByRegion(
        @Header("Authorization") token: String,
        @Path("region") region: String
    ): Response<TemplateResponse<List<UserModel>>>

    @GET("parent/name")
    @Headers("Accept: application/json")
    suspend fun getParentByName(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Response<TemplateResponse<List<UserModel>>>

    @FormUrlEncoded
    @PUT("parent")
    @Headers("Accept: application/json")
    suspend fun updateParent(
        @Header("Authorization") token: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("phone_number") phone_number: String,
        @Field("address") address: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<UserModel>>

    //Officer
    @GET("officer/account/{id}")
    @Headers("Accept: application/json")
    suspend fun getOfficerAccount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<AccountModel>>

    @GET("officer/{id}")
    @Headers("Accept: application/json")
    suspend fun getOfficerProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<UserModel>>

    @GET("officer")
    @Headers("Accept: application/json")
    suspend fun getAllOfficer(
        @Header("Authorization") token: String,
    ): Response<TemplateResponse<List<UserModel>>>

    @GET("officer/region/{region}")
    @Headers("Accept: application/json")
    suspend fun getOfficerByRegion(
        @Header("Authorization") token: String,
        @Path("region") region: String
    ): Response<TemplateResponse<List<UserModel>>>

    @FormUrlEncoded
    @PUT("officer")
    @Headers("Accept: application/json")
    suspend fun updateOfficer(
        @Header("Authorization") token: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("phone_number") phone_number: String,
        @Field("address") address: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<UserModel>>

    //Children
    @GET("children/parent/{id}")
    @Headers("Accept: application/json")
    suspend fun getChildrenByParent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<List<ChildrenModel>>>

    @GET("children/region/{region}")
    @Headers("Accept: application/json")
    suspend fun getChildrenByRegion(
        @Header("Authorization") token: String,
        @Path("region") region: String
    ): Response<TemplateResponse<List<ChildrenModel>>>

    @GET("children/name")
    @Headers("Accept: application/json")
    suspend fun getChildrenByName(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Response<TemplateResponse<List<ChildrenModel>>>

    @GET("children/region/{region}/name")
    @Headers("Accept: application/json")
    suspend fun getChildrenByNameAndRegion(
        @Header("Authorization") token: String,
        @Path("region") region: String,
        @Query("name") name: String
    ): Response<TemplateResponse<List<ChildrenModel>>>

    @GET("children/{id}")
    @Headers("Accept: application/json")
    suspend fun getChildrenProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<ChildrenModel>>

    @GET("children")
    @Headers("Accept: application/json")
    suspend fun getAllChildren(
        @Header("Authorization") token: String,
    ): Response<TemplateResponse<List<ChildrenModel>>>

    @FormUrlEncoded
    @POST("children")
    @Headers("Accept: application/json")
    suspend fun addChildren(
        @Header("Authorization") token: String,
        @Field("parents_id") parents_id: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("place_of_birth") place_of_birth: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("father") father: String,
        @Field("mother") mother: String,
        @Field("region") region: String,
        @Field("birth_weight") birth_weight: Number,
        @Field("birth_height") birth_height: Number,
        @Field("birth_head_circum") birth_head_circum: Number,
    ): Response<TemplateResponse<ChildrenModel>>

    @FormUrlEncoded
    @PUT("children/{id}")
    @Headers("Accept: application/json")
    suspend fun updateChildren(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("parents_id") parents_id: String,
        @Field("full_name") full_name: String,
        @Field("gender") gender: String,
        @Field("place_of_birth") place_of_birth: String,
        @Field("date_of_birth") date_of_birth: String,
        @Field("father") father: String,
        @Field("mother") mother: String,
        @Field("region") region: String,
        @Field("birth_weight") birth_weight: Number,
        @Field("birth_height") birth_height: Number,
        @Field("birth_head_circum") birth_head_circum: Number,
    ): Response<TemplateResponse<ChildrenModel>>

    //Growth
    @GET("growth/date/last/{id}")
    @Headers("Accept: application/json")
    suspend fun getLastGrowth(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<GrowthModel>>

    @GET("growth")
    @Headers("Accept: application/json")
    suspend fun getAllGrowth(
        @Header("Authorization") token: String,
    ): Response<TemplateResponse<List<GrowthModel>>>

    @GET("growth/{id}")
    @Headers("Accept: application/json")
    suspend fun getGrowthById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<GrowthModel>>

    @GET("growth/children/{id}")
    @Headers("Accept: application/json")
    suspend fun getGrowthByChildId(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<List<GrowthModel>>>

    @FormUrlEncoded
    @GET("growth/date/month/children/{id}")
    @Headers("Accept: application/json")
    suspend fun getGrowthByDateAndChildId(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("date") date: String,
    ): Response<TemplateResponse<List<GrowthModel>>>

    @FormUrlEncoded
    @GET("growth/date/month")
    @Headers("Accept: application/json")
    suspend fun getGrowthByDate(
        @Header("Authorization") token: String,
        @Field("date") date: String,
    ): Response<TemplateResponse<List<GrowthModel>>>

    @FormUrlEncoded
    @POST("growth")
    @Headers("Accept: application/json")
    suspend fun addGrowth(
        @Header("Authorization") token: String,
        @Field("children_id") children_id: String,
        @Field("date") date: String,
        @Field("weight") weight: Number,
        @Field("height") height: Number,
        @Field("head_circum") head_circum: Number,
        @Field("arm_circum") arm_circum: Number,
        @Field("note") note: String,
    ): Response<TemplateResponse<GrowthModel>>

    @FormUrlEncoded
    @PUT("growth/{id}")
    @Headers("Accept: application/json")
    suspend fun updateGrowth(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("children_id") children_id: String,
        @Field("date") date: String,
        @Field("weight") weight: Number,
        @Field("height") height: Number,
        @Field("head_circum") head_circum: Number,
        @Field("arm_circum") arm_circum: Number,
        @Field("note") note: String,
    ): Response<TemplateResponse<GrowthModel>>

    @DELETE("growth/{id}")
    @Headers("Accept: application/json")
    suspend fun deleteGrowth(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<GrowthModel>>

    //Event
    @GET("event")
    @Headers("Accept: application/json")
    suspend fun getAllEvent(
        @Header("Authorization") token: String,
    ): Response<TemplateResponse<List<EventModel>>>

    @GET("event/{id}")
    @Headers("Accept: application/json")
    suspend fun getEventById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<EventModel>>

    @GET("event/region/{region}")
    @Headers("Accept: application/json")
    suspend fun getEventByRegion(
        @Header("Authorization") token: String,
        @Path("region") region: String
    ): Response<TemplateResponse<List<EventModel>>>

    @GET("event/incoming/event")
    @Headers("Accept: application/json")
    suspend fun getIncomingEvent(
        @Header("Authorization") token: String,
        @Query("date") date: String,
        @Query("region") region: String,
    ): Response<TemplateResponse<List<EventModel>>>

    @GET("event/today/reminder")
    @Headers("Accept: application/json")
    suspend fun getEventToday(
        @Header("Authorization") token: String,
        @Query("date") date: String,
        @Query("region") region: String,
    ): Response<TemplateResponse<List<EventModel>>>

    @FormUrlEncoded
    @POST("event")
    @Headers("Accept: application/json")
    suspend fun addEvent(
        @Header("Authorization") token: String,
        @Field("title") title: String,
        @Field("date") date: String,
        @Field("start_time") start_time: String,
        @Field("end_time") end_time: String,
        @Field("place") place: String,
        @Field("description") description: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<EventModel>>

    @FormUrlEncoded
    @PUT("event/{id}")
    @Headers("Accept: application/json")
    suspend fun updateEvent(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("title") title: String,
        @Field("date") date: String,
        @Field("start_time") start_time: String,
        @Field("end_time") end_time: String,
        @Field("place") place: String,
        @Field("description") description: String,
        @Field("region") region: String,
    ): Response<TemplateResponse<EventModel>>

    @DELETE("event/{id}")
    @Headers("Accept: application/json")
    suspend fun deleteEvent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<EventModel>>

    //Vaccine
    @FormUrlEncoded
    @POST("vaccine")
    @Headers("Accept: application/json")
    suspend fun addVaccine(
        @Header("Authorization") token: String,
        @Field("children_id") children_id: String,
        @Field("date") date: String,
        @Field("vaccine_name") vaccine_name: String,
    ): Response<TemplateResponse<VaccineModel>>

    @GET("vaccine/{id}")
    @Headers("Accept: application/json")
    suspend fun getVaccineById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<VaccineModel>>

    @GET("vaccine/children/{childId}")
    @Headers("Accept: application/json")
    suspend fun getVaccineByChildId(
        @Header("Authorization") token: String,
        @Path("childId") childId: String
    ): Response<TemplateResponse<List<VaccineModel>>>

    @FormUrlEncoded
    @PUT("vaccine/{id}")
    @Headers("Accept: application/json")
    suspend fun updateVaccine(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("children_id") children_id: String,
        @Field("date") date: String,
        @Field("vaccine_name") vaccine_name: String,
    ): Response<TemplateResponse<VaccineModel>>

    @DELETE("vaccine/{id}")
    @Headers("Accept: application/json")
    suspend fun deleteVaccine(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<TemplateResponse<VaccineModel>>

    //Report
    @GET("report/children")
    @Streaming
    suspend fun getChildrenReport(
        @Header("Authorization") token: String,
        @Query("currentDate") currentDate: String,
    ): Response<ResponseBody>

    @GET("report/parents")
    @Streaming
    suspend fun getParentReport(
        @Header("Authorization") token: String,
    ): Response<ResponseBody>

    @GET("report/children/region/{region}")
    @Streaming
    suspend fun getRegionChildrenReport(
        @Header("Authorization") token: String,
        @Path("region") region: String,
        @Query("currentDate") currentDate: String,
    ): Response<ResponseBody>

    @GET("report/parents/region/{region}")
    @Streaming
    suspend fun getRegionParentReport(
        @Header("Authorization") token: String,
        @Path("region") region: String
    ): Response<ResponseBody>

    @GET("report/growth/month/{region}")
    @Streaming
    suspend fun getMonthlyReport(
        @Header("Authorization") token: String,
        @Path("region") region: String,
        @Query("currentDate") currentDate: String,
    ): Response<ResponseBody>
}