package se.miun.sath2102.dt031g.bathingsites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import se.miun.sath2102.dt031g.bathingsites.databinding.ActivityMainBinding

/**
 * The main activity of the app.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFloatingActionButtonOnClickListener()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.main_menu_download -> {
                val intent = Intent(this, DownloadActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Set the event listener for the floating action button.
     */
    private fun setFloatingActionButtonOnClickListener() {
        binding.addBathingSiteButton.setOnClickListener {
            val intent = Intent(this, AddBathingSiteActivity::class.java)
            startActivity(intent)
        }
    }


}
