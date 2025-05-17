package com.amar.observerpattern

fun interface MyObserver<T> {
     fun onChanged(value: T)
}