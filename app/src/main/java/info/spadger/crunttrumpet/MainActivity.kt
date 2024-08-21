package info.spadger.crunttrumpet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.telephony.ServiceState
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startForegroundService

class MainActivity : AppCompatActivity() {

    companion object {
        var startedService = false
        const val MESSAGE_EXTRAS_KEY = "__message__"
        const val COLOUR_EXTRAS_KEY = "__colour__"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("main", "XXXXXXXXXXX")

        setContentView(R.layout.activity_main)

        if (!startedService) {
            val service = startForegroundService(Intent(this, ListenerService::class.java))
            startedService = true
        }
    }

    override fun onResume() {
        super.onResume()

        if (intent.extras?.containsKey(MESSAGE_EXTRAS_KEY) == true) {
            val message = intent.extras?.getString(MESSAGE_EXTRAS_KEY)!!
            val colour = intent.extras?.getInt(COLOUR_EXTRAS_KEY)!!
            findViewById<TextView>(R.id.txtMain).apply {
                text = message
                setBackgroundColor(colour)
            }
        }
    }
}

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.w("BOOT", "YEah, booting now!")

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startForegroundService(Intent(context, ListenerService::class.java))
        }
    }
}