package fr.sercurio.soulseek.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import fr.sercurio.saoul_seek.slsk_android.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the Preferences from the XML file
        setPreferencesFromResource(R.xml.fragment_preferences, rootKey)
        val prefs = this.context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
    }
}