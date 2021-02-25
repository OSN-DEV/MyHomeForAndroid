package jp.gr.java_conf.osndev.myhome

import jp.gr.java_conf.osndev.myhome.battery.BatteryData
import jp.gr.java_conf.osndev.myhome.weather.WeatherData

interface IServiceReceiver {
    /**
     * 充電を開始した
     */
    fun onPowerConnected()

    /**
     * 充電が停止した
     */
    fun onPowerDisconnected()

    /**
     * バッテリー残量通知
     */
    fun onBatteryLevel(info: BatteryData)

    /**
     * 転記データ通知
     */
    fun onWeatherData(info: WeatherData)
}