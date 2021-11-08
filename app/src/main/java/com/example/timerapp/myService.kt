package com.example.timerapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.util.*


class myService() : Service() {
	val NOTIFICATIONID = 1234
	var notification = NotificationUI()
	var time = "00:00:00"
	var seconds = 0
	var running = true
	var wasRunning = true



	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onCreate() {
		super.onCreate()
		runTimer()
	}


	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//		Toast.makeText(this, "Notification Service started by user.", Toast.LENGTH_LONG).show()

		if (intent != null) {
			if (intent.getAction().equals("PAUSE")) {
				 running = false
				 wasRunning = running
			}
			if (intent.getAction().equals("RESUME")) {
				running = true
				wasRunning = running
			}

			if (intent.getAction().equals("STOP")) {
				time = "00:00:00"
				seconds = 0
				onDestroy()
				notification.cancelNotification(this@myService,this, NOTIFICATIONID)
			}

			sendIntent(time,seconds,running,wasRunning)
		}

		val intentFilter = IntentFilter()
		intentFilter.addAction("com.example.timerapp")
		registerReceiver(broadcastReceiver, intentFilter)

		return START_STICKY
	}

	var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent) {
			val s1 = intent.getStringExtra("NUMBER")
			val running = intent.getBooleanExtra("RUNNING", false)
			val wasrunning = intent.getBooleanExtra("WASRUNNING", true)
			val seconds = intent.getIntExtra("SECONDS", 0)

			this@myService.running = running
			this@myService.wasRunning = wasrunning
			this@myService.seconds = seconds
			this@myService.time = s1.toString()

		}
	}

	override fun onDestroy() {
		running = false
		wasRunning = false


		super.onDestroy()

		//Toast.makeText(this, "Notification Service destroyed by user.", Toast.LENGTH_LONG).show()
	}

	private fun runTimer() {

		val handler = Handler()

		handler.post(object : Runnable {
			override fun run() {
				val hours: Int = seconds / 3600
				val minutes: Int = seconds % 3600 / 60
				val secs: Int = seconds % 60

				// Format the seconds into hours, minutes,
				// and seconds.
				time = String.format(
						Locale.getDefault(),
						"%02d:%02d:%02d", hours,
						minutes, secs
				)

				//Toast.makeText(this@jobService, time, Toast.LENGTH_SHORT).show()

				if (running) {
					seconds++
					Log.d("timer: ", time)

					notification.addNotification(this@myService,this@myService,time,NOTIFICATIONID)
					sendIntent(time,seconds,running,wasRunning)
				}
				else{
					sendIntent(time,seconds,running,wasRunning)
				}
				handler.postDelayed(this, 1000)
			}
		})
	}


	fun sendIntent(time:String, seconds:Int, running:Boolean,wasRunning:Boolean){
		val intent1 = Intent()
		intent1.action = "com.example.timerapp"
		intent1.putExtra("NUMBER", time)
		intent1.putExtra("SECONDS", seconds)
		intent1.putExtra("RUNNING", running)
		intent1.putExtra("WASRUNNING", wasRunning)
		sendBroadcast(intent1)
	}



}
