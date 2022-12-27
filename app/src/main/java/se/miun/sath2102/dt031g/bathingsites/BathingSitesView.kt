package se.miun.sath2102.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import se.miun.sath2102.dt031g.bathingsites.databinding.BathingSitesViewBinding

class BathingSitesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: BathingSitesViewBinding
    private var storedBathingSites = 0

    init {
        binding = BathingSitesViewBinding.inflate(LayoutInflater.from(context), this, true)
        setText()
    }

    @SuppressLint("SetTextI18n")
    fun setText() {
        binding.textView.text = "$storedBathingSites ${context.getString(R.string.bathing_sites_text)}"
    }

    fun incrementStoredBathingSites() {
        storedBathingSites ++
    }
}