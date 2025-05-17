package com.amar.observerpattern

class MyLiveData<T> {
     private val observers = mutableListOf<MyObserver<T>>()
     private var data: T? = null

     fun observe(observer: MyObserver<T>) {
          observers.add(observer)
     }

     fun setValue(value: T) {
          data = value
          dispatchValue()
     }

     private fun dispatchValue() {
          observers.forEach { observer ->
               notify(observer)
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