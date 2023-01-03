package se.miun.sath2102.dt031g.bathingsites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import se.miun.sath2102.dt031g.bathingsites.databinding.ActivityDisplayBathingSitesBinding
import kotlin.coroutines.CoroutineContext

class DisplayBathingSitesActivity : AppCompatActivity(), RecyclerViewInterface, CoroutineScope {

    private lateinit var binding: ActivityDisplayBathingSitesBinding
    private lateinit var bathingSites: List<BathingSite>
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        binding = ActivityDisplayBathingSitesBinding.inflate(layoutInflater)

        launch {
            bathingSites = BathingsiteDatabase.
            getInstance(this@DisplayBathingSitesActivity).BathingSiteDao().getAll()
            val adapter = BathingSiteRecyclerAdapter(
                this@DisplayBathingSitesActivity,
                bathingSites,
                this@DisplayBathingSitesActivity)
            binding.bathingSitesRecyclerView.adapter = adapter
            binding.bathingSitesRecyclerView.layoutManager = LinearLayoutManager(this@DisplayBathingSitesActivity)
            println(bathingSites)
        }

        setContentView(binding.root)
    }


    override fun onClick(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val clickedBathingSite = bathingSites[position]
        alertDialogBuilder.setMessage(
            "Name: ${clickedBathingSite.name}\n" +
            "Description: ${clickedBathingSite.description}\n" +
            "Address: ${clickedBathingSite.address}\n" +
            "Longitude: ${clickedBathingSite.longitude}\n" +
            "Latitude: ${clickedBathingSite.latitude}\n" +
            "Grade: ${clickedBathingSite.grade}\n" +
            "Water temp: ${clickedBathingSite.waterTemp}\n" +
            "Date for temp: ${clickedBathingSite.waterTempDate}\n"
        )
        alertDialogBuilder.show()
    }


}
