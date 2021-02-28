package uk.henrytwist.androidbasics.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uk.henrytwist.kotlinbasics.Event
import uk.henrytwist.kotlinbasics.Trigger

inline fun <T> LiveData<out Event<T>>.observeEvent(
        owner: LifecycleOwner,
        crossinline observer: (T) -> Unit
) {

    observe(owner) { if (!it.consumed) observer(it.consume()) }
}

var <T> MutableLiveData<Event<T>>.event: T?
    get() = value?.peek()
    set(value) {

        this.value = value?.let { Event(it) }
    }

fun MutableLiveData<Trigger>.trigger() {

    value = Trigger()
}