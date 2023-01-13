package se.miun.sath2102.dt031g.bathingsites

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.miun.sath2102.dt031g.bathingsites.databinding.FragmentBathingSitesBinding

/**
 * Fragment containing the custom view BathingSitesView and its functionality.
 */
class BathingSitesFragment : Fragment() {

    private lateinit var binding: FragmentBathingSitesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentBathingSitesBinding.inflate(inflater, container, false)
        setBathingSiteViewOnClickListener()

        return binding.root
    }


    override fun onResume() {
        binding.bathingSitesView.setTextFromDatabase()
        super.onResume()
    }


    /**
     * Sets the listener for click events on the BathingSitesView.
     */
    private fun setBathingSiteViewOnClickListener() {
        binding.bathingSitesView.setOnClickListener {
            val intent = Intent(context, DisplayBathingSitesActivity::class.java)
            startActivity(intent)
        }
    }


}
