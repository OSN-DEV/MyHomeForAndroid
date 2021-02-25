package jp.gr.java_conf.osndev.myhome.battery

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class BatteryJobService : JobService() {
    companion object {
        const val ACTION_GET_BATTERY = "jp.gr.java_conf.osndev.myhome.battery.BatteryJobService.action_get_battery"
        const val KEY_DATA = "jp.gr.java_conf.osndev.myhome.battery.BatteryJobService.key_battery"
        var TAG = BatteryJobService::class.simpleName ?: ""


        fun batteryJobServiceFactory(context: Context, jobId: Int, latency: Long = 0): JobInfo {
            val componentName = ComponentName(context, BatteryJobService::class.java)
            return JobInfo.Builder(jobId, componentName).apply {
                setPersisted(true)
                setMinimumLatency(latency * (1000 * 60))
                setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                setPeriodic(1 * 60 * 1000)
            }.build()
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob")
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob.")
        Thread(Runnable {
            val intent = Intent()
            intent.action = ACTION_GET_BATTERY
            intent.putExtra(KEY_DATA, BatteryDataRepo(applicationContext).getCurrent())
            sendBroadcast(intent)
        }).start()
        return false
    }
}