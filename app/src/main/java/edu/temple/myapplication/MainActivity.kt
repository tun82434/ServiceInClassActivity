package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView: TextView

    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false

    val timerHandler = Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
        true
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.textView)
        var stopButton = findViewById<Button>(R.id.startButton)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (timerBinder.paused) {
                if (isConnected) timerBinder.start(timerTextView.text.toString().toInt())
            } else {
                if (isConnected) timerBinder.start(100)
            }

        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (!timerBinder.paused) {
                if (isConnected) {
                    timerBinder.pause()
                }
            } else {
                if (isConnected) {
                    timerBinder.stop()
                }
            }
        }
    }


    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()

    }
}