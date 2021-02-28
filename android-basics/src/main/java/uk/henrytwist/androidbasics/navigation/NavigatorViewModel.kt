package uk.henrytwist.androidbasics.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import uk.henrytwist.androidbasics.Event
import uk.henrytwist.androidbasics.livedata.observeEvent

abstract class NavigatorViewModel : ViewModel() {

    private val _navigationCommander = MutableLiveData<Event<NavigationCommand>>()

    fun navigate(navigationCommand: NavigationCommand) {

        _navigationCommander.value = Event(navigationCommand)
    }

    fun observeNavigation(lifecycleOwner: LifecycleOwner, observer: (NavigationCommand) -> Unit) {

        _navigationCommander.observeEvent(lifecycleOwner) {

            observer(it)
        }
    }

    fun observeNavigation(fragment: Fragment) {

        observeNavigation(fragment.viewLifecycleOwner) {

            it.navigateWith(fragment.requireContext(), fragment.findNavController())
        }
    }

    fun navigate(@IdRes id: Int) = navigate(NavigationCommand.To(id))

    fun navigate(@IdRes id: Int, args: Bundle) = navigate(NavigationCommand.To(id, args))

    fun navigate(directions: NavDirections) = navigate(NavigationCommand.Directions(directions))

    fun navigateBack() = navigate(NavigationCommand.Back)
}