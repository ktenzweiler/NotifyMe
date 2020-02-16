package com.kodingwithkyle.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val PRIMARY_CHANNEL_ID = "PRIMARY_CHANNEL_ID"
        const val NOTIFICATION_ID = 0
        const val ACTION_UPDATE_NOTIFICATION =
            "com.kodingwithkyle.notifyme.ACTION_UPDATE_NOTIFICATION"
    }

    private val mNotificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var mNotifyButton: Button
    private lateinit var mUpdateButton: Button
    private lateinit var mCancelButton: Button
    private val mReceiver = NotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNotifyButton = findViewById(R.id.notify)
        mNotifyButton.setOnClickListener {
            sendNotification()
        }
        mUpdateButton = findViewById(R.id.update)
        mUpdateButton.setOnClickListener {
            updateNotification()
        }
        mCancelButton = findViewById(R.id.cancel)
        mCancelButton.setOnClickListener {
            cancelNotification()
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =  NotificationChannel(PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION)
        registerReceiver(mReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID)
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources,R.drawable.mascot_1)
        val builder = getNotificationBuilder()
        builder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidImage)
            .setBigContentTitle("Notification Updated!"))
        mNotificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getNotificationBuilder() : NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this,
        NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    inner class NotificationReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }
}
