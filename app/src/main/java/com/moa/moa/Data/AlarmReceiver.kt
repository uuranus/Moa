package com.moa.moa.Data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.moa.moa.Local.AlarmHistory
import com.moa.moa.Local.AppDatabase
import com.moa.moa.Main.HomeActivity
import com.moa.moa.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val text = intent.getStringExtra("workTitle")
        val id = intent.getIntExtra("workId", 0)
        val groupId = intent.getStringExtra("groupId")
        val userKey = intent.getStringExtra("userKey")

        createNotificationChannel(context)

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "MoaDB"
        ).fallbackToDestructiveMigration()
            .build()

        notifyNotification(context, id, text!!)

        Thread {
            db.alarmDao().insertHistory(AlarmHistory(null, text, Date(), 0))
        }.start()

        //첫 알림이면 뱃지 추가

        val database = FirebaseDatabase.getInstance().reference

        val sharedPreferences = context.getSharedPreferences("Info", Context.MODE_PRIVATE)

        val alarms = sharedPreferences.getInt("alarm", 0)

        if (alarms == 0) {
            database.child("group").child(groupId!!).child("users").child(userKey!!)
                .child("badges").child("3").child("get").setValue(true)
        }

        sharedPreferences.edit(true) {
            putInt("alarm", alarms + 1)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "집안일 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context, id: Int, text: String) {
        val intent = Intent(context, HomeActivity::class.java) //알림 누르면 메인 홈 화면으로 이동

        val pendingIntent =
            PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("집안일 알람")
                .setContentText("${text}을(를) 할 시간입니다")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notify(id, build.build())
        }

    }

}