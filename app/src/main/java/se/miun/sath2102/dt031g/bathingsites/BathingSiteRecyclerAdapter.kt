package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.miun.sath2102.dt031g.bathingsites.databinding.RecyclerViewRowBinding

class BathingSiteRecyclerAdapter(context: Context, bathingSites: List<BathingSite>)
    : RecyclerView.Adapter<BathingSiteRecyclerAdapter.ViewHolder>() {

    private val context: Context
    private var bathingSites: List<BathingSite>

    init {
        this.context = context
        this.bathingSites = bathingSites
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate layout give look to rows
        val binding = RecyclerViewRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

//        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // assign values to views in the row layout file
        holder.binding.title.text = bathingSites[position].name
//        holder.
    }

    override fun getItemCount(): Int {
        // Totalt antal views
        return bathingSites.size
    }


    class ViewHolder(val binding: RecyclerViewRowBinding) : RecyclerView.ViewHolder(binding.root) {

        private var image: ImageView = binding.imageView
        private var title: TextView = binding.title
    }
}