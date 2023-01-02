package se.miun.sath2102.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import se.miun.sath2102.dt031g.bathingsites.databinding.BathingSitesViewBinding
import kotlin.coroutines.CoroutineContext

class BathingSitesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), CoroutineScope {

    private val binding: BathingSitesViewBinding
    private var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        binding = BathingSitesViewBinding.inflate(LayoutInflater.from(context), this, true)
        job = Job()
    }

    @SuppressLint("SetTextI18n")
    fun setText() {
        launch {
            val storedBathingSites = BathingsiteDatabase.getInstance(context).BathingSiteDao().getAll().size
            binding.textView.text = "$storedBathingSites ${context.getString(R.string.bathing_sites_text)}"
        }
    }

    //TODO rename and implement new activity open
    fun incrementStoredBathingSites() {
//        storedBathingSites ++
    }
}