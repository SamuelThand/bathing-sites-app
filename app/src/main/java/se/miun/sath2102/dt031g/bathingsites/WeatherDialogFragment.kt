package se.miun.sath2102.dt031g.bathingsites

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.json.JSONObject

private const val ARG_WEATHER_DATA = "weatherData"
private const val ARG_WEATHER_ICON = "weatherIcon"

/**
 * Fragment containing the weather information to display to a user.
 *
 * Information about passing data to fragments was learned from:
 * https://stackoverflow.com/questions/47104345/kotlin-idiomatic-way-to-create-a-fragment-newinstance-pattern
 */
class WeatherDialogFragment : DialogFragment() {

    private var weatherData: JSONObject? = null
    private var weatherIcon: Bitmap? = null
    companion object {


        /**
         * Factory method used to create a new instance with passed data.
         *
         * @param weatherData The weather data to display
         * @param weatherIcon The icon to display with the data
         * @return An instance of this fragment
         */
        @JvmStatic
        fun newInstance(weatherData: String, weatherIcon: Bitmap) =
            WeatherDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_WEATHER_DATA, weatherData)
                    putParcelable(ARG_WEATHER_ICON, weatherIcon)
                }
            }


    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherData = JSONObject(it.getString(ARG_WEATHER_DATA)!!)
            weatherIcon = it.getParcelable(ARG_WEATHER_ICON)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Formatting the data recieved from the newInstance() initialization
        val weatherDescription = weatherData?.getJSONArray("weather")?.getJSONObject(0)
            ?.get("description").toString()
        val weatherTemp = weatherData?.getJSONObject("main")?.getString("temp")
            ?.split(".")?.first() + "\u2103"

        // Building the dialog with the formatted data and displaying it
        return AlertDialog.Builder(requireContext())
            .setMessage("$weatherDescription\n$weatherTemp")
            .setTitle(R.string.current_weather_title)
            .setIcon(BitmapDrawable(resources, weatherIcon))
            .setNeutralButton(R.string.weather_dialog_button) { dialog, _ -> dialog.cancel() }
            .create()
    }


}
