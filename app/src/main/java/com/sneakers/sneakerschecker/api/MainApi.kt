package com.sneakers.sneakerschecker.api

import com.sneakers.sneakerschecker.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MainApi {
    @GET("sneaker/{sneakerId}/")
    fun validateSneaker(@Path("sneakerId") sneakerId: String): Call<ValidateModel>

    @POST("sneaker/collection/{ownerId}/")
    fun getCollection(
        @Header("Authorization") token: String,
        @Path("ownerId") ownerId: Int,
        @Body param: HashMap<String, Any>
    ): Call<CollectionModel>

    @FormUrlEncoded
    @PATCH("sneaker/ownership")
    fun changeOwnership(
        @Header("Authorization") token: String,
        @Field("sneakerId") sneakerId: String,
        @Field("newAddress") newAddress: String
    ): Call<ResponseBody>

    @PATCH("user/collector/{userId}/")
    fun updateUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body param: HashMap<String, Any?>
    ): Call<ResponseBody>

    @GET("user/duplicate/")
    fun checkDuplicatePhone(@Query("userIdentity") userIdentity: String): Call<ResponseBody>

    @GET("user/collector/{userId}/")
    fun getUserInformation(@Path("userId") userId: Int): Call<CollectorModel>

    @GET("user/userIdentity/{phone}/")
    fun getUserNameByPhone(@Path("phone") phone: String): Call<ResponseBody>

    @POST("sneaker/fcm/notification/")
    fun pushTransferNotification(
        @Header("Authorization") token: String,
        @Body param: HashMap<String, Any>
    ): Call<ResponseBody>

    @GET("payment/token/{userId}/")
    fun getBrainTreeClientToken(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Call<ResponseBody>

    @POST("payment/transaction/{userId}/")
    fun createTransaction(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body param: HashMap<String, Any>
        ): Call<ResponseBody>

    @GET("sneaker/availableTrade/")
    fun getListSneakerAvailable(): Call<ResponseBody>

    @PATCH("sneaker/collection/visibility/{sneakerId}/")
    fun publicSneaker(
        @Header("Authorization") token: String,
        @Path("sneakerId") sneakerId: Long,
        @Body param: HashMap<String, Any?>
    ): Call<ResponseBody>

    @POST("tracing/")
    fun getTradeHistory(@Body param: HashMap<String, Any?>): Call<ArrayList<SneakerHistory>>

    @PATCH("user/password/{userId}/")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body param: HashMap<String, Any?>
    ): Call<ResponseBody>
}