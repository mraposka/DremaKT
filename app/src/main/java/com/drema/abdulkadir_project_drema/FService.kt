package com.drema.abdulkadir_project_drema

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

var channelId="App"
var channelName="com.drema.abdulkadir_project_drema"
class FService:FirebaseMessagingService() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        println("Title"+ message.notification!!.title.toString())
        println("Body"+ message.notification!!.body.toString())
        if(message.notification!!.title!=null)
            showNotification(this,message.notification!!.title.toString(),message.notification!!.body.toString(),R.drawable.logo)

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("TOKEN:$token")
    }

    private fun showNotification(context: Context, title: String, text: String, imageResId: Int) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_IMMUTABLE)
        else
            PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, imageResId))
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}