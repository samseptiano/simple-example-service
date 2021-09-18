package samseptiano.example.stopwatchapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build

class NotificationUI {

	fun addNotification(context: Context, service: Service, timer: String, notifyId: Int) {
		lateinit var notificationChannel: NotificationChannel
		lateinit var notificationManager: NotificationManager
		lateinit var builder: Notification.Builder
		val channelId = notifyId.toString()
		val description = "Timer Notification"
		notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
				NotificationManager

		val notificationIntent = Intent(context, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val pausenotificationIntent = Intent(context, myService::class.java)
		pausenotificationIntent.action = "PAUSE"
		val Intent = PendingIntent.getService(context, 0, pausenotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val resumenotificationIntent = Intent(context, myService::class.java)
		resumenotificationIntent.action = "RESUME"
		val IntentResume = PendingIntent.getService(context, 0, resumenotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val stopnotificationIntent = Intent(context, myService::class.java)
		stopnotificationIntent.action = "STOP"
		val IntentStop = PendingIntent.getService(context, 0, stopnotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
			notificationChannel.lightColor = Color.BLUE
			notificationChannel.enableVibration(true)
			notificationManager.createNotificationChannel(notificationChannel)
			builder = Notification.Builder(context, channelId)
					.setContentTitle(timer)
					.setOngoing(true)
					.setSmallIcon(R.drawable.ic_launcher_foreground)
					.addAction(R.drawable.ic_launcher_foreground, "Pause", Intent)
					.addAction(R.drawable.ic_launcher_foreground, "Resume", IntentResume)
					.addAction(R.drawable.ic_launcher_foreground, "Stop", IntentStop)

					.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable
							.ic_launcher_background)).setContentIntent(pendingIntent)
		}
		service.startForeground(notifyId, builder.build())

	}

	fun cancelNotification(context: Context, notifyId: Int) {
		var ns = Context.NOTIFICATION_SERVICE;
		val nMgr = context.getSystemService(ns) as NotificationManager
//		nMgr.cancel(notifyId)
		nMgr.cancelAll()
	}
}
