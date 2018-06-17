package com.indexer.weather.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import com.indexer.weather.HomeActivity
import com.indexer.weather.R

class NotificationHelper(private val mContext: Context) {
  private var mNotificationManager: NotificationManager? = null
  private var mBuilder: NotificationCompat.Builder? = null

  @SuppressLint("ObsoleteSdkInt")
      /**
       * Create and push the notification
       */
  fun createNotification(
    title: String,
    message: String
  ) {
    /**Creates an explicit intent for an Activity in your app */
    val resultIntent = Intent(mContext, HomeActivity::class.java)
    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val resultPendingIntent = PendingIntent.getActivity(
        mContext,
        0 /* Request code */, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    mBuilder = NotificationCompat.Builder(mContext)
    mBuilder!!.setSmallIcon(R.mipmap.ic_launcher)
    mBuilder!!.setContentTitle(title)
        .setContentText(message)
        .setAutoCancel(false)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setContentIntent(resultPendingIntent)

    mNotificationManager =
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val notificationChannel =
        NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
      notificationChannel.enableLights(true)
      notificationChannel.lightColor = Color.RED
      notificationChannel.enableVibration(true)
      notificationChannel.vibrationPattern =
          longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
      assert(mNotificationManager != null)
      mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
      mNotificationManager!!.createNotificationChannel(notificationChannel)
    }
    assert(mNotificationManager != null)
    mNotificationManager!!.notify(0 /* Request Code */, mBuilder!!.build())
  }

  companion object {
    const val NOTIFICATION_CHANNEL_ID = "10001"
  }
}