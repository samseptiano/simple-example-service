package com.example.timerapp

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var timer: TextView
    var seconds = 0
    var running = false
    var wasRunning = false
    var lastTime = "00:00:00"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        checkRunningService()
    }


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val s1 = intent.getStringExtra("NUMBER")
            val running = intent.getBooleanExtra("RUNNING", true)
            val wasrunning = intent.getBooleanExtra("WASRUNNING", true)
            val seconds = intent.getIntExtra("SECONDS", 0)

            this@MainActivity.running = running
            this@MainActivity.wasRunning = wasrunning
            this@MainActivity.seconds = seconds
            this@MainActivity.lastTime = s1?:"00:00:00"
            timer.setText(s1)


            if (wasRunning) {
                this@MainActivity.running = true;
                button?.text = "Pause"
            }
            else if(seconds == 0){
                button?.text = "Start"
            }
            else{
                this@MainActivity.running = false;
                button?.text = "Resume"
            }


        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.timerapp")
        registerReceiver(broadcastReceiver, intentFilter)
        timer.setText(lastTime)


    }

    override fun onPause() {
        wasRunning = running;
        running = false;
        button?.text = "Resume"
        lastTime = timer.text.toString()
        super.onPause()

    }

    override fun onResume() {
        super.onResume()

        if (wasRunning) {
            running = true;
            button?.text = "Pause"
        }
    }

    fun initView(){
        timer = findViewById(R.id.timer)
        button = findViewById(R.id.button)

        button.setOnClickListener{
            if(running){
                running = false
                button.text = "Resume"
                intentToService(false,false)

            }
            else{
                running = true
                button.text = "Pause"
                intentToService(true,true)
            }
        }

    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun checkRunningService(){
        if(isMyServiceRunning(myService::class.java)){
            intent.action = "com.example.timerapp"
            intent.putExtra("NUMBER", lastTime)
            intent.putExtra("SECONDS", seconds)
            intent.putExtra("RUNNING", false)
            intent.putExtra("WASRUNNING", false)
            sendBroadcast(intent)
        }
        else{
            stopService(Intent(this@MainActivity, myService::class.java))
            startService(Intent(this@MainActivity, myService::class.java))

        }

    }

    private fun intentToService(running:Boolean, wasRunning:Boolean){
        val intent = Intent()
        intent.action = "com.example.timerapp"
        intent.putExtra("NUMBER", timer.text.toString())
        intent.putExtra("SECONDS", seconds)
        intent.putExtra("RUNNING", running)
        intent.putExtra("WASRUNNING", wasRunning)
        sendBroadcast(intent)
    }


}
