package com.indexer.weather.job

import androidx.work.Worker
import com.indexer.weather.utils.NotificationHelper

class FetchApiWorker : Worker() {
  override fun doWork(): WorkerResult {
    NotificationHelper(applicationContext).createNotification(
        "Weather", "Weather Information Updated "
    )
    return WorkerResult.RETRY
  }

}