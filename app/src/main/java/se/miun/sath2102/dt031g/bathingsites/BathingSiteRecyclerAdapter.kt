package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import se.miun.sath2102.dt031g.bathingsites.databinding.RecyclerViewRowBinding

/**
 * Adapter class responsible for providing data to the DisplayBathingSitesActivity's recycler view,
 * and create the view holders for this data.
 *
 * Recyclerview concepts was learned from the video https://www.youtube.com/watch?v=Mc0XT58A1Z4
 */
class BathingSiteRecyclerAdapter(
    val context: Context,
    val bathingSites: List<BathingSite>,
    private val recyclerViewInterface: RecyclerViewInterface) : RecyclerView.Adapter<BathingSiteRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = bathingSites[position].name
    }

    override fun getItemCount(): Int {
        return bathingSites.size
    }


    /**
     * Holds references to the view items that display data in DisplayBathingSitesActivity's recycler view.
     */
    class ViewHolder(
        val binding: RecyclerViewRowBinding,
        private val recyclerViewInterface: RecyclerViewInterface)
        : RecyclerView.ViewHolder(binding.root) {


        /**
         * Sets the recyclerViewInterface to handle click events for the view item in
         * DisplayBathingSitesActivity's recycler view if the ViewHolder has a valid position
         * in the adapter.
         */
        init {
            binding.root.setOnClickListener {
                recyclerViewInterface.let {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onClick(adapterPosition)
                    }
                }
            }
        }


    }


}
