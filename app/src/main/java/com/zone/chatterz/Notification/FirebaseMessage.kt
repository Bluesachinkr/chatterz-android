package com.zone.chatterz.Notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zone.chatterz.ChatMessageActivity
import com.zone.chatterz.R

class FirebaseMessage : FirebaseMessagingService() {

    private val CHANNEL_ID = "com.zone.Chatterz"
    private val CHANNEL_NAME = "Chatterz"

    private lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val sented = remoteMessage?.data?.get("sented")

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        if (firebaseUser != null && sented.equals(firebaseUser.uid)) {
            sendNotifcation(remoteMessage)
        }
    }

    private fun sendNotifcation(remoteMessage: RemoteMessage?) {
        if (remoteMessage != null) {

            val user: String = remoteMessage.data.get("user").toString()
            val icon = remoteMessage.data.get("icon")
            val body = remoteMessage.data.get("body")
            val title = remoteMessage.data.get("title")

            // val notification = remoteMessage.notification as RemoteMessage.Notification
            val j = Integer.parseInt(user.replace("\\D".toRegex(), ""))
            val intent = Intent(this, ChatMessageActivity::class.java)
            intent.putExtra("UserId",user)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent =
                PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Build version is equals to oreo or greater
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(false)
                notificationChannel.enableVibration(true)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

                notificationManager.createNotificationChannel(notificationChannel)

                val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val builder = Notification.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent)

                var i = 0
                if (j > 0) {
                    i = j
                }
                notificationManager.notify(i, builder.build())

            } else {

                val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.blur)
                    .setContentText(body)
                    .setContentTitle(title)
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
}