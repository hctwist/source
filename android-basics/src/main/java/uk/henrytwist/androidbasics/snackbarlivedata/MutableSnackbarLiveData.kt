package uk.henrytwist.androidbasics.snackbarlivedata

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import uk.henrytwist.androidbasics.livedata.event
import uk.henrytwist.androidbasics.livedata.observeEvent
import uk.henrytwist.kotlinbasics.Event

class MutableSnackbarLiveData : MutableLiveData<Event<SnackbarContent>>() {

    fun showMessage(message: String, duration: Int) {

        event = SnackbarContent(message, duration)
    }
}

fun LiveData<Event<SnackbarContent>>.observe(fragment: Fragment) {

    observeEvent(fragment.viewLifecycleOwner) { content ->

        fragment.view?.let {

            Snackbar.make(it, content.message, content.duration).show()
        }
    }
}