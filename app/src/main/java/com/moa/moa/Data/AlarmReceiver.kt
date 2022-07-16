package com.moa.moa.Data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moa.moa.R

class AlarmReceiver :BroadcastReceiver(){
    companion object{
        const val NOTIFICATION_CHANNEL_ID="1000"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val text=intent.getStringExtra("workTitle")
        val id=intent.getIntExtra("workId",0)
        createNotificationChannel(context)

        notifyNotification(context,id,text!!)
    }

    private fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(NOTIFICATION_CHANNEL_ID,"집안일 알림",NotificationManager.IMPORTANCE_HIGH)
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context:Context,id:Int,text:String){
        with(NotificationManagerCompat.from(context)){
            val build=NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Moa")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notify(id,build.build())
        }
    }

}