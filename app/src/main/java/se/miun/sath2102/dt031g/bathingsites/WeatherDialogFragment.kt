package se.miun.sath2102.dt031g.bathingsites

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.json.JSONObject
import se.miun.sath2102.dt031g.bathingsites.databinding.FragmentWeatherDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_WEATHER_DATA = "weatherData"
private const val ARG_WEATHER_ICON = "weatherIcon"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var weatherData: JSONObject? = null
    private var weatherIcon: Bitmap? = null
    private lateinit var binding: FragmentWeatherDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherData = JSONObject(it.getString(ARG_WEATHER_DATA)!!)
            weatherIcon = it.getParcelable(ARG_WEATHER_ICON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWeatherDialogBinding.inflate(inflater, container, false)

        println("param1")
        println(weatherData?.getJSONArray("weather")?.getJSONObject(0)?.get("description"))

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val weatherDescription = weatherData?.getJSONArray("weather")?.getJSONObject(0)
            ?.get("description").toString()
        val weatherTemp = weatherData?.getJSONObject("main")?.getString("temp")
            ?.split(".")?.first() + "\u2103"

        return AlertDialog.Builder(requireContext())
            .setMessage("$weatherDescription\n$weatherTemp")
            .setTitle(R.string.current_weather_title)
            .setIcon(BitmapDrawable(resources, weatherIcon))
            .setNeutralButton(R.string.weather_dialog_button) { dialog, _ -> dialog.cancel() }
            .create()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param weatherData Parameter 1.
         * @return A new instance of fragment WeatherDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(weatherData: String, weatherIcon: Bitmap) =
            WeatherDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_WEATHER_DATA, weatherData)
                    putParcelable(ARG_WEATHER_ICON, weatherIcon)
                }
            }
    }
}