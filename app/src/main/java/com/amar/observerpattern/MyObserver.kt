package com.amar.observerpattern

/**
 * A functional interface that represents an observer that listens for data changes.
 *
 * @param T The type of data being observed.
 *
 * This interface mimics the behavior of Android's [androidx.lifecycle.Observer] and is
 * used in conjunction with [MyLiveData] to receive updates when the data changes.
 *
 * Usage example:
 * ```
 * val observer = MyObserver<String> { newValue ->
 *     println("New value received: $newValue")
 * }
 * ```
 */
fun interface MyObserver<T> {
     /**
      * Called when the observed data is changed.
      *
      * @param value The new value.
      */
     fun onChanged(value: T)
}