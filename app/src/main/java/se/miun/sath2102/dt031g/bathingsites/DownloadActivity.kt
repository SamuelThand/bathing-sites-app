package se.miun.sath2102.dt031g.bathingsites

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.DownloadListener
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import se.miun.sath2102.dt031g.bathingsites.databinding.ActivityDownloadBinding
import java.io.File
import java.net.URL
import java.net.URLConnection
import kotlin.coroutines.CoroutineContext

class DownloadActivity : AppCompatActivity(), DownloadListener, CoroutineScope {

    private val TAG = "DownloadActivity"
    private lateinit var binding: ActivityDownloadBinding
    private lateinit var job: Job
    private lateinit var bathingsiteDatabase: BathingsiteDatabase
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bathingsiteDatabase = BathingsiteDatabase.getInstance(this)
        job = Job()
        initializeWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {
        val webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }

        webView.setDownloadListener(this)
        SettingsActivity.getBathingSiteURL(this)?.let {
            webView.loadUrl(it)
        }
    }


    override fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimetype: String?,
        contentLength: Long
    ) {
        url?.run {
            val fileName = url.substringAfterLast("/").substringBeforeLast(".")
            val downloadPath = File(this@DownloadActivity.filesDir, fileName)

            if (!downloadPath.exists()) {
                launch (Dispatchers.IO) {
                    val progress = makeProgressDialog(fileName)

                    try {
                        downloadFile(this@run, downloadPath)
                        incrementProgress(progress,33)

                        val newBathingSites = buildBathingSiteListFromFile(downloadPath)
                        incrementProgress(progress,33)

                        bathingsiteDatabase.BathingSiteDao().insertList(newBathingSites)
                        incrementProgress(progress,34)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error in download chain for $downloadPath: $e")
                        val errorSnackbar = Snackbar.make(binding.webView,R.string.unexpected_bathing_site_download_error,
                            BaseTransientBottomBar.LENGTH_LONG
                        )
                        errorSnackbar.show()
                    } finally {
                        downloadPath.delete()
                        progress.dismiss()
                    }
                }

            } else {
                val alreadyDownloadedSnackbar = Snackbar.make(binding.webView, R.string.already_downloaded_snackbar_text,
                    BaseTransientBottomBar.LENGTH_LONG
                )
                alreadyDownloadedSnackbar.show()
            }

        }
    }


    private fun downloadFile(url: String, downloadPath: File) {
        val downloadConnection = URL(url).openConnection()
        downloadConnection.connect()

        downloadPath.downloadToThisPath(downloadConnection)
    }

    private fun buildBathingSiteListFromFile(file: File): List<BathingSite> {
        val newBathingSites = mutableListOf<BathingSite>()

        file.inputStream().bufferedReader().forEachLine {

            val bathingSiteData = it.substring(1).split(',')
            val longitude = bathingSiteData[0].stripUnwantedCharacters().toDouble()
            val latitude = bathingSiteData[1].stripUnwantedCharacters().toDouble()
            val name = bathingSiteData[2].stripUnwantedCharacters()
            val address: String? = when {
                bathingSiteData.lastIndex == 3 -> bathingSiteData[3].stripUnwantedCharacters()
                bathingSiteData.lastIndex >= 4 -> (bathingSiteData[3] + "," + bathingSiteData[4]).stripUnwantedCharacters()
                else -> null
            }

            newBathingSites.add(
                BathingSite(
                    id = null,
                    name = name,
                    description = null,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    waterTemp = null,
                    waterTempDate = null,
                    grade = 2.5f
                ))
        }

        return newBathingSites
    }

    private fun String.stripUnwantedCharacters(): String {
        val unwantedCharacters = setOf('"', "\uFEFF")

        return this.filterNot { it in unwantedCharacters }.trim()
    }

    private fun File.downloadToThisPath(connection: URLConnection) {
        this.outputStream().use { fileOutput ->
            connection.getInputStream().copyTo(fileOutput)
        }
    }

    private suspend fun makeProgressDialog(fileName: String): ProgressDialog {
        return withContext(Dispatchers.Main) {
            val progress = ProgressDialog(this@DownloadActivity)
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progress.setTitle(getString(R.string.download_bathing_site_progress_title))
            progress.setMessage(fileName)
            progress.show()

            return@withContext progress
        }
    }

    private suspend fun incrementProgress(progressDialog: ProgressDialog, amount: Int) {
        withContext(Dispatchers.Main) {
            delay(200)
            progressDialog.incrementProgressBy(amount)
        }
    }

}