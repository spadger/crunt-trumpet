package info.spadger.crunttrumpet

import android.R
import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_FROM_BACKGROUND
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        var startedService = false
        const val MessageKey = "__message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("main", "XXXXXXXXXXX")

        setContentView(info.spadger.crunttrumpet.R.layout.activity_main)

        if (!startedService) {
            val service = startForegroundService(Intent(this, ListenerService::class.java))
            startedService = true
        }
    }

    override fun onResume() {
        super.onResume()

        if(intent.extras?.containsKey(MessageKey) == true){
            val message = intent.extras?.getString(MessageKey)
            findViewById<TextView>(info.spadger.crunttrumpet.R.id.txtMain).text = message
        }
    }
}


class ListenerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private var run = true
    private var i = 1

    companion object {
        const val CHANNEL_ID = "superchan"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("start", "starting!")

        val notificationIntent = Intent(this, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
            .addFlags(FLAG_FROM_BACKGROUND)
        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            0,
            notificationIntent,
            FLAG_IMMUTABLE
        )

        val channel =
            NotificationChannel(CHANNEL_ID, "PuppyBoggle", NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    lightColor = Color.BLUE
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("PuppyBoggle")
            .setContentText("I'll tell you when it's time to stop...")
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .build()

        startForeground(1, notification)

//        notificationManager.notify(1, notification)

        thread(isDaemon = true, name = "yoho", start = true) {
            while (run) {
                Thread.sleep(1000)

                if (i++ % 3 == 0) {

                    Log.e("start", "Here we go...")

                    val uiIntent = Intent(this, MainActivity::class.java)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(MainActivity.MessageKey, "Hello ther@!@@@ $i")

                    startActivity(uiIntent)
                }

//                run = false

                Log.e("start", "yeah! $i")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}