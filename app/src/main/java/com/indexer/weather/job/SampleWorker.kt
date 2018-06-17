package com.indexer.weather.job

import android.util.Log
import androidx.work.Worker

class SampleWorker : Worker() {
  override fun doWork(): WorkerResult {
    Log.e("Worker","check")
    return WorkerResult.RETRY
  }

}