package info.spadger.crunttrumpet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {
        var startedService = false
        const val MESSAGE_EXTRAS_KEY = "__message__"
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

        if(intent.extras?.containsKey(MESSAGE_EXTRAS_KEY) == true){
            val message = intent.extras?.getString(MESSAGE_EXTRAS_KEY)
            findViewById<TextView>(R.id.txtMain).text = message

            Log.i("main", "killing messages...")
            val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = manager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "crunttrumpet:boohoo")
            wl.acquire(2000)
            wl.release()


            val params = window.attributes
            params.screenBrightness = 0f
            window.attributes = params

            Log.i("main", "kilked messages?")
        }
    }
}
