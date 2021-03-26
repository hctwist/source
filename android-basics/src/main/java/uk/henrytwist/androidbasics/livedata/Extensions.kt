package uk.henrytwist.androidbasics.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.immutable(): LiveData<T> = this