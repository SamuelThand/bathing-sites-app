package se.miun.sath2102.dt031g.bathingsites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import se.miun.sath2102.dt031g.bathingsites.databinding.ActivityDisplayBathingSitesBinding

class DisplayBathingSitesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayBathingSitesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayBathingSitesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}