package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.miun.sath2102.dt031g.bathingsites.databinding.RecyclerViewRowBinding

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


    class ViewHolder(
        val binding: RecyclerViewRowBinding,
        private val recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(binding.root) {

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