package uk.henrytwist.androidbasics.preferences

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

fun PreferenceFragmentCompat.onDisplaySelfHandlingPreferenceDialog(preference: Preference?): Boolean {

    if (preference is SelfHandlingDialogPreference) {

        val dialog = preference.createDialog()

        if (dialog is SelfHandlingPreferenceDialogFragment<*>) {

            dialog.setTargetFragment(this, 0)
            val args = dialog.arguments ?: Bundle()
            args.putString(SelfHandlingPreferenceDialogFragment.PREF_KEY, preference.key)
            dialog.arguments = args
        }

        dialog.show(parentFragmentManager, null)

        return true
    }

    return false
}

abstract class SelfHandlingDialogPreference(context: Context, attributeSet: AttributeSet) : DialogPreference(context, attributeSet) {

    abstract fun createDialog(): DialogFragment
}