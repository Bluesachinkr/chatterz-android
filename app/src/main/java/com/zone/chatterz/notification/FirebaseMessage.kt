package com.zone.chatterz.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

import com.google.firebase.database.DataSnapshot
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zone.chatterz.R
import com.zone.chatterz.chats.ChatMessageActivity
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.model.User
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FirebaseMessage : FirebaseMessagingService() {

    private val CHANNEL_ID = "com.zone.chatterz"
    private val CHANNEL_NAME = "Chatterz"

    private lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val sented = remoteMessage?.data?.get("sented")

        sented?.let {
            if (it.equals(com.zone.chatterz.connection.Connection.user) && remoteMessage != null) {
                sendNotifcation(remoteMessage)
            }
        }
    }

    private fun sendNotifcation(remoteMessage: RemoteMessage) {
        val user: String = remoteMessage.data.get("user").toString()
        val body = remoteMessage.data.get("body")
        val picture = remoteMessage.data.get("icon")
        val title = remoteMessage.data.get("title")

        when (title) {
            NotificationType.chat -> {
                sendChatMessage(this, user, body)
            }
            NotificationType.groupChat -> {
                sendGroupMessage(this, user, body)
            }
            NotificationType.comment -> {

            }
            NotificationType.follow -> {

            }
            NotificationType.like -> {

            }
        }
    }

    private fun sendGroupMessage(mContext: Context, user: String, body: String?) {

    }

    private fun sendChatMessage(mContext: Context, user: String, body: String?) {
        val j = Integer.parseInt(user.replace("\\D".toRegex(), ""))
        val intent = Intent(this, ChatMessageActivity::class.java)
        intent.putExtra("UserId", user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
var imageBitmap = BitmapFactory.decodeResource(this.resources,R.drawable.app_icon)
        FirebaseMethods.singleValueEvent(com.zone.chatterz.connection.Connection.userRef + File.separator + user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val u = dataSnapshot.getValue(User::class.java)
                    u?.let {
                        if (u.imageUrl.equals("null")) {
                            if(u.gender.equals("Male")){
                                imageBitmap = BitmapFactory.decodeResource(mContext.resources,R.drawable.ic_male_gender_profile)
                            }else{
                                imageBitmap = BitmapFactory.decodeResource(mContext.resources,R.drawable.ic_female_gender_profile)
                            }
                        } else{
                            imageBitmap = getBitmapFromURL(u.imageUrl)
                        }
                        //Build version is equals to oreo or greater
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            val notificationChannel = NotificationChannel(
                                CHANNEL_ID,
                                CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            notificationChannel.enableLights(false)
                            notificationChannel.enableVibration(true)
                            notificationChannel.lockscreenVisibility =
                                Notification.VISIBILITY_PRIVATE

                            notificationManager.createNotificationChannel(
                                notificationChannel
                            )
                            val defaultSound =
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val builder =
                                Notification.Builder(applicationContext, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.app_icon)
                                    .setContentText(body)
                                    .setContentTitle(u.displayName)
                                    .setLargeIcon(imageBitmap)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)

                            var i = 0
                            if (j > 0) {
                                i = j
                            }
                            notificationManager.notify(i, builder.build())

                        } else {

                            val defaultSound =
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val builder = NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.drawable.app_icon)
                                .setContentText(body)
                                .setContentTitle(u.displayName)
                                .setLargeIcon(imageBitmap)
                                .setAutoCancel(true)
                                .setSound(defaultSound)
                                .setContentIntent(pendingIntent)

                            var i = 0
                            if (j > 0) {
                                i = j
                            }
                            notificationManager.notify(i, builder.build())
                        }
                    }
                }
            })
    }

    fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}