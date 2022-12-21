package se.miun.sath2102.dt031g.bathingsites

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import se.miun.sath2102.dt031g.bathingsites.databinding.FragmentAddBathingSiteBinding
import java.io.File
import java.net.URL
import java.net.URLConnection
import java.time.LocalDate
import kotlin.coroutines.CoroutineContext

// TODO: Fixa scrollbar
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddBathingSiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBathingSiteFragment : Fragment(), CoroutineScope {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAddBathingSiteBinding
    private lateinit var inputFields: MutableMap<EditText, Boolean>
    private val TAG = "AddBathingSiteFragment"

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddBathingSiteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddBathingSiteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddBathingSiteBinding.inflate(inflater, container, false)

        // True if field is required, false if not
        inputFields = mutableMapOf(
            Pair(binding.name, true),
            Pair(binding.description, false),
            Pair(binding.address, true),
            Pair(binding.latitude, true),
            Pair(binding.longitude, true),
            Pair(binding.waterTemp, false),
            Pair(binding.waterTempDate, false),
        )

        setBathingSiteDateToToday()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_bathing_site_view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_bathing_site_menu_clear -> {
                clearForm()
                true
            }
            R.id.add_bathing_site_menu_save -> {
                determineRequiredFields()
                validateInput()
                if (completeForm()) {
                    displayBathingSiteInfo(buildInfoString())
                }
                true
            }
            R.id.add_bathing_site_menu_show_weather -> {

                // Visa bara fragment om hämtningen var successful
                // TODO kör hanteringen av knapptryckningen i coroutine på nått sätt utan att låsa UI

                if (runBlocking { getWeatherData() }) {
                    val dialog = WeatherDialogFragment()
                    dialog.show(childFragmentManager, "WeatherFragment")
                    true
                } else {
                    return false
                }

            }
            R.id.add_bathing_site_menu_settings -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                activity?.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun getWeatherData(): Boolean {

        val address = binding.address.text
        val lat = binding.latitude.text
        val long = binding.longitude.text
        val addressProvided: Boolean = address.isNotEmpty()
        val coordinatesProvided: Boolean = lat.isNotEmpty() && long.isNotEmpty()

//      TODO progressbar, sen starta weatherdialogfragment med väderdatan
        if (addressProvided && coordinatesProvided) {
            job = launch (Dispatchers.IO) {
                val progress = makeProgressDialog()
                downloadWeatherData("lat=$lat&lon=$long")
                delayProgress()
                progress.dismiss()
            }

            job.join()
            return true

        } else if (coordinatesProvided) {
            launch (Dispatchers.IO) {
                downloadWeatherData("lat=$lat&lon=$long")
            }
            return true
        } else if (addressProvided) {
            launch (Dispatchers.IO) {
                downloadWeatherData("q=$address")
            }
            return true
        } else {
            val cantGetWeatherInfoSnackbar = Snackbar.make(binding.root, R.string.cant_download_weather_data,
                BaseTransientBottomBar.LENGTH_LONG
            )
            cantGetWeatherInfoSnackbar.show()

            return false
        }

    }

    private suspend fun delayProgress() {
        withContext(Dispatchers.Main) {
            delay(1000)
        }
    }

    private suspend fun makeProgressDialog(): ProgressDialog {
        return withContext(Dispatchers.Main) {
            val progress = ProgressDialog(context)
            progress.setMessage(getString(R.string.download_weather_progress_message))
            progress.show()

            return@withContext progress
        }
    }

//            lat=13.3&lon=63.456
    private fun downloadWeatherData(queryString: String) {

        context?.let { it ->
            val weatherURL = SettingsActivity.getWeatherURL(it)
            val queryURL = "$weatherURL?$queryString"

            //TODO Translate spaces, åäö to url format

            try {
                val weatherConnection = URL(queryURL).openConnection()
                weatherConnection.connect()

                val weatherString: String = weatherConnection.getInputStream()
                    .bufferedReader().use { it.readText() }

                println(weatherString)

            } catch (e: Exception) {
                Log.e(TAG, "Error downloading $queryString: $e")
            }

            //TODO öppna inputstream och läs svaret som en sträng, konvertera till JSON
            // TODO Visa ut datan eller printat ut meddelandet att datan inte kunde hämtas

        }
    }

    private fun validateInput() {
        inputFields.forEach {
            val field = it.key
            val fieldName = resources.getResourceName(field.id)
                .split("/").last().replaceFirstChar { char -> char.uppercaseChar() }
            val required: Boolean = it.value

            if (required) {
                if (field.text.isEmpty()) {
                    field.error = "${fieldName} is required."
                }
            } else {
                field.error = null
            }
        }
    }

    private fun determineRequiredFields() {
        val addressText = binding.address.text
        val latText = binding.latitude.text
        val longText = binding.longitude.text

        if (addressText.isNotEmpty()) {
            inputFields[binding.latitude] = false
            inputFields[binding.longitude] = false
        } else if (latText.isNotEmpty() || longText.isNotEmpty()) {
            inputFields[binding.address] = false
        } else {
            inputFields[binding.address] = true
            inputFields[binding.latitude] = true
            inputFields[binding.longitude] = true
        }
    }

    private fun displayBathingSiteInfo(infoString: String) {
            context?.let {
                val alertDialogBuilder = AlertDialog.Builder(it)
                alertDialogBuilder.setMessage(infoString)
                alertDialogBuilder.show()
            }
    }

    private fun completeForm(): Boolean {
        var completeForm = true
        inputFields.forEach {
            val field = it.key

            if (field.error != null) {
                completeForm = false
            }
        }

        return completeForm
    }

    private fun buildInfoString(): String {
        var bathingSiteInfo = ""

        inputFields.forEach {
            val field = it.key
            val fieldName = resources.getResourceName(field.id)
                .split("/").last().replaceFirstChar { char -> char.uppercaseChar() }

            bathingSiteInfo += "${fieldName}: ${field.text}\n"
        }

        val gradeField = binding.grade
        val gradeFieldName = resources.getResourceName(binding.grade.id)
            .split("/").last().replaceFirstChar { char -> char.uppercaseChar() }
        bathingSiteInfo += "${gradeFieldName}: ${gradeField.rating}\n"

        return bathingSiteInfo
    }

    private fun setBathingSiteDateToToday() {
        binding.waterTempDate.setText(LocalDate.now().toString())
    }

    private fun clearForm() {
        inputFields.forEach {
            val field = it.key
            field.text.clear()
        }
        binding.grade.rating = 0F
        setBathingSiteDateToToday()
    }
}