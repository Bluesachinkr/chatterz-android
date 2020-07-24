package com.zone.chatterz.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    companion object{

        private val BASE_URL = "https://fcm.googleapis.com/"

        var retrofit : Retrofit? = null

       fun  getClient() : Retrofit?{
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}