package jp.gr.java_conf.osndev.myhome

import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.gr.java_conf.osndev.myhome.battery.BatteryData
import jp.gr.java_conf.osndev.myhome.battery.BatteryDataRepo
import jp.gr.java_conf.osndev.myhome.battery.BatteryJobService
import jp.gr.java_conf.osndev.myhome.weather.WeatherData
import jp.gr.java_conf.osndev.myhome.weather.WeatherDataRepo
import jp.gr.java_conf.osndev.myhome.weather.WeatherJobService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    IServiceReceiver {
    private lateinit var _batteryReceiver: ServiceReceiver

    companion object {
        private const val latency: Int = 0
        private var batteryScheduler: JobScheduler? = null
        private var weatherScheduler: JobScheduler? = null
        var TAG = MainActivity::class.simpleName ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // setup job
        this.setupBatteryInfo()
        this.setupWeatherInfo()

        // show current
        this.showCurrentInfo()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        fun setVal(key: String, id: Int) {
            outState.putString(key, findViewById<TextView>(id).text.toString())
        }
        setVal("k0", R.id.weather)
        setVal("k1", R.id.rainy_p1)
        setVal("k2", R.id.rainy_p2)
        setVal("k3", R.id.rainy_p3)
        setVal("k4", R.id.rainy_p4)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        fun setVal(key: String, id: Int) {
            findViewById<TextView>(id).text = savedInstanceState.getString(key)
        }
        setVal("k0", R.id.weather)
        setVal("k1", R.id.rainy_p1)
        setVal("k2", R.id.rainy_p2)
        setVal("k3", R.id.rainy_p3)
        setVal("k4", R.id.rainy_p4)
    }

    override fun onPowerConnected() {
        Log.d(TAG, "Connected.")
        findViewById<ImageView>(R.id.charging).visibility = View.VISIBLE
    }

    override fun onPowerDisconnected() {
        Log.d(TAG, "Disconnected.")
        findViewById<ImageView>(R.id.charging).visibility = View.INVISIBLE
    }

    override fun onBatteryLevel(info: BatteryData) {
        Log.d(TAG, "BatteryLevel.")
        findViewById<ImageView>(R.id.charging).visibility =
            if (info.isCharging) View.VISIBLE else View.INVISIBLE
        findViewById<TextView>(R.id.battery_level).text = info.level
    }

    override fun onWeatherData(info: WeatherData) {
        Log.d(TAG, "onWeatherData.")
        runOnUiThread {
            fun setText(id: Int, text: String) {
                val target = findViewById<TextView>(id)
                target.text = text
                target.invalidate()
            }
            setText(R.id.weather, info.weather)
            setText(R.id.rainy_p1, info.rainyP1)
            setText(R.id.rainy_p2, info.rainyP2)
            setText(R.id.rainy_p3, info.rainyP3)
            setText(R.id.rainy_p4, info.rainyP4)
        }
    }

    /**
     * setup battery receiver
     */
    private fun setupBatteryInfo() {
        this._batteryReceiver =
            ServiceReceiver(this)
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(BatteryJobService.ACTION_GET_BATTERY)
        filter.addAction(WeatherJobService.ACTION_GET_WEATHER)

        applicationContext.registerReceiver(this._batteryReceiver, filter)

        if (null == batteryScheduler) {
            batteryScheduler =
                applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            batteryScheduler?.schedule(
                BatteryJobService.batteryJobServiceFactory(
                    applicationContext,
                    0,
                    latency.toLong()
                )
            )
        }
    }

    /**
     * setup weather receiver
     */
    private fun setupWeatherInfo() {
        if (null == weatherScheduler) {
            weatherScheduler =
                applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            weatherScheduler?.schedule(
                WeatherJobService.weatherJobServiceFactory(
                    applicationContext,
                    1,
                    latency.toLong()
                )
            )
        }
    }

    /**
     * show current info
     */
    private fun showCurrentInfo() {
        this.onBatteryLevel(BatteryDataRepo(applicationContext).getCurrent())
        GlobalScope.launch(Dispatchers.Default) {
            WeatherDataRepo().getCurrent(this@MainActivity)
        }
    }
}