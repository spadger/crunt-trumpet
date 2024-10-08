package info.spadger.crunttrumpet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import org.json.JSONObject

class ListenerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private var run = true

    companion object {
        const val CHANNEL_ID = "superchan"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("start", "starting!")

        val notification = createServiceNotification()
        startForeground(1, notification)


        val channel = createPusherChannel()
        channel.bind("message") { event ->
            Log.i("Pusher", "Received event with data: $event")

            val payload = JSONObject(event.data)
            val message = payload.getString("message")
            val numSeconds = payload.getInt("numSeconds")

            for(i in 0..numSeconds){

                val colour = if(i%2==0) Color.RED else Color.BLUE
                val uiIntent = Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(MainActivity.MESSAGE_EXTRAS_KEY, message)
                    .putExtra(MainActivity.COLOUR_EXTRAS_KEY, colour)

                startActivity(uiIntent)
                Thread.sleep(1000)
            }
        }

        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun createPusherChannel(): Channel {
        val options = PusherOptions()
        options.setCluster("eu");

        You need your api key here...
        val pusher = Pusher("XXXX", options)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i(
                    "Pusher",
                    "State changed from ${change.previousState} to ${change.currentState}"
                )
            }

            override fun onError(
                message: String?,
                code: String?,
                e: Exception?
            ) {
                Log.e(
                    "Pusher",
                    "There was a problem connecting! code ($code), message ($message), exception($e)"
                )
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("general")
        return channel
    }

    private fun createServiceNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_FROM_BACKGROUND)
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)



        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
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
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .build()
        return notification
    }
}