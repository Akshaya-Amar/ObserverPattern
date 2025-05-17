package com.amar.observerpattern

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), LifecycleEventObserver {

     private val values = MyLiveData<String>()

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          val textView = findViewById<TextView>(R.id.sample_text_view)

          lifecycleScope.launch {
               delay(6000)
               startActivity(Intent(this@MainActivity, MainActivity2::class.java))
          }

          values.observe {
               textView.text = it
          }

          values.observe {
               Log.d("testing...", "onCreate: observing value $it")
          }

          lifecycleScope.launch {
               for (i in 1..10) {
                    delay(2000)
                    Log.d("testing....", "onCreate: setting value $i")
                    values.setValue("Updating value to $i", this@MainActivity)
               }
          }


          lifecycle.addObserver(this)

          Log.d("current state...", "onCreate: ${lifecycle.currentState}")
     }

     override fun onPause() {
          super.onPause()
          Log.d("current state...", "onPause: ${lifecycle.currentState}")
     }

     override fun onStart() {
          super.onStart()
          Log.d("current state...", "onStart: ${lifecycle.currentState}")
     }

     override fun onResume() {
          super.onResume()
          Log.d("current state...", "onResume: ${lifecycle.currentState}")
     }

     override fun onStop() {
          super.onStop()
          Log.d("current state...", "onStop: ${lifecycle.currentState}")
     }

     override fun onDestroy() {
          super.onDestroy()
          Log.d("current state...", "onDestroy: ${lifecycle.currentState}")
     }

     override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          var currentState = lifecycle.currentState
          if (currentState == Lifecycle.State.DESTROYED) {
               // remove observers
               values.removeObservers()
               Log.d("current state...1", "onStateChanged: destroyed")
               return
          }

          var prevState: Lifecycle.State? = null

          while (prevState != currentState) {
               prevState = currentState
//               activeStateChanged(isActive())
               currentState = lifecycle.currentState
          }
          Log.d("current state...1", "onStateChanged: event - >$event")
     }

     private fun isActive(): Boolean {
          return lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
     }
}