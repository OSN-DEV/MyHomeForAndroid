package jp.gr.java_conf.osndev.myhome.weather

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import jp.gr.java_conf.osndev.myhome.IServiceReceiver
import jp.gr.java_conf.osndev.myhome.battery.BatteryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherJobService : JobService(), IServiceReceiver {
    companion object {
        const val ACTION_GET_WEATHER =
            "jp.gr.java_conf.osndev.myhome.weather.WeatherJobService.action_get_weather"
        const val KEY_DATA = "jp.gr.java_conf.osndev.myhome.battery.BatteryJobService.key_info"
        var TAG = WeatherJobService::class.simpleName ?: ""

        fun weatherJobServiceFactory(context: Context, jobId: Int, latency: Long = 0): JobInfo {
            val componentName = ComponentName(context, WeatherJobService::class.java)
            return JobInfo.Builder(jobId, componentName).apply {
                setPersisted(true)
                setMinimumLatency(latency * (1000 * 60))
                setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                setPeriodic(15 * 60 * 1000)
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
            GlobalScope.launch(Dispatchers.Default) {
                WeatherDataRepo().getCurrent(this@WeatherJobService)
            }

        }).start()
        return false
    }

    override fun onPowerConnected() {
        TODO("Not yet implemented")
    }

    override fun onPowerDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onBatteryLevel(info: BatteryData) {
        TODO("Not yet implemented")
    }

    override fun onWeatherData(info: WeatherData) {
        Log.d(TAG, "onWeatherData.")
        val intent = Intent()
        intent.action = ACTION_GET_WEATHER
        intent.putExtra(KEY_DATA, info)
        sendBroadcast(intent)
    }
}