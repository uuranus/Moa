package com.moa.moa.Data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moa.moa.Main.HomeActivity
import com.moa.moa.R

class AlarmReceiver :BroadcastReceiver(){
    companion object{
        const val NOTIFICATION_ID=1000
        const val NOTIFICATION_CHANNEL_ID="1000"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("alarem received!!!!","alarm")
        val text=intent.getStringExtra("workTitle")
        val id=intent.getIntExtra("workId",0)
        createNotificationChannel(context)

        notifyNotification(context,id,text!!)
    }

    private fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(NOTIFICATION_CHANNEL_ID,"집안일 알림",NotificationManager.IMPORTANCE_DEFAULT)
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context:Context,id:Int,text:String){
//        val intent= Intent(context, HomeActivity::class.java) //알림 누르면 메인 홈 화면으로 이동
//
//        val pendingIntent=
//            PendingIntent.getActivity(context,id,intent, PendingIntent.FLAG_UPDATE_CURRENT)


        with(NotificationManagerCompat.from(context)){
            val build=NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Moa")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notify(NOTIFICATION_ID,build.build())
        }
    }

}