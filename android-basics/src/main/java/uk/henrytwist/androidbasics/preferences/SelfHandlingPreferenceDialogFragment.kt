package uk.henrytwist.androidbasics.preferences

import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceFragmentCompat

abstract class SelfHandlingPreferenceDialogFragment<P : SelfHandlingDialogPreference> : DialogFragment() {

    protected fun getPreference(): P {

        val preferenceFragment = targetFragment as PreferenceFragmentCompat
        return preferenceFragment.findPreference(requireArguments().getString(PREF_KEY)!!)!!
    }

    companion object {

        const val PREF_KEY = "pref_key"
    }
}