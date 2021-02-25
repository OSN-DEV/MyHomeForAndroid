package jp.gr.java_conf.osndev.myhome

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import jp.gr.java_conf.osndev.myhome.battery.BatteryData
import jp.gr.java_conf.osndev.myhome.battery.BatteryJobService
import jp.gr.java_conf.osndev.myhome.weather.WeatherData
import jp.gr.java_conf.osndev.myhome.weather.WeatherJobService

class ServiceReceiver() : BroadcastReceiver() {
    companion object {
        var TAG = ServiceReceiver::class.simpleName ?: ""
    }
    private var callback: IServiceReceiver? = null

    constructor(callback: IServiceReceiver):this() {
        this.callback = callback
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: " + intent.action)
        when(intent.action) {
            Intent.ACTION_POWER_CONNECTED -> callback?.onPowerConnected()
            Intent.ACTION_POWER_DISCONNECTED -> callback?.onPowerDisconnected()
            BatteryJobService.ACTION_GET_BATTERY -> {
                intent.extras?.getParcelable<BatteryData>(
                    BatteryJobService.KEY_DATA
                )?.let {
                    callback?.onBatteryLevel(it)
                }
            }
            WeatherJobService.ACTION_GET_WEATHER -> {
                intent.extras?.getParcelable<WeatherData>(
                    WeatherJobService.KEY_DATA
                )?.let {
                    callback?.onWeatherData(it)
                }
            }
        }
    }
}