package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/**
 * The activity for app settings.
 */
class SettingsActivity : AppCompatActivity() {

    companion object {

        /**
         * Get the string value set for the weather URL
         *
         * @param context The application context
         * @return The string value for the weather URL
         */
        fun getWeatherURL(context: Context): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(
                context.getString(R.string.weather_url_key), Constants.WEATHER_URL.value)
        }


        /**
         * Get the string value set for the BathingSite URL
         *
         * @param context The application context
         * @return The string value for the BathingSite URL
         */
        fun getBathingSiteURL(context: Context): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(
                context.getString(R.string.bathingsite_url_key), Constants.BATHINGSITE_URL.value)
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Fragment containing the preferences.
     */
    class SettingsFragment : PreferenceFragmentCompat() {


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


    }


}
