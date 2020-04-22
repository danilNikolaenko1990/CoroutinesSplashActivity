package com.danilniko.progressBarSample.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.danilniko.progressBarSample.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class SplashActivity : AppCompatActivity() {
    private var crazyComputationJob: Job? = null
    private var progressReadingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_splash)
        val reportedProgress = Channel<Int>()
        startCrazyComputation(reportedProgress)

        progressReadingJob = GlobalScope.launch(Dispatchers.Main) {
            for (percentage in reportedProgress) {
                splash_progressBar.progress = percentage
            }

            startNextActivity()
        }
        super.onCreate(savedInstanceState)
    }

    private fun startCrazyComputation(reportedProgress: Channel<Int>) {
        crazyComputationJob = GlobalScope.launch(Dispatchers.IO) {
            performComputation(reportedProgress)
        }
    }

    private suspend fun performComputation(reportedProgress: Channel<Int>) {
        val piecesOfJob = 2000

        for (i in 0 until piecesOfJob) {
            delay(1)
            reportedProgress.send((i.toDouble() / piecesOfJob * PERCENTS).toInt())
        }

        reportedProgress.close()
    }

    private fun startNextActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        crazyComputationJob?.cancel()
        progressReadingJob?.cancel()
        super.onDestroy()
    }

    companion object {
        const val PERCENTS = 100
    }
}
