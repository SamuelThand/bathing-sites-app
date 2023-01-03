package se.miun.sath2102.dt031g.bathingsites

import android.app.ProgressDialog
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.json.JSONObject
import se.miun.sath2102.dt031g.bathingsites.databinding.FragmentAddBathingSiteBinding
import java.net.URL
import java.time.LocalDate
import kotlin.coroutines.CoroutineContext

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
    private lateinit var weatherData: String
    private lateinit var bathingsiteDatabase: BathingsiteDatabase
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

        context?.let {
            bathingsiteDatabase = BathingsiteDatabase.getInstance(it)
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
    ): View {
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
        inflater.inflate(R.menu.add_bathing_site_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.add_bathing_site_menu_clear -> {
                clearForm()
                return true
            }

            R.id.add_bathing_site_menu_save -> {
                determineRequiredFields()
                validateInput()
                if (completeForm()) {
                    launch {
                        try {
                            saveBathingSiteToDatabase()
                            displaySnackbar(getString(R.string.bathing_site_saved))
                            delay(1000)
                            clearForm()
                            activity?.finish()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving entity to database: $e")
                            when(e) {
                                is SQLiteConstraintException ->
                                    displaySnackbar(getString(R.string.bathing_site_already_exists_error))
                                else ->
                                    displaySnackbar(getString(R.string.unexpected_error))
                            }
                        }
                    }
                }

                return true
            }

            R.id.add_bathing_site_menu_show_weather -> {
                launch {
                    if (validateLocationData()) {
                        val iconCode = JSONObject(weatherData).getJSONArray("weather")
                            .getJSONObject(0).getString("icon")
                        val weatherIcon: Bitmap = downloadWeatherIcon(iconCode)
                        val dialog = WeatherDialogFragment.newInstance(weatherData, weatherIcon)
                        dialog.show(childFragmentManager, "WeatherFragment")
                    }
                }

                return true
            }

            R.id.add_bathing_site_menu_settings -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                activity?.startActivity(intent)

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Throws(SQLiteConstraintException::class)
    private suspend fun saveBathingSiteToDatabase() {
        withContext(Dispatchers.IO) {

            val bathingSiteDao = bathingsiteDatabase.BathingSiteDao()

            val name = binding.name.text
            val description = binding.description.text
            val address = binding.address.text
            val lat = binding.latitude.text
            val long = binding.longitude.text
            val waterTemp = binding.waterTemp.text
            val waterTempDate = binding.waterTempDate.text
            val grade = binding.grade

            bathingSiteDao.insertAll(BathingSite(
                id = null,
                name = name.toString(),
                description = if (description.isEmpty()) null else description.toString(),
                address = if (address.isEmpty()) null else address.toString(),
                latitude = if (lat.isEmpty()) null else lat.toString().toDouble(),
                longitude = if (long.isEmpty()) null else long.toString().toDouble(),
                waterTemp = if (waterTemp.isEmpty()) null else waterTemp.toString().toDouble(),
                waterTempDate = if (waterTempDate.isEmpty()) null else waterTempDate.toString(),
                grade = grade.rating
            ))
        }
    }

    private suspend fun validateLocationData(): Boolean {

        return withContext(Dispatchers.IO) {

            val address = binding.address.text
            val lat = binding.latitude.text
            val long = binding.longitude.text
            val addressProvided: Boolean = address.isNotEmpty()
            val coordinatesProvided: Boolean = lat.isNotEmpty() && long.isNotEmpty()

            if (addressProvided && coordinatesProvided || coordinatesProvided) {

                val validWeatherData = getWeatherData("lat=$lat&lon=$long")

                if (!validWeatherData) {
                    displaySnackbar(createWeatherDataErrorMessage())
                    return@withContext false
                } else {
                    return@withContext true
                }

            } else if (addressProvided) {

                val validWeatherData = getWeatherData("q=$address")

                if (!validWeatherData) {
                    displaySnackbar(createWeatherDataErrorMessage())
                    return@withContext false
                } else {
                    return@withContext true
                }

            } else {
                displaySnackbar(getString(R.string.cant_download_weather_data))
                return@withContext false
            }
        }
    }

    private suspend fun getWeatherData(queryString: String): Boolean {
        val progress = makeProgressDialog()
        weatherData = downloadWeatherData(queryString)
        delay(1000)
        progress.dismiss()

        val statusCode = JSONObject(weatherData).get("cod")
        return statusCode == 200
    }

    private suspend fun makeProgressDialog(): ProgressDialog {
        return withContext(Dispatchers.Main) {
            val progress = ProgressDialog(context)
            progress.setMessage(getString(R.string.download_weather_progress_message))
            progress.show()

            return@withContext progress
        }
    }

    private fun displaySnackbar(messageToDisplay: String) {
        val snackBar = Snackbar.make(binding.root, messageToDisplay, BaseTransientBottomBar.LENGTH_LONG)
        snackBar.show()
    }

    private fun createWeatherDataErrorMessage(): String {
        return (JSONObject(weatherData).get("message") as String).replaceFirstChar {
                firstChar -> firstChar.uppercase()
        }
    }

    private suspend fun downloadWeatherIcon(queryString: String): Bitmap {
        return withContext(Dispatchers.IO) {
                val iconURL = getString(R.string.icon_url)
                val queryURL = "$iconURL$queryString.png"

            try {
                val iconConnection = URL(queryURL).openConnection()
                iconConnection.connect()
                iconConnection.getInputStream().use { input ->
                    return@withContext BitmapFactory.decodeStream(input)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error downloading $queryString: $e")
                return@withContext BitmapFactory.decodeResource(resources, R.drawable.ic_error)
            }
        }
    }

    private fun downloadWeatherData(queryString: String): String {
        context?.let { it ->
            val weatherURL = SettingsActivity.getWeatherURL(it)
            val queryURL = "$weatherURL?$queryString"

            try {
                val weatherConnection = URL(queryURL).openConnection()
                weatherConnection.connect()

                return weatherConnection.getInputStream().bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading $queryString: $e")

                return "error"
            }
        }

        return ""
    }

    private fun validateInput() {
        inputFields.forEach {
            val field = it.key
            val fieldName = resources.getResourceName(field.id)
                .split("/").last().replaceFirstChar { char -> char.uppercaseChar() }
            val required: Boolean = it.value

            if (required) {
                if (field.text.isEmpty()) {
                    field.error = "$fieldName is required."
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