package se.miun.sath2102.dt031g.bathingsites

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

    init {
        binding = BathingSitesViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

}