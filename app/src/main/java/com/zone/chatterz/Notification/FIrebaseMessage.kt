package com.zone.chatterz.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zone.chatterz.ChatMessageActivity

class FIrebaseMessage : FirebaseMessagingService(){

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        val sented = p0?.data?.get("sented")

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        if(firebaseUser!=null && sented.equals(firebaseUser.uid)){
            sendNotifcation(p0)
        }
    }

    private fun sendNotifcation(remoteMessage : RemoteMessage?) {
        if(remoteMessage!=null){

            val user : String = remoteMessage.data.get("user").toString()
            val icon = remoteMessage.data.get("icon")
            val body = remoteMessage.data.get("body")
            val title = remoteMessage.data.get("title")

            val  notification = remoteMessage.notification as RemoteMessage.Notification
            val j = Integer.parseInt(user.replace("\\D".toRegex(),""))
            val bundle = Bundle()
            bundle.putString("userId",user)
            val intent = Intent(this,ChatMessageActivity::class.java)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)

            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon.toString()))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)

            val notificate = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var i =0
            if(j > 0){
                i=j
            }

            notificate.notify(i,builder.build())
        }
    }
}