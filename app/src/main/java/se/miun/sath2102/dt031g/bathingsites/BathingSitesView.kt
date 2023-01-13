package se.miun.sath2102.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import se.miun.sath2102.dt031g.bathingsites.databinding.BathingSitesViewBinding
import kotlin.coroutines.CoroutineContext

/**
 * Custom view that displays the amount of stored BathingSite objects.
 *
 * Usage of the @JvmOverloads annotation for overloading the constructor
 * was learned from:
 *
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-overloads/
 * https://medium.com/android-news/demystifying-the-jvmoverloads-in-kotlin-10dd098e6f72
 * https://stackoverflow.com/questions/20670828/how-to-create-constructor-of-custom-view-with-kotlin
 */
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


    /**
     * Gets the amount of stored BathingSites from the BathingSiteDatabase in the
     * coroutine-scope and sets the text of the this view to include this number.
     */
    @SuppressLint("SetTextI18n")
    fun setTextFromDatabase() {
        launch {
            val storedBathingSites = BathingsiteDatabase.getInstance(context).BathingSiteDao().getAll().size
            binding.textView.text = "$storedBathingSites ${context.getString(R.string.bathing_sites_text)}"
        }
    }


}
