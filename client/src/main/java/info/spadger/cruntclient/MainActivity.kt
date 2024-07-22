package info.spadger.cruntclient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.pusher.rest.Pusher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections


class MainActivity : AppCompatActivity() {

    Add your pusher creds here...
    val pusher = Pusher("XXX", "XXX", "XXX")

    init {

        pusher.setCluster("eu")
        pusher.setEncrypted(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtMessage = findViewById<EditText>(R.id.txtMessage)

        findViewById<Button>(R.id.btnSend)
            .setOnClickListener {
                val message = txtMessage.text.toString()
                Log.e("button", "got $message")
                sendMessage(message)
            }

        findViewById<NumberPicker>(R.id.numSeconds).apply {
            minValue = 1
            maxValue = 60
            value = 10
        }
    }

    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val payload = mapOf(
                "numSeconds" to findViewById<NumberPicker>(R.id.numSeconds).value,
                "message" to message
            )
            pusher.trigger("general", "message", payload)
        }
    }
}
