package uk.henrytwist.androidbasics.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

interface NavigationCommand {

    fun navigateWith(context: Context, navController: NavController)

    class To(@IdRes private val id: Int, private val args: Bundle? = null) : NavigationCommand {

        override fun navigateWith(context: Context, navController: NavController) {

            navController.navigate(id, args)
        }
    }

    class Directions(private val directions: NavDirections) : NavigationCommand {

        override fun navigateWith(context: Context, navController: NavController) {

            navController.navigate(directions)
        }
    }

    object Back : NavigationCommand {

        override fun navigateWith(context: Context, navController: NavController) {

            navController.popBackStack()
        }
    }

    class StartActivity(private val intent: Intent) : NavigationCommand {

        override fun navigateWith(context: Context, navController: NavController) {

            if (intent.resolveActivity(context.packageManager) != null) {

                context.startActivity(intent)
            }
        }
    }
}