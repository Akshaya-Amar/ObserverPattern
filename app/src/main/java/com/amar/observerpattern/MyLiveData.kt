package com.amar.observerpattern

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A simplified implementation of LiveData with lifecycle-aware observers.
 * Observers are only notified when their associated LifecycleOwner is in at least STARTED state.
 * Observers are automatically removed when the LifecycleOwner reaches DESTROYED state.
 *
 * @param T The type of data held by this LiveData.
 */
class MyLiveData<T> {
     /**
      * Maps registered observers to their lifecycle-aware wrappers.
      * This allows tracking observer lifecycles and safe dispatching of data.
      */
     private val observers = mutableMapOf<MyObserver<T>, LifecycleBoundObserver<T>>()
     private var data: T? = null

     /**
      * Adds an observer that is bound to the given LifecycleOwner.
      * The observer will only receive updates when the owner's lifecycle is at least STARTED.
      * If the owner's lifecycle is already DESTROYED or the observer is already registered, this call is ignored.
      *
      * @param owner The LifecycleOwner which controls the observer.
      * @param observer The observer that will receive data updates.
      */
     fun observe(owner: LifecycleOwner, observer: MyObserver<T>) {
          if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return

          // Create a lifecycle-bound wrapper for this observer
          val wrapper = LifecycleBoundObserver(owner, this)

          // Only add observer if it's not already registered
          val observerExistence = observers.putIfAbsent(observer, wrapper)
          if (observerExistence != null) return

          // Add lifecycle observer to track lifecycle changes
          owner.lifecycle.addObserver(wrapper)
     }

     /**
      * Sets a new value and notifies all active observers whose lifecycle is at least STARTED.
      *
      * @param value The new data to set.
      */
     fun setValue(value: T) {
          data = value
          dispatchValue()
     }

     /**
      * Dispatches the current data value to all observers whose LifecycleOwner is active.
      */
     private fun dispatchValue() {
          observers.forEach { (observer, wrapper) ->
               val currentState = wrapper.owner.lifecycle.currentState
               if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    notify(observer)
               }
          }
     }

     /**
      * Calls the observer's onChanged callback with the current data, if any.
      *
      * @param observer The observer to notify.
      */
     private fun notify(observer: MyObserver<T>) {
          data?.let {
               observer.onChanged(it)
          }
     }

     /**
      * Removes all observers that are tied to the given LifecycleOwner.
      * This is typically called when the LifecycleOwner reaches the DESTROYED state.
      *
      * @param owner The LifecycleOwner whose observers should be removed.
      */
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

     /**
      * Lifecycle-aware observer wrapper that listens for lifecycle events of the owner.
      * Removes the observer when the lifecycle reaches the DESTROYED state.
      *
      * @param owner The LifecycleOwner associated with this observer.
      * @param liveData Reference to the LiveData instance to notify for cleanup.
      */
     class LifecycleBoundObserver<T>(
          val owner: LifecycleOwner,
          private val liveData: MyLiveData<T>
     ) : LifecycleEventObserver {
          override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
               val currentState = owner.lifecycle.currentState
               if (currentState == Lifecycle.State.DESTROYED) {
                    liveData.removeObservers(owner) // Remove all observers related to this owner when it is destroyed
                    return
               }
          }
     }
}