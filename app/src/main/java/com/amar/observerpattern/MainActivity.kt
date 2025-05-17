package com.amar.observerpattern

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

     private val values = MyLiveData<String>()

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          val textView = findViewById<TextView>(R.id.sample_text_view)

          lifecycleScope.launch {
               delay(6000)
               startActivity(Intent(this@MainActivity, MainActivity2::class.java))
          }

          values.observe(this) {
               textView.text = it
          }

          values.observe(this) {
               Log.d("testing...", "onCreate: observing value $it")
          }

          lifecycleScope.launch {
               for (i in 1..10) {
                    delay(2000)
                    Log.d("testing....", "onCreate: setting value $i")
                    values.setValue("Updating value to $i")
               }
          }
     }
}