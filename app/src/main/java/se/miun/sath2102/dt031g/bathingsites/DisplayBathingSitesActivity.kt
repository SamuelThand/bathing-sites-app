package se.miun.sath2102.dt031g.bathingsites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import se.miun.sath2102.dt031g.bathingsites.databinding.ActivityDisplayBathingSitesBinding
import kotlin.coroutines.CoroutineContext

class DisplayBathingSitesActivity : AppCompatActivity(), CoroutineScope {

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
            val adapter = BathingSiteRecyclerAdapter(this@DisplayBathingSitesActivity, bathingSites)
            binding.bathingSitesRecyclerView.adapter = adapter
            binding.bathingSitesRecyclerView.layoutManager = LinearLayoutManager(this@DisplayBathingSitesActivity)
            println(bathingSites)
        }


        setContentView(binding.root)
    }

}