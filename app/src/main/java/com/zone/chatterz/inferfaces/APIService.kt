package com.zone.chatterz.inferfaces

import com.zone.chatterz.notification.Response
import com.zone.chatterz.notification.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {

    @Headers(
        "Content-Type: application/json",
        "Authorization:key= AAAA-SomYzM:APA91bFRHh5JvJQtPbMqAUJYu78hA_8ZnnLH2yQonvzRSttx6loI8bttfXE6xjrFvMVbYvC4I2ZvlbPapjoUEh-XhT_l404YA_kyLz7g_rIZmtJc3FX-cWzn-ogF2RdZA3-6twSAbSeD"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<Response>
}