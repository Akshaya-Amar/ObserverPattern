package com.amar.observerpattern

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

          values.observe {
               textView.text = it
          }

          values.observe {
               Log.d("testing...", "onCreate: observing value $it")
          }

          lifecycleScope.launch {
               for (i in 1..10) {
                    delay(1000)
                    Log.d("testing....", "onCreate: setting value $i")
                    values.setValue("Updating value to $i")
                    if (i == 5) { // stop observing
                         delay(500)
                         values.removeObservers()
                    }
               }
          }
     }
}