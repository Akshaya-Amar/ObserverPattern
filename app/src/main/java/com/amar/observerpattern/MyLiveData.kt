package com.amar.observerpattern

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class MyLiveData<T> {

     private val observers = mutableListOf<MyObserver<T>>()
     private var data: T? = null

     fun observe(observer: MyObserver<T>) {
          observers.add(observer)
     }

     fun setValue(value: T, owner: LifecycleOwner) {
          data = value
          if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
               dispatchValue()
          }
     }

     private fun dispatchValue() {
          observers.forEach { myObserver ->
               notify(myObserver)
          }
     }

     private fun notify(observer: MyObserver<T>) {
          data?.let {
               observer.onChanged(it)
          }
     }

     fun removeObservers() {
          observers.clear()
     }
}