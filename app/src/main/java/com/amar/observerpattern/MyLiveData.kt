package com.amar.observerpattern

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MyLiveData<T> {
     private val observers = mutableMapOf<MyObserver<T>, LifecycleBoundObserver<T>>()
     private var data: T? = null

     fun observe(owner: LifecycleOwner, observer: MyObserver<T>) {
          if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return

          val wrapper = LifecycleBoundObserver(owner, this)
          val observerExistence = observers.putIfAbsent(observer, wrapper)
          if (observerExistence != null) return
          owner.lifecycle.addObserver(wrapper)
     }

     fun setValue(value: T) {
          data = value
          dispatchValue()
     }

     private fun dispatchValue() {
          observers.forEach { (observer, wrapper) ->
               val currentState = wrapper.owner.lifecycle.currentState
               if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    notify(observer)
               }
          }
     }

     private fun notify(observer: MyObserver<T>) {
          data?.let {
               observer.onChanged(it)
          }
     }

     private fun removeObservers(owner: LifecycleOwner) {
          val iterator = observers.iterator() // used iterator to avoid ConcurrentModificationException
          while (iterator.hasNext()) {
               val entry = iterator.next()
               val wrapper = entry.value
               if (wrapper.owner == owner) { // The same LiveData instance inside the SharedViewModel is observed by different fragments — each fragment is a different LifecycleOwner.
                    iterator.remove()
                    owner.lifecycle.removeObserver(wrapper)
               }
          }
     }

     class LifecycleBoundObserver<T>(
          val owner: LifecycleOwner,
          private val liveData: MyLiveData<T>
     ) : LifecycleEventObserver {
          override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
               val currentState = owner.lifecycle.currentState
               if (currentState == Lifecycle.State.DESTROYED) {
                    liveData.removeObservers(owner)
                    return
               }
          }
     }
}