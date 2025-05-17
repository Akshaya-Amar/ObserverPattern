package com.amar.observerpattern

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [MainActivity] demonstrates a custom implementation of LiveData (MyLiveData).
 *
 * It registers two observers for the same LiveData instance and updates the value periodically.
 * After a short delay, it navigates to [MainActivity2].
 *
 * Key Features:
 * - Registers two observers to the same instance of `MyLiveData`
 * - Updates the value every 2 seconds using a coroutine
 * - Launches another activity after 6 seconds
 */
class MainActivity : AppCompatActivity() {

     private val values = MyLiveData<String>()

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          val textView = findViewById<TextView>(R.id.sample_text_view)

          /**
           * Launches a coroutine that waits for 6 seconds,
           * then navigates to [MainActivity2].
           */
          lifecycleScope.launch {
               delay(6000)
               startActivity(Intent(this@MainActivity, MainActivity2::class.java))
          }

          /**
          * First observer: Updates the text view with the emitted value.
          */
          values.observe(this) {
               textView.text = it
          }

          /**
           * Second observer: Logs the emitted value for debugging.
           */
          values.observe(this) {
               Log.d("testing...", "onCreate: observing value $it")
          }

          /**
           * Periodically updates the LiveData value every 2 seconds.
           */
          lifecycleScope.launch {
               for (i in 1..10) {
                    delay(2000)
                    Log.d("testing....", "onCreate: setting value $i")
                    values.setValue("Updating value to $i")
               }
          }
     }
}